package com.onter.onter_ic2.block.generators;

import com.onter.onter_ic2.energy.IC2EnergyStorage;
import com.onter.onter_ic2.init.ModBlockEntities;
import com.onter.onter_ic2.menu.QuantumGeneratorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class QuantumGeneratorBlockEntity extends BlockEntity implements MenuProvider {
    private int production = 512;
    private int tier = 3;
    private boolean active = true;

    private final IC2EnergyStorage energyStorage = new IC2EnergyStorage(Integer.MAX_VALUE, 0, Integer.MAX_VALUE, this::setChanged) {
        @Override
        public int getEnergyStored() {
            return active ? production * 4 : 0;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            if (!active) return 0;
            return Math.min(maxExtract, production * 4);
        }
    };

    protected final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> production & 0xFFFF;
                case 1 -> (production >> 16) & 0xFFFF;
                case 2 -> tier;
                case 3 -> active ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> production = (production & 0xFFFF0000) | (value & 0xFFFF);
                case 1 -> production = (production & 0x0000FFFF) | ((value & 0xFFFF) << 16);
                case 2 -> tier = value;
                case 3 -> active = value != 0;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    public QuantumGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.QUANTUM_GENERATOR.get(), pos, state);
    }

    public int getProduction() {
        return production;
    }

    public int getTier() {
        return tier;
    }

    public boolean isActive() {
        return active;
    }

    public IEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    public ContainerData getDataAccess() {
        return dataAccess;
    }

    public void handleButtonClick(int event) {
        int[] changes = {-100, -10, -1, 1, 10, 100};
        int[] shifted = {-500, -50, -5, 5, 50, 500};
        if (event >= 0 && event < 6) {
            production = Math.max(0, production + changes[event]);
        } else if (event >= 10 && event < 16) {
            production = Math.max(0, production + shifted[event - 10]);
        } else if (event >= 20 && event <= 25) {
            tier = event - 19;
        }
        setChanged();
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;

        boolean hasSignal = level.hasNeighborSignal(pos);
        boolean shouldBeActive = !hasSignal;

        if (this.active != shouldBeActive) {
            this.active = shouldBeActive;
            level.setBlock(pos, state.setValue(QuantumGeneratorBlock.ACTIVE, this.active), 3);
            setChanged();
        }

        if (!active || production <= 0) return;

        int energyToOutput = production * 4; // 1 EU = 4 FE

        // Push energy to all 6 sides
        for (Direction direction : Direction.values()) {
            BlockEntity neighbor = level.getBlockEntity(pos.relative(direction));
            if (neighbor != null) {
                IEnergyStorage neighborStorage = level.getCapability(
                        Capabilities.EnergyStorage.BLOCK,
                        pos.relative(direction),
                        direction.getOpposite()
                );
                if (neighborStorage != null && neighborStorage.canReceive()) {
                    neighborStorage.receiveEnergy(energyToOutput, false);
                }
            }
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("production", production);
        tag.putInt("tier", tier);
        tag.putBoolean("active", active);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        production = tag.contains("production") ? tag.getInt("production") : 512;
        tier = tag.contains("tier") ? tag.getInt("tier") : 3;
        active = !tag.contains("active") || tag.getBoolean("active");
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.onter_ic2.quantum_generator");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory, @NotNull Player player) {
        return new QuantumGeneratorMenu(containerId, playerInventory, this, this.dataAccess);
    }
}
