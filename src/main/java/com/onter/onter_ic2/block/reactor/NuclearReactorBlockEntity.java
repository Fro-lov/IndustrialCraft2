package com.onter.onter_ic2.block.reactor;

import com.onter.onter_ic2.energy.IC2EnergyStorage;
import com.onter.onter_ic2.init.ModBlockEntities;
import com.onter.onter_ic2.init.ModSounds;
import com.onter.onter_ic2.inventory.NuclearReactorMenu;
import com.onter.onter_ic2.reactor.IReactor;
import com.onter.onter_ic2.reactor.IReactorComponent;
import com.onter.onter_ic2.reactor.ReactorPlatingItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NuclearReactorBlockEntity extends BlockEntity implements IReactor, MenuProvider {
    public static final int BASE_MAX_HEAT = 10000;
    public static final int GRID_ROWS = 6;
    public static final int GRID_COLS = 9;
    public static final int TOTAL_SLOTS = GRID_ROWS * GRID_COLS; // 54 slots

    private final ItemStackHandler itemHandler = new ItemStackHandler(TOTAL_SLOTS) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private final IC2EnergyStorage energyStorage = new IC2EnergyStorage(1_000_000, 0, 32_768, this::setChanged);

    private int heat = 0;
    private int maxHeat = BASE_MAX_HEAT;
    private int chamberCount = 0;
    private float lastOutputEU = 0;
    private int cycleTimer = 0;
    private boolean isPowered = false;

    protected final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> heat;
                case 1 -> maxHeat;
                case 2 -> chamberCount;
                case 3 -> (int) lastOutputEU;
                case 4 -> energyStorage.getEnergyStored();
                case 5 -> energyStorage.getMaxEnergyStored();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> heat = value;
                case 1 -> maxHeat = value;
                case 2 -> chamberCount = value;
                case 3 -> lastOutputEU = value;
                case 4 -> energyStorage.setEnergy(value);
            }
        }

        @Override
        public int getCount() {
            return 6;
        }
    };

    public NuclearReactorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.NUCLEAR_REACTOR.get(), pos, state);
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

    public int getChamberCount() {
        return chamberCount;
    }

    public int getActiveColumns() {
        return Math.min(GRID_COLS, 3 + chamberCount);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;

        // Distribute buffered energy to adjacent energy consumers
        distributeEnergy(level, pos);

        // Update chamber count every 20 ticks
        if (cycleTimer % 20 == 0) {
            updateChamberCount(level, pos);
            isPowered = level.hasNeighborSignal(pos);
            for (Direction dir : Direction.values()) {
                if (level.hasNeighborSignal(pos.relative(dir))) {
                    isPowered = true;
                    break;
                }
            }
        }

        cycleTimer++;
        if (cycleTimer >= 20) {
            cycleTimer = 0;
            if (isPowered) {
                runReactorCycle(level, pos);
            } else {
                lastOutputEU = 0;
            }

            // Warning siren when heat > 70%
            if (heat > (maxHeat * 0.70F)) {
                level.playSound(null, pos, ModSounds.REACTOR_HIGH.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        }

        boolean active = isPowered && lastOutputEU > 0;
        if (state.getValue(NuclearReactorBlock.ACTIVE) != active) {
            level.setBlock(pos, state.setValue(NuclearReactorBlock.ACTIVE, active), 3);
        }
    }

    private void updateChamberCount(Level level, BlockPos pos) {
        int count = 0;
        for (Direction dir : Direction.values()) {
            BlockEntity be = level.getBlockEntity(pos.relative(dir));
            if (be instanceof ReactorChamberBlockEntity) {
                count++;
            }
        }
        this.chamberCount = Math.min(6, count);
    }

    private void runReactorCycle(Level level, BlockPos pos) {
        int calculatedMaxHeat = BASE_MAX_HEAT;
        float totalEUProduced = 0.0F;

        int activeCols = getActiveColumns();

        // Calculate max heat from platings
        for (int y = 0; y < GRID_ROWS; y++) {
            for (int x = 0; x < activeCols; x++) {
                ItemStack stack = getItemAt(x, y);
                if (!stack.isEmpty() && stack.getItem() instanceof ReactorPlatingItem plating) {
                    calculatedMaxHeat += plating.maxHeatAddition;
                }
            }
        }
        this.maxHeat = calculatedMaxHeat;

        // Energy Production Phase
        for (int y = 0; y < GRID_ROWS; y++) {
            for (int x = 0; x < activeCols; x++) {
                ItemStack stack = getItemAt(x, y);
                if (!stack.isEmpty() && stack.getItem() instanceof IReactorComponent comp) {
                    comp.processChamber(stack, this, x, y, false);
                }
            }
        }

        // Heat Transfer and Dissipation Phase
        for (int y = 0; y < GRID_ROWS; y++) {
            for (int x = 0; x < activeCols; x++) {
                ItemStack stack = getItemAt(x, y);
                if (!stack.isEmpty() && stack.getItem() instanceof IReactorComponent comp) {
                    comp.processChamber(stack, this, x, y, true);
                }
            }
        }

        // Meltdown check
        if (heat >= maxHeat) {
            meltdown(level, pos);
        }

        setChanged();
    }

    private void meltdown(Level level, BlockPos pos) {
        float explosionStrength = 15.0F;
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() instanceof IReactorComponent comp) {
                explosionStrength += comp.influenceExplosion(stack, this);
            }
        }

        level.playSound(null, pos, ModSounds.NUKE_EXPLODE.get(), SoundSource.BLOCKS, 2.0F, 1.0F);
        level.destroyBlock(pos, false);
        for (Direction dir : Direction.values()) {
            BlockPos chamberPos = pos.relative(dir);
            if (level.getBlockEntity(chamberPos) instanceof ReactorChamberBlockEntity) {
                level.destroyBlock(chamberPos, false);
            }
        }

        level.explode(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, Math.max(5.0F, explosionStrength), Level.ExplosionInteraction.BLOCK);
    }

    private void distributeEnergy(Level level, BlockPos pos) {
        if (energyStorage.getEnergyStored() <= 0) return;

        for (Direction dir : Direction.values()) {
            BlockPos targetPos = pos.relative(dir);
            IEnergyStorage targetStorage = level.getCapability(Capabilities.EnergyStorage.BLOCK, targetPos, dir.getOpposite());
            if (targetStorage != null && targetStorage.canReceive()) {
                int maxExtract = energyStorage.extractEnergy(32_768, true);
                int accepted = targetStorage.receiveEnergy(maxExtract, false);
                energyStorage.extractEnergy(accepted, false);
                if (energyStorage.getEnergyStored() <= 0) break;
            }
        }
    }

    @Override
    public Level getReactorLevel() {
        return this.level;
    }

    @Override
    public BlockPos getReactorPos() {
        return this.worldPosition;
    }

    @Override
    public int getHeat() {
        return heat;
    }

    @Override
    public void setHeat(int heat) {
        this.heat = Math.max(0, heat);
    }

    @Override
    public void addHeat(int heat) {
        this.heat = Math.max(0, this.heat + heat);
    }

    @Override
    public int getMaxHeat() {
        return maxHeat;
    }

    @Override
    public void addEmitHeat(int heat) {
        // Dissipated into environment
    }

    @Override
    public void addOutput(float energy) {
        float eu = energy * 5.0F; // IC2 1 uranium pulse = 5 EU/t (20 ticks = 100 EU)
        this.lastOutputEU += eu;
        int fe = (int) (eu * 4 * 20); // convert EU/t per sec to FE
        this.energyStorage.produceEnergy(fe);
    }

    @Override
    public ItemStack getItemAt(int x, int y) {
        if (x < 0 || x >= GRID_COLS || y < 0 || y >= GRID_ROWS) return ItemStack.EMPTY;
        int slot = y * GRID_COLS + x;
        return itemHandler.getStackInSlot(slot);
    }

    @Override
    public void setItemAt(int x, int y, ItemStack stack) {
        if (x < 0 || x >= GRID_COLS || y < 0 || y >= GRID_ROWS) return;
        int slot = y * GRID_COLS + x;
        itemHandler.setStackInSlot(slot, stack);
    }

    @Override
    public boolean produceEnergy() {
        return isPowered;
    }

    @Override
    public int getGridWidth() {
        return getActiveColumns();
    }

    @Override
    public int getGridHeight() {
        return GRID_ROWS;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", itemHandler.serializeNBT(registries));
        tag.put("Energy", energyStorage.serializeNBT(registries));
        tag.putInt("Heat", heat);
        tag.putInt("MaxHeat", maxHeat);
        tag.putInt("Chambers", chamberCount);
        tag.putFloat("LastOutput", lastOutputEU);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Inventory")) itemHandler.deserializeNBT(registries, tag.getCompound("Inventory"));
        if (tag.contains("Energy")) energyStorage.deserializeNBT(registries, tag.get("Energy"));
        heat = tag.getInt("Heat");
        maxHeat = tag.getInt("MaxHeat");
        chamberCount = tag.getInt("Chambers");
        lastOutputEU = tag.getFloat("LastOutput");
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.onter_ic2.nuclear_reactor");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory, @NotNull Player player) {
        return new NuclearReactorMenu(containerId, playerInventory, itemHandler, dataAccess);
    }
}
