package com.onter.onter_ic2.block.machines;

import com.onter.onter_ic2.energy.IC2EnergyStorage;
import com.onter.onter_ic2.init.ModBlockEntities;
import com.onter.onter_ic2.init.ModItems;
import com.onter.onter_ic2.inventory.UnifiedReplicatorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UnifiedReplicatorBlockEntity extends BlockEntity implements MenuProvider {
    public static final int CAPACITY = 2_000_000;
    public static final int MAX_RECEIVE = 32_768;
    public static final int MAX_PROGRESS = 100;

    private final ItemStackHandler itemHandler = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private final IC2EnergyStorage energyStorage = new IC2EnergyStorage(CAPACITY, MAX_RECEIVE, 0, this::setChanged);

    private int progress = 0;
    private int scanProgress = 0;
    private int selectedPatternIndex = 0;

    // Pattern table
    public static final Item[] PATTERNS = new Item[] {
            Items.DIAMOND,
            Items.NETHERITE_SCRAP,
            Items.GOLD_INGOT,
            Items.IRON_INGOT,
            Items.COPPER_INGOT,
            Items.REDSTONE,
            Items.GLOWSTONE_DUST,
            Items.COAL,
            Items.OBSIDIAN
    };

    public static final int[] UU_COST = new int[] { 1, 4, 1, 1, 1, 1, 1, 1, 1 };
    public static final int[] YIELD_AMOUNT = new int[] { 1, 1, 2, 4, 8, 8, 8, 8, 4 };
    public static final int[] ENERGY_COST = new int[] { 20000, 80000, 10000, 5000, 5000, 5000, 5000, 5000, 10000 };

    protected final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> MAX_PROGRESS;
                case 2 -> scanProgress;
                case 3 -> selectedPatternIndex;
                case 4 -> energyStorage.getEnergyStored();
                case 5 -> energyStorage.getMaxEnergyStored();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> progress = value;
                case 2 -> scanProgress = value;
                case 3 -> selectedPatternIndex = Math.max(0, Math.min(PATTERNS.length - 1, value));
                case 4 -> energyStorage.setEnergy(value);
            }
        }

        @Override
        public int getCount() {
            return 6;
        }
    };

    public UnifiedReplicatorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.UNIFIED_REPLICATOR.get(), pos, state);
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    public IC2EnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    public ContainerData getDataAccess() {
        return dataAccess;
    }

    public void setSelectedPatternIndex(int idx) {
        if (idx >= 0 && idx < PATTERNS.length) {
            this.selectedPatternIndex = idx;
            setChanged();
        }
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;

        // Scanning Slot (Slot 0)
        ItemStack scanStack = itemHandler.getStackInSlot(0);
        if (!scanStack.isEmpty()) {
            for (int i = 0; i < PATTERNS.length; i++) {
                if (PATTERNS[i] == scanStack.getItem()) {
                    selectedPatternIndex = i;
                    break;
                }
            }
            scanProgress = 100;
        } else {
            scanProgress = 0;
        }

        // Replication Logic
        int uuNeeded = UU_COST[selectedPatternIndex];
        int energyPerTick = ENERGY_COST[selectedPatternIndex] / MAX_PROGRESS;
        Item targetItem = PATTERNS[selectedPatternIndex];
        int yield = YIELD_AMOUNT[selectedPatternIndex];

        ItemStack uuStack = itemHandler.getStackInSlot(1);
        ItemStack outStack = itemHandler.getStackInSlot(2);

        boolean canReplicate = !uuStack.isEmpty()
                && uuStack.getItem() == ModItems.UU_MATTER.get()
                && uuStack.getCount() >= uuNeeded
                && (outStack.isEmpty() || (outStack.getItem() == targetItem && outStack.getCount() + yield <= outStack.getMaxStackSize()))
                && energyStorage.getEnergyStored() >= energyPerTick;

        boolean active = false;

        if (canReplicate) {
            energyStorage.extractEnergy(energyPerTick, false);
            progress++;
            active = true;

            if (progress >= MAX_PROGRESS) {
                progress = 0;
                uuStack.shrink(uuNeeded);
                if (outStack.isEmpty()) {
                    itemHandler.setStackInSlot(2, new ItemStack(targetItem, yield));
                } else {
                    outStack.grow(yield);
                }
            }
            setChanged();
        } else {
            if (progress > 0) {
                progress = Math.max(0, progress - 1);
                setChanged();
            }
        }

        if (state.getValue(UnifiedReplicatorBlock.ACTIVE) != active) {
            level.setBlock(pos, state.setValue(UnifiedReplicatorBlock.ACTIVE, active), 3);
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", itemHandler.serializeNBT(registries));
        tag.put("Energy", energyStorage.serializeNBT(registries));
        tag.putInt("Progress", progress);
        tag.putInt("ScanProgress", scanProgress);
        tag.putInt("PatternIndex", selectedPatternIndex);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Inventory")) itemHandler.deserializeNBT(registries, tag.getCompound("Inventory"));
        if (tag.contains("Energy")) energyStorage.deserializeNBT(registries, tag.get("Energy"));
        progress = tag.getInt("Progress");
        scanProgress = tag.getInt("ScanProgress");
        selectedPatternIndex = tag.getInt("PatternIndex");
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.onter_ic2.unified_replicator");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory, @NotNull Player player) {
        return new UnifiedReplicatorMenu(containerId, playerInventory, itemHandler, dataAccess);
    }
}
