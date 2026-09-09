package com.onter.onter_ic2.block.generators;

import com.onter.onter_ic2.energy.IC2EnergyStorage;
import com.onter.onter_ic2.init.ModBlockEntities;
import com.onter.onter_ic2.item.BatteryItem;
import com.onter.onter_ic2.menu.GeneratorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class GeneratorBlockEntity extends BlockEntity implements MenuProvider {
    public static final int SLOT_FUEL = 0;
    public static final int SLOT_CHARGE = 1;

    private final ItemStackHandler itemHandler = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot == SLOT_FUEL) return stack.getBurnTime(RecipeType.SMELTING) > 0;
            if (slot == SLOT_CHARGE) return stack.getItem() instanceof BatteryItem;
            return true;
        }
    };

    private final IC2EnergyStorage energyStorage = new IC2EnergyStorage(40000, 0, 512, this::setChanged);
    private int burnTime = 0;
    private int maxBurnTime = 0;

    protected final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> burnTime;
                case 1 -> maxBurnTime;
                case 2 -> energyStorage.getEnergyStored();
                case 3 -> energyStorage.getMaxEnergyStored();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> burnTime = value;
                case 1 -> maxBurnTime = value;
                case 2 -> energyStorage.setEnergy(value);
                case 3 -> energyStorage.setCapacity(value);
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    public GeneratorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.GENERATOR.get(), pos, blockState);
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    public IEnergyStorage getEnergyStorage() {
        return energyStorage;
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
        return Component.translatable("block.onter_ic2.generator");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new GeneratorMenu(containerId, playerInventory, this, this.dataAccess);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;

        boolean wasLit = state.hasProperty(BlockStateProperties.LIT) && state.getValue(BlockStateProperties.LIT);
        boolean isBurning = burnTime > 0;

        if (burnTime > 0) {
            burnTime--;
            energyStorage.produceEnergy(40); // 40 FE/t = 10 EU/t
            setChanged();
        }

        if (burnTime <= 0 && energyStorage.getEnergyStored() < energyStorage.getMaxEnergyStored()) {
            ItemStack fuelStack = itemHandler.getStackInSlot(SLOT_FUEL);
            if (!fuelStack.isEmpty()) {
                int fuelValue = fuelStack.getBurnTime(RecipeType.SMELTING);
                if (fuelValue > 0) {
                    burnTime = fuelValue;
                    maxBurnTime = fuelValue;
                    fuelStack.shrink(1);
                    isBurning = true;
                    setChanged();
                }
            }
        }

        // Charge battery in slot
        ItemStack chargeStack = itemHandler.getStackInSlot(SLOT_CHARGE);
        if (!chargeStack.isEmpty()) {
            net.neoforged.neoforge.energy.IEnergyStorage itemCap = chargeStack.getCapability(net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage.ITEM, null);
            if (itemCap != null && itemCap.canReceive()) {
                int toSend = Math.min(energyStorage.getEnergyStored(), 1000);
                int accepted = itemCap.receiveEnergy(toSend, false);
                if (accepted > 0) {
                    energyStorage.consumeEnergy(accepted);
                    setChanged();
                }
            } else if (chargeStack.getItem() instanceof BatteryItem battery) {
                int canSend = Math.min(energyStorage.getEnergyStored(), battery.getMaxTransfer());
                if (canSend > 0) {
                    int received = BatteryItem.receiveEnergy(chargeStack, canSend, battery.getCapacity(), battery.getMaxTransfer(), false);
                    if (received > 0) {
                        energyStorage.consumeEnergy(received);
                        setChanged();
                    }
                }
            }
        }

        if (wasLit != isBurning && state.hasProperty(BlockStateProperties.LIT)) {
            level.setBlock(pos, state.setValue(BlockStateProperties.LIT, isBurning), 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", itemHandler.serializeNBT(registries));
        tag.put("Energy", energyStorage.serializeNBT(registries));
        tag.putInt("BurnTime", burnTime);
        tag.putInt("MaxBurnTime", maxBurnTime);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Inventory")) itemHandler.deserializeNBT(registries, tag.getCompound("Inventory"));
        if (tag.contains("Energy")) energyStorage.deserializeNBT(registries, tag.getCompound("Energy"));
        burnTime = tag.getInt("BurnTime");
        maxBurnTime = tag.getInt("MaxBurnTime");
    }
}
