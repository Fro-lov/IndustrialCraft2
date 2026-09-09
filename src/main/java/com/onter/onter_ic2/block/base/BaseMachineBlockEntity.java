package com.onter.onter_ic2.block.base;

import com.onter.onter_ic2.energy.EnergyPriority;
import com.onter.onter_ic2.energy.IEnergyPrioritized;
import com.onter.onter_ic2.energy.IC2EnergyStorage;
import com.onter.onter_ic2.item.BatteryItem;
import com.onter.onter_ic2.item.EjectorUpgradeItem;
import com.onter.onter_ic2.item.PullingUpgradeItem;
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

public abstract class BaseMachineBlockEntity extends BlockEntity implements MenuProvider, IEnergyPrioritized {
    protected EnergyPriority priority = EnergyPriority.HIGH;

    @Override
    public EnergyPriority getEnergyPriority() {
        return priority;
    }

    @Override
    public void setEnergyPriority(EnergyPriority priority) {
        this.priority = priority;
        setChanged();
    }
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
            if (slot == SLOT_BATTERY) return stack.getItem() instanceof BatteryItem || stack.getCapability(net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage.ITEM, null) != null;
            if (slot >= SLOT_UPGRADE_1 && slot <= SLOT_UPGRADE_4) return stack.getItem() instanceof UpgradeItem;
            if (slot == SLOT_INPUT) return isValidInput(stack);
            return true;
        }
    };

    public abstract boolean isValidInput(ItemStack stack);

    protected final IC2EnergyStorage energyStorage;
    protected final int baseCapacity;
    protected final int baseEnergyPerTick;
    protected final int baseMaxProgress;
    protected final int batchMultiplier;

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
                case 4 -> priority.getLevel();
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
                case 4 -> priority = EnergyPriority.fromLevel(value);
            }
        }

        @Override
        public int getCount() {
            return 5;
        }
    };

    public BaseMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState,
                                  int baseCapacity, int baseEnergyPerTick, int baseMaxProgress) {
        this(type, pos, blockState, baseCapacity, baseEnergyPerTick, baseMaxProgress, 1);
    }

    public BaseMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState,
                                  int baseCapacity, int baseEnergyPerTick, int baseMaxProgress, int batchMultiplier) {
        super(type, pos, blockState);
        this.baseCapacity = baseCapacity;
        this.baseEnergyPerTick = baseEnergyPerTick;
        this.baseMaxProgress = baseMaxProgress;
        this.maxProgress = baseMaxProgress;
        this.batchMultiplier = batchMultiplier;
        this.energyStorage = new IC2EnergyStorage(baseCapacity, 2000 * batchMultiplier, 0, this::setChanged);
    }

    public int getBatchMultiplier() {
        return batchMultiplier;
    }

    protected void produceOutput(ItemStack singleResult, int inputExtracted) {
        int totalCount = singleResult.getCount() * inputExtracted;
        ItemStack outputSlot = itemHandler.getStackInSlot(SLOT_OUTPUT);

        if (outputSlot.isEmpty()) {
            int toPutInSlot = Math.min(singleResult.getMaxStackSize(), totalCount);
            ItemStack inSlot = singleResult.copy();
            inSlot.setCount(toPutInSlot);
            itemHandler.setStackInSlot(SLOT_OUTPUT, inSlot);
            totalCount -= toPutInSlot;
        } else if (ItemStack.isSameItemSameComponents(outputSlot, singleResult)) {
            int space = outputSlot.getMaxStackSize() - outputSlot.getCount();
            int toAdd = Math.min(space, totalCount);
            outputSlot.grow(toAdd);
            totalCount -= toAdd;
        }

        if (totalCount > 0) {
            ItemStack surplus = singleResult.copy();
            surplus.setCount(totalCount);
            surplusOutputBuffer.add(surplus);
        }
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

    public int getBaseEnergyPerTick() {
        return baseEnergyPerTick;
    }

    public int getProgress() {
        return progress;
    }

    public NonNullList<ItemStack> getDrops() {
        NonNullList<ItemStack> drops = NonNullList.create();
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                drops.add(stack);
            }
        }
        for (ItemStack surplus : surplusOutputBuffer) {
            if (!surplus.isEmpty()) {
                drops.add(surplus);
            }
        }
        return drops;
    }

    protected final NonNullList<ItemStack> surplusOutputBuffer = NonNullList.create();

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;

        updateUpgrades();
        handleBatteryDischarge();
        drainSurplusBuffer();
        handleAutomationUpgrades(level, pos);

        boolean wasLit = state.getValue(BaseMachineBlock.LIT);
        boolean isWorking = false;

        if (surplusOutputBuffer.isEmpty() && canProcess()) {
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

    protected void drainSurplusBuffer() {
        if (surplusOutputBuffer.isEmpty()) return;

        ItemStack outputSlot = itemHandler.getStackInSlot(SLOT_OUTPUT);
        for (int i = 0; i < surplusOutputBuffer.size(); i++) {
            ItemStack surplus = surplusOutputBuffer.get(i);
            if (surplus.isEmpty()) continue;

            if (outputSlot.isEmpty()) {
                itemHandler.setStackInSlot(SLOT_OUTPUT, surplus.copy());
                surplusOutputBuffer.remove(i);
                setChanged();
                break;
            } else if (ItemStack.isSameItemSameComponents(outputSlot, surplus)) {
                int space = outputSlot.getMaxStackSize() - outputSlot.getCount();
                if (space > 0) {
                    int toAdd = Math.min(space, surplus.getCount());
                    outputSlot.grow(toAdd);
                    surplus.shrink(toAdd);
                    if (surplus.isEmpty()) {
                        surplusOutputBuffer.remove(i);
                    }
                    setChanged();
                    break;
                }
            }
        }
    }

    protected void handleAutomationUpgrades(Level level, BlockPos pos) {
        ItemStack ejectorStack = ItemStack.EMPTY;
        ItemStack pullingStack = ItemStack.EMPTY;

        for (int i = SLOT_UPGRADE_1; i <= SLOT_UPGRADE_4; i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (stack.getItem() instanceof EjectorUpgradeItem) {
                ejectorStack = stack;
            } else if (stack.getItem() instanceof PullingUpgradeItem) {
                pullingStack = stack;
            }
        }

        // 1. Ejector Upgrade: push output items to adjacent inventory
        if (!ejectorStack.isEmpty()) {
            ItemStack output = itemHandler.getStackInSlot(SLOT_OUTPUT);
            if (!output.isEmpty()) {
                Direction targetDir = EjectorUpgradeItem.getDirection(ejectorStack);
                if (targetDir != null) {
                    ejectToSide(level, pos, targetDir, output);
                } else {
                    for (Direction dir : Direction.values()) {
                        if (ejectToSide(level, pos, dir, output)) break;
                    }
                }
            }
        }

        // 2. Pulling Upgrade: pull items from adjacent inventory into input slot
        if (!pullingStack.isEmpty()) {
            ItemStack input = itemHandler.getStackInSlot(SLOT_INPUT);
            if (input.isEmpty() || input.getCount() < input.getMaxStackSize()) {
                Direction pullDir = PullingUpgradeItem.getDirection(pullingStack);
                if (pullDir != null) {
                    pullFromSide(level, pos, pullDir, input);
                } else {
                    for (Direction dir : Direction.values()) {
                        if (pullFromSide(level, pos, dir, input)) break;
                    }
                }
            }
        }
    }

    private boolean ejectToSide(Level level, BlockPos pos, Direction dir, ItemStack output) {
        IItemHandler target = level.getCapability(net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK, pos.relative(dir), dir.getOpposite());
        if (target != null) {
            for (int slot = 0; slot < target.getSlots(); slot++) {
                ItemStack remainder = target.insertItem(slot, output.copy(), false);
                int accepted = output.getCount() - remainder.getCount();
                if (accepted > 0) {
                    output.shrink(accepted);
                    if (output.isEmpty()) {
                        itemHandler.setStackInSlot(SLOT_OUTPUT, ItemStack.EMPTY);
                    }
                    setChanged();
                    return true;
                }
            }
        }
        return false;
    }

    private boolean pullFromSide(Level level, BlockPos pos, Direction dir, ItemStack currentInput) {
        IItemHandler source = level.getCapability(net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK, pos.relative(dir), dir.getOpposite());
        if (source != null) {
            for (int slot = 0; slot < source.getSlots(); slot++) {
                ItemStack inSlot = source.getStackInSlot(slot);
                if (!inSlot.isEmpty() && (currentInput.isEmpty() || ItemStack.isSameItemSameComponents(currentInput, inSlot))) {
                    int space = currentInput.isEmpty() ? inSlot.getMaxStackSize() : (currentInput.getMaxStackSize() - currentInput.getCount());
                    if (space > 0) {
                        ItemStack extracted = source.extractItem(slot, Math.min(space, inSlot.getCount()), false);
                        if (!extracted.isEmpty()) {
                            if (currentInput.isEmpty()) {
                                itemHandler.setStackInSlot(SLOT_INPUT, extracted);
                            } else {
                                currentInput.grow(extracted.getCount());
                            }
                            setChanged();
                            return true;
                        }
                    }
                }
            }
        }
        return false;
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
        tag.putInt("EnergyPriority", priority.getLevel());

        net.minecraft.nbt.ListTag surplusTag = new net.minecraft.nbt.ListTag();
        for (ItemStack surplus : surplusOutputBuffer) {
            if (!surplus.isEmpty()) {
                surplusTag.add(surplus.save(registries));
            }
        }
        tag.put("SurplusBuffer", surplusTag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Inventory")) {
            itemHandler.deserializeNBT(registries, tag.getCompound("Inventory"));
            if (itemHandler.getSlots() != TOTAL_SLOTS) {
                itemHandler.setSize(TOTAL_SLOTS);
            }
        } else {
            if (itemHandler.getSlots() != TOTAL_SLOTS) {
                itemHandler.setSize(TOTAL_SLOTS);
            }
        }
        if (tag.contains("Energy")) {
            energyStorage.deserializeNBT(registries, tag.getCompound("Energy"));
        }
        if (tag.contains("Progress")) {
            progress = tag.getInt("Progress");
        }
        if (tag.contains("EnergyPriority")) {
            priority = EnergyPriority.fromLevel(tag.getInt("EnergyPriority"));
        }
        surplusOutputBuffer.clear();
        if (tag.contains("SurplusBuffer")) {
            net.minecraft.nbt.ListTag surplusTag = tag.getList("SurplusBuffer", net.minecraft.nbt.Tag.TAG_COMPOUND);
            for (int i = 0; i < surplusTag.size(); i++) {
                ItemStack.parse(registries, surplusTag.getCompound(i)).ifPresent(surplusOutputBuffer::add);
            }
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
