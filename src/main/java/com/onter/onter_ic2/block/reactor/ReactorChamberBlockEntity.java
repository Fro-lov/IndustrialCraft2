package com.onter.onter_ic2.block.reactor;

import com.onter.onter_ic2.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class ReactorChamberBlockEntity extends BlockEntity {
    public ReactorChamberBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.REACTOR_CHAMBER.get(), pos, state);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;

        // Forward energy distribution from connected reactor through chamber faces
        NuclearReactorBlockEntity reactor = ReactorChamberBlock.findReactor(level, pos);
        if (reactor != null && reactor.getEnergyStorage().getEnergyStored() > 0) {
            for (Direction dir : Direction.values()) {
                BlockPos targetPos = pos.relative(dir);
                if (level.getBlockEntity(targetPos) instanceof NuclearReactorBlockEntity || level.getBlockEntity(targetPos) instanceof ReactorChamberBlockEntity) {
                    continue;
                }
                IEnergyStorage targetStorage = level.getCapability(Capabilities.EnergyStorage.BLOCK, targetPos, dir.getOpposite());
                if (targetStorage != null && targetStorage.canReceive()) {
                    int maxExtract = reactor.getEnergyStorage().extractEnergy(32_768, true);
                    int accepted = targetStorage.receiveEnergy(maxExtract, false);
                    reactor.getEnergyStorage().extractEnergy(accepted, false);
                    if (reactor.getEnergyStorage().getEnergyStored() <= 0) break;
                }
            }
        }
    }
}
