package com.onter.onter_ic2.block.cables;

import com.onter.onter_ic2.energy.network.EnergyNetwork;
import com.onter.onter_ic2.energy.network.EnergyNetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class CableBlockEntity extends BlockEntity {
    private final int maxTransfer;
    private EnergyNetwork network;

    private final IEnergyStorage energyStorageWrapper = new IEnergyStorage() {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            if (level != null && !level.isClientSide && maxReceive > 0) {
                EnergyNetwork net = getNetwork();
                if (net != null) {
                    return net.receiveEnergy(maxReceive, simulate);
                }
            }
            return 0;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            return 0;
        }

        @Override
        public int getEnergyStored() {
            EnergyNetwork net = getNetwork();
            return net != null ? net.getNetworkBuffer() : 0;
        }

        @Override
        public int getMaxEnergyStored() {
            return Integer.MAX_VALUE;
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

    public CableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState, int maxTransfer) {
        super(type, pos, blockState);
        this.maxTransfer = maxTransfer;
    }

    public void setNetwork(EnergyNetwork network) {
        this.network = network;
    }

    public EnergyNetwork getNetwork() {
        if ((network == null || !network.isValid()) && level != null && !level.isClientSide) {
            network = EnergyNetworkManager.getOrCreateNetwork(level, worldPosition);
        }
        return network;
    }

    public IEnergyStorage getEnergyStorage() {
        return energyStorageWrapper;
    }

    public int getMaxTransfer() {
        return maxTransfer;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null && !level.isClientSide) {
            EnergyNetworkManager.onCableAdded(level, worldPosition);
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (level != null && !level.isClientSide) {
            EnergyNetworkManager.onCableRemoved(level, worldPosition);
        }
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        if (level != null && !level.isClientSide) {
            // Выгрузка чанка не ломает сеть, топология остается кэшированной
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
    }
}
