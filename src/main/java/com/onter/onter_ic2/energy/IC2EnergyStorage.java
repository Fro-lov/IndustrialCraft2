package com.onter.onter_ic2.energy;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.energy.EnergyStorage;

public class IC2EnergyStorage extends EnergyStorage {
    private final Runnable onContentsChanged;

    public IC2EnergyStorage(int capacity, int maxTransfer, Runnable onContentsChanged) {
        super(capacity, maxTransfer, maxTransfer, 0);
        this.onContentsChanged = onContentsChanged;
    }

    public IC2EnergyStorage(int capacity, int maxReceive, int maxExtract, Runnable onContentsChanged) {
        super(capacity, maxReceive, maxExtract, 0);
        this.onContentsChanged = onContentsChanged;
    }

    public void setEnergy(int energy) {
        this.energy = Math.max(0, Math.min(energy, capacity));
        if (onContentsChanged != null) {
            onContentsChanged.run();
        }
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
        if (this.energy > capacity) {
            this.energy = capacity;
        }
        if (onContentsChanged != null) {
            onContentsChanged.run();
        }
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int received = super.receiveEnergy(maxReceive, simulate);
        if (received > 0 && !simulate && onContentsChanged != null) {
            onContentsChanged.run();
        }
        return received;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int extracted = super.extractEnergy(maxExtract, simulate);
        if (extracted > 0 && !simulate && onContentsChanged != null) {
            onContentsChanged.run();
        }
        return extracted;
    }

    public void consumeEnergy(int amount) {
        this.energy = Math.max(0, this.energy - amount);
        if (onContentsChanged != null) {
            onContentsChanged.run();
        }
    }

    public void produceEnergy(int amount) {
        this.energy = Math.min(this.capacity, this.energy + amount);
        if (onContentsChanged != null) {
            onContentsChanged.run();
        }
    }

    public CompoundTag serializeNBT(HolderLookup.Provider lookupProvider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("Energy", this.energy);
        tag.putInt("Capacity", this.capacity);
        return tag;
    }

    public void deserializeNBT(HolderLookup.Provider lookupProvider, CompoundTag tag) {
        if (tag.contains("Energy")) {
            this.energy = tag.getInt("Energy");
        }
        if (tag.contains("Capacity")) {
            this.capacity = tag.getInt("Capacity");
        }
    }
}
