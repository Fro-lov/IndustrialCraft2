package com.onter.onter_ic2.block.cables;

import com.onter.onter_ic2.energy.IC2EnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.*;

public class CableBlockEntity extends BlockEntity {
    private final int maxTransfer;
    private final IC2EnergyStorage energyStorage;

    public CableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState, int maxTransfer) {
        super(type, pos, blockState);
        this.maxTransfer = maxTransfer;
        this.energyStorage = new IC2EnergyStorage(maxTransfer, maxTransfer, maxTransfer, this::setChanged) {
            @Override
            public int receiveEnergy(int toReceive, boolean simulate) {
                if (level != null && !level.isClientSide && toReceive > 0) {
                    // Instantly attempt network distribution to connected endpoints
                    int distributed = distributeToNetwork(toReceive, simulate);
                    if (distributed >= toReceive) {
                        return distributed;
                    }
                    int leftover = toReceive - distributed;
                    int stored = super.receiveEnergy(leftover, simulate);
                    return distributed + stored;
                }
                return super.receiveEnergy(toReceive, simulate);
            }
        };
    }

    public IEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    public int getMaxTransfer() {
        return maxTransfer;
    }

    public int distributeToNetwork(int amount, boolean simulate) {
        if (level == null || level.isClientSide || amount <= 0) return 0;

        List<Endpoint> endpoints = findEndpoints();
        if (endpoints.isEmpty()) return 0;

        int totalAccepted = 0;
        int remaining = Math.min(amount, maxTransfer);

        for (Endpoint ep : endpoints) {
            if (remaining <= 0) break;
            IEnergyStorage target = level.getCapability(Capabilities.EnergyStorage.BLOCK, ep.pos, ep.side);
            if (target != null && target.canReceive()) {
                int accepted = target.receiveEnergy(remaining, simulate);
                if (accepted > 0) {
                    totalAccepted += accepted;
                    remaining -= accepted;
                }
            }
        }
        return totalAccepted;
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;

        if (energyStorage.getEnergyStored() > 0) {
            int toSend = Math.min(energyStorage.getEnergyStored(), maxTransfer);
            int sent = distributeToNetwork(toSend, false);
            if (sent > 0) {
                energyStorage.consumeEnergy(sent);
            }
        }
    }

    public record Endpoint(BlockPos pos, Direction side) {}

    private List<Endpoint> findEndpoints() {
        List<Endpoint> endpoints = new ArrayList<>();
        Set<BlockPos> visitedCables = new HashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();

        queue.add(worldPosition);
        visitedCables.add(worldPosition);

        int maxSearch = 256; // Limit cable network search depth to prevent lag

        while (!queue.isEmpty() && visitedCables.size() < maxSearch) {
            BlockPos current = queue.poll();

            for (Direction dir : Direction.values()) {
                BlockPos neighborPos = current.relative(dir);
                BlockEntity neighborBe = level.getBlockEntity(neighborPos);

                if (neighborBe instanceof CableBlockEntity) {
                    if (visitedCables.add(neighborPos)) {
                        queue.add(neighborPos);
                    }
                } else if (neighborBe != null) {
                    IEnergyStorage target = level.getCapability(Capabilities.EnergyStorage.BLOCK, neighborPos, dir.getOpposite());
                    if (target != null && target.canReceive()) {
                        endpoints.add(new Endpoint(neighborPos, dir.getOpposite()));
                    }
                }
            }
        }

        return endpoints;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Energy", energyStorage.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Energy")) energyStorage.deserializeNBT(registries, tag.getCompound("Energy"));
    }
}
