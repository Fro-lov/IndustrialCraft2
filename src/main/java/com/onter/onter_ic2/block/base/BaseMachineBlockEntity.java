package com.onter.onter_ic2.block.base;

import com.onter.onter_ic2.energy.IC2EnergyStorage;
import com.onter.onter_ic2.item.BatteryItem;
import com.onter.onter_ic2.item.UpgradeItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public abstract class BaseMachineBlockEntity extends BlockEntity implements MenuProvider {
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_OUTPUT = 1;
    public static final int SLOT_BATTERY = 2;
    public static final int SLOT_UPGRADE_1 = 3;
    public static final int SLOT_UPGRADE_2 = 4;
    public static final int SLOT_UPGRADE_3 = 5;
    public static final int SLOT_UPGRADE_4 = 6;
    public static final int TOTAL_SLOTS = 7;

    protected final ItemStackHandler itemHandler = new ItemStackHandler(TOTAL_SLOTS) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot == SLOT_OUTPUT) return false;
            if (slot == SLOT_BATTERY) return stack.getItem() instanceof BatteryItem;
            if (slot >= SLOT_UPGRADE_1 && slot <= SLOT_UPGRADE_4) return stack.getItem() instanceof UpgradeItem;
            return true;
        }
    };

    protected final IC2EnergyStorage energyStorage;
    protected final int baseCapacity;
    protected final int baseEnergyPerTick;
    protected final int baseMaxProgress;

    protected int progress = 0;
    protected int maxProgress = 100;

    protected final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> maxProgress;
                case 2 -> energyStorage.getEnergyStored();
                case 3 -> energyStorage.getMaxEnergyStored();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> progress = value;
                case 1 -> maxProgress = value;
                case 2 -> energyStorage.setEnergy(value);
                case 3 -> energyStorage.setCapacity(value);
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    public BaseMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState,
                                  int baseCapacity, int baseEnergyPerTick, int baseMaxProgress) {
        super(type, pos, blockState);
        this.baseCapacity = baseCapacity;
        this.baseEnergyPerTick = baseEnergyPerTick;
        this.baseMaxProgress = baseMaxProgress;
        this.maxProgress = baseMaxProgress;
        this.energyStorage = new IC2EnergyStorage(baseCapacity, 2000, 0, this::setChanged);
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    public IItemHandler getItemHandler(@Nullable Direction side) {
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

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;

        updateUpgrades();
        handleBatteryDischarge();

        boolean wasLit = state.getValue(BaseMachineBlock.LIT);
        boolean isWorking = false;

        if (canProcess()) {
            int energyNeeded = calculateEnergyPerTick();
            if (energyStorage.getEnergyStored() >= energyNeeded) {
                energyStorage.consumeEnergy(energyNeeded);
                progress++;
                isWorking = true;

                if (progress >= maxProgress) {
                    processItem();
                    progress = 0;
                }
                setChanged();
            }
        } else {
            if (progress > 0) {
                progress = Math.max(0, progress - 2);
                setChanged();
            }
        }

        if (wasLit != isWorking) {
            level.setBlock(pos, state.setValue(BaseMachineBlock.LIT, isWorking), 3);
        }
    }

    protected void updateUpgrades() {
        int overclockers = 0;
        int storageUpgrades = 0;

        for (int i = SLOT_UPGRADE_1; i <= SLOT_UPGRADE_4; i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (stack.getItem() instanceof UpgradeItem upgrade) {
                if (upgrade.getType() == UpgradeItem.UpgradeType.OVERCLOCKER) {
                    overclockers += stack.getCount();
                } else if (upgrade.getType() == UpgradeItem.UpgradeType.ENERGY_STORAGE) {
                    storageUpgrades += stack.getCount();
                }
            }
        }

        // Apply capacity upgrade
        int newCapacity = baseCapacity + (storageUpgrades * 40000);
        if (energyStorage.getMaxEnergyStored() != newCapacity) {
            energyStorage.setCapacity(newCapacity);
        }

        // Apply overclocker upgrade to maxProgress
        int newMaxProgress = Math.max(1, (int) (baseMaxProgress * Math.pow(0.7, overclockers)));
        this.maxProgress = newMaxProgress;
    }

    protected int calculateEnergyPerTick() {
        int overclockers = 0;
        for (int i = SLOT_UPGRADE_1; i <= SLOT_UPGRADE_4; i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (stack.getItem() instanceof UpgradeItem upgrade && upgrade.getType() == UpgradeItem.UpgradeType.OVERCLOCKER) {
                overclockers += stack.getCount();
            }
        }
        return (int) (baseEnergyPerTick * Math.pow(1.6, overclockers));
    }

    protected void handleBatteryDischarge() {
        ItemStack batteryStack = itemHandler.getStackInSlot(SLOT_BATTERY);
        if (!batteryStack.isEmpty() && batteryStack.getItem() instanceof BatteryItem battery) {
            int needed = energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored();
            if (needed > 0) {
                int extracted = BatteryItem.extractEnergy(batteryStack, needed, battery.getMaxTransfer(), false);
                if (extracted > 0) {
                    energyStorage.produceEnergy(extracted);
                    setChanged();
                }
            }
        }
    }

    protected abstract boolean canProcess();
    protected abstract void processItem();

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", itemHandler.serializeNBT(registries));
        tag.put("Energy", energyStorage.serializeNBT(registries));
        tag.putInt("Progress", progress);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Inventory")) {
            itemHandler.deserializeNBT(registries, tag.getCompound("Inventory"));
        }
        if (tag.contains("Energy")) {
            energyStorage.deserializeNBT(registries, tag.getCompound("Energy"));
        }
        if (tag.contains("Progress")) {
            progress = tag.getInt("Progress");
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }
}
