package com.onter.onter_ic2.block.storage;

import com.onter.onter_ic2.energy.IC2EnergyStorage;
import com.onter.onter_ic2.item.BatteryItem;
import com.onter.onter_ic2.item.UpgradeItem;
import com.onter.onter_ic2.menu.EnergyStorageMenu;
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

public class EnergyStorageBlockEntity extends BlockEntity implements MenuProvider {
    public static final int SLOT_CHARGE = 0;
    public static final int SLOT_DISCHARGE = 1;
    public static final int SLOT_UPGRADE_1 = 2;
    public static final int TOTAL_SLOTS = 6;

    private final String nameKey;
    private final int baseCapacity;
    private final int maxTransfer;
    private final IC2EnergyStorage energyStorage;

    private final ItemStackHandler itemHandler = new ItemStackHandler(TOTAL_SLOTS) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot == SLOT_CHARGE || slot == SLOT_DISCHARGE) {
                return stack.getItem() instanceof BatteryItem;
            }
            return stack.getItem() instanceof UpgradeItem;
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
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            // Handled on server
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    public EnergyStorageBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState,
                                    String nameKey, int capacity, int maxTransfer) {
        super(type, pos, blockState);
        this.nameKey = nameKey;
        this.baseCapacity = capacity;
        this.maxTransfer = maxTransfer;
        this.energyStorage = new IC2EnergyStorage(capacity, maxTransfer, maxTransfer, this::setChanged);
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
        return Component.translatable("block.onter_ic2." + nameKey);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new EnergyStorageMenu(containerId, playerInventory, this, this.dataAccess);
    }

    public Direction getOutputDirection() {
        BlockState state = getBlockState();
        if (state.hasProperty(EnergyStorageBlock.FACING)) {
            return state.getValue(EnergyStorageBlock.FACING);
        }
        return Direction.NORTH;
    }

    public IEnergyStorage getEnergyStorageForSide(@Nullable Direction side) {
        if (side == null) {
            return energyStorage;
        }
        Direction outputDir = getOutputDirection();
        if (side == outputDir) {
            return new IEnergyStorage() {
                @Override
                public int receiveEnergy(int maxReceive, boolean simulate) {
                    return 0;
                }
                @Override
                public int extractEnergy(int maxExtract, boolean simulate) {
                    return energyStorage.extractEnergy(maxExtract, simulate);
                }
                @Override
                public int getEnergyStored() {
                    return energyStorage.getEnergyStored();
                }
                @Override
                public int getMaxEnergyStored() {
                    return energyStorage.getMaxEnergyStored();
                }
                @Override
                public boolean canExtract() {
                    return true;
                }
                @Override
                public boolean canReceive() {
                    return false;
                }
            };
        } else {
            return new IEnergyStorage() {
                @Override
                public int receiveEnergy(int maxReceive, boolean simulate) {
                    return energyStorage.receiveEnergy(maxReceive, simulate);
                }
                @Override
                public int extractEnergy(int maxExtract, boolean simulate) {
                    return 0;
                }
                @Override
                public int getEnergyStored() {
                    return energyStorage.getEnergyStored();
                }
                @Override
                public int getMaxEnergyStored() {
                    return energyStorage.getMaxEnergyStored();
                }
                @Override
                public boolean canExtract() {
                    return false;
                }
                @Override
                public boolean canReceive() {
                    return true;
                }
            };
        }
    }

    public int getMaxTransfer() {
        return maxTransfer;
    }

    public int getEnergyStored() {
        return energyStorage.getEnergyStored();
    }

    public void setEnergyStored(int energy) {
        energyStorage.setEnergy(energy);
        setChanged();
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;

        int slots = itemHandler.getSlots();

        // Upgrades
        int storageUpgrades = 0;
        for (int i = SLOT_UPGRADE_1; i < slots; i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (stack.getItem() instanceof UpgradeItem upgrade && upgrade.getType() == UpgradeItem.UpgradeType.ENERGY_STORAGE) {
                storageUpgrades += stack.getCount();
            }
        }
        int newCap = baseCapacity + (storageUpgrades * 40000);
        if (energyStorage.getMaxEnergyStored() != newCap) {
            energyStorage.setCapacity(newCap);
        }

        // Slot 0: Charge battery
        if (SLOT_CHARGE < slots) {
            ItemStack chargeStack = itemHandler.getStackInSlot(SLOT_CHARGE);
            if (!chargeStack.isEmpty()) {
                net.neoforged.neoforge.energy.IEnergyStorage itemCap = chargeStack.getCapability(net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage.ITEM, null);
                if (itemCap != null && itemCap.canReceive()) {
                    int toSend = Math.min(energyStorage.getEnergyStored(), maxTransfer);
                    int accepted = itemCap.receiveEnergy(toSend, false);
                    if (accepted > 0) {
                        energyStorage.consumeEnergy(accepted);
                        setChanged();
                    }
                } else if (chargeStack.getItem() instanceof BatteryItem battery) {
                    int canSend = Math.min(energyStorage.getEnergyStored(), Math.min(maxTransfer, battery.getMaxTransfer()));
                    if (canSend > 0) {
                        int received = BatteryItem.receiveEnergy(chargeStack, canSend, battery.getCapacity(), battery.getMaxTransfer(), false);
                        if (received > 0) {
                            energyStorage.consumeEnergy(received);
                            setChanged();
                        }
                    }
                }
            }
        }

        // Slot 1: Discharge battery into storage
        if (SLOT_DISCHARGE < slots) {
            ItemStack dischargeStack = itemHandler.getStackInSlot(SLOT_DISCHARGE);
            if (!dischargeStack.isEmpty()) {
                net.neoforged.neoforge.energy.IEnergyStorage itemCap = dischargeStack.getCapability(net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage.ITEM, null);
                if (itemCap != null && itemCap.canExtract()) {
                    int needed = energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored();
                    int canExtract = Math.min(needed, maxTransfer);
                    int extracted = itemCap.extractEnergy(canExtract, false);
                    if (extracted > 0) {
                        energyStorage.produceEnergy(extracted);
                        setChanged();
                    }
                } else if (dischargeStack.getItem() instanceof BatteryItem battery) {
                    int needed = energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored();
                    int canExtract = Math.min(needed, Math.min(maxTransfer, battery.getMaxTransfer()));
                    if (canExtract > 0) {
                        int extracted = BatteryItem.extractEnergy(dischargeStack, canExtract, battery.getMaxTransfer(), false);
                        if (extracted > 0) {
                            energyStorage.produceEnergy(extracted);
                            setChanged();
                        }
                    }
                }
            }
        }

        // Output energy ONLY to adjacent block at the output direction (FACING)
        if (energyStorage.getEnergyStored() > 0) {
            Direction outputDir = getOutputDirection();
            IEnergyStorage adjacent = level.getCapability(Capabilities.EnergyStorage.BLOCK, pos.relative(outputDir), outputDir.getOpposite());
            if (adjacent != null && adjacent.canReceive()) {
                int toSend = Math.min(energyStorage.getEnergyStored(), maxTransfer);
                int accepted = adjacent.receiveEnergy(toSend, false);
                if (accepted > 0) {
                    energyStorage.consumeEnergy(accepted);
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
            if (itemHandler.getSlots() < TOTAL_SLOTS) {
                itemHandler.setSize(TOTAL_SLOTS);
            }
        }
        if (tag.contains("Energy")) energyStorage.deserializeNBT(registries, tag.getCompound("Energy"));
    }
}
