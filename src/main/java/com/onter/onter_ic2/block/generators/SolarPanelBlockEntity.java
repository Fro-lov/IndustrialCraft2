package com.onter.onter_ic2.block.generators;

import com.onter.onter_ic2.energy.IC2EnergyStorage;
import com.onter.onter_ic2.item.BatteryItem;
import com.onter.onter_ic2.menu.SolarPanelMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class SolarPanelBlockEntity extends BlockEntity implements MenuProvider {
    public static final int CHARGE_SLOTS = 4;

    private final int dayGen;
    private final int nightGen;
    private final IC2EnergyStorage energyStorage;
    private int currentGenRate = 0;
    private boolean isSunVisible = false;

    private final ItemStackHandler itemHandler = new ItemStackHandler(CHARGE_SLOTS) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.getItem() instanceof BatteryItem;
        }
    };

    protected final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> energyStorage.getEnergyStored() & 0xFFFF;
                case 1 -> (energyStorage.getEnergyStored() >> 16) & 0xFFFF;
                case 2 -> energyStorage.getMaxEnergyStored() & 0xFFFF;
                case 3 -> (energyStorage.getMaxEnergyStored() >> 16) & 0xFFFF;
                case 4 -> dayGen;
                case 5 -> nightGen;
                case 6 -> isSunVisible ? 1 : 0;
                case 7 -> currentGenRate;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            // Handled on server
        }

        @Override
        public int getCount() {
            return 8;
        }
    };

    public SolarPanelBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState,
                                 int dayGen, int nightGen, int capacity) {
        super(type, pos, blockState);
        this.dayGen = dayGen;
        this.nightGen = nightGen;
        this.energyStorage = new IC2EnergyStorage(capacity, 0, Math.max(dayGen * 2, 1000), this::setChanged);
    }

    public int getDayGen() {
        return dayGen;
    }

    public int getNightGen() {
        return nightGen;
    }

    public IEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    public ContainerData getDataAccess() {
        return dataAccess;
    }

    public NonNullList<ItemStack> getDrops() {
        NonNullList<ItemStack> drops = NonNullList.create();
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                drops.add(stack);
            }
        }
        return drops;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(getBlockState().getBlock().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new SolarPanelMenu(containerId, playerInventory, this, this.dataAccess);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;

        boolean canSeeSky = level.canSeeSky(pos.above());
        isSunVisible = canSeeSky;
        int gen = 0;

        if (canSeeSky) {
            boolean isDay = level.isDay() && !level.isRaining();
            gen = isDay ? dayGen : nightGen;
            if (gen > 0) {
                energyStorage.produceEnergy(gen);
            }
        }
        currentGenRate = gen;

        // Charge batteries in charging slots
        int slots = itemHandler.getSlots();
        for (int i = 0; i < Math.min(CHARGE_SLOTS, slots); i++) {
            if (energyStorage.getEnergyStored() <= 0) break;
            ItemStack batteryStack = itemHandler.getStackInSlot(i);
            if (!batteryStack.isEmpty() && batteryStack.getItem() instanceof BatteryItem battery) {
                int canSend = Math.min(energyStorage.getEnergyStored(), battery.getMaxTransfer());
                if (canSend > 0) {
                    int received = BatteryItem.receiveEnergy(batteryStack, canSend, battery.getCapacity(), battery.getMaxTransfer(), false);
                    if (received > 0) {
                        energyStorage.consumeEnergy(received);
                        setChanged();
                    }
                }
            }
        }

        // Auto push to adjacent blocks
        if (energyStorage.getEnergyStored() > 0) {
            for (Direction dir : Direction.values()) {
                IEnergyStorage adjacent = level.getCapability(Capabilities.EnergyStorage.BLOCK, pos.relative(dir), dir.getOpposite());
                if (adjacent != null && adjacent.canReceive()) {
                    int toSend = Math.min(energyStorage.getEnergyStored(), Math.max(dayGen * 2, 1000));
                    int accepted = adjacent.receiveEnergy(toSend, false);
                    if (accepted > 0) {
                        energyStorage.consumeEnergy(accepted);
                        if (energyStorage.getEnergyStored() <= 0) break;
                    }
                }
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", itemHandler.serializeNBT(registries));
        tag.put("Energy", energyStorage.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Inventory")) {
            itemHandler.deserializeNBT(registries, tag.getCompound("Inventory"));
            if (itemHandler.getSlots() < CHARGE_SLOTS) {
                itemHandler.setSize(CHARGE_SLOTS);
            }
        }
        if (tag.contains("Energy")) energyStorage.deserializeNBT(registries, tag.getCompound("Energy"));
    }
}
