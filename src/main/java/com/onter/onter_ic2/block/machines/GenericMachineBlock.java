package com.onter.onter_ic2.block.machines;

import com.mojang.serialization.MapCodec;
import com.onter.onter_ic2.block.base.BaseMachineBlock;
import com.onter.onter_ic2.block.base.BaseMachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class GenericMachineBlock extends BaseMachineBlock {
    private final BiFunction<BlockPos, BlockState, ? extends BlockEntity> factory;
    @Nullable
    private final Supplier<SoundEvent> soundSupplier;

    public GenericMachineBlock(BiFunction<BlockPos, BlockState, ? extends BlockEntity> factory,
                               @Nullable Supplier<SoundEvent> soundSupplier,
                               Properties properties) {
        super(properties);
        this.factory = factory;
        this.soundSupplier = soundSupplier;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return factory.apply(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : (lvl, p, st, be) -> {
            if (be instanceof BaseMachineBlockEntity machine) {
                machine.tick(lvl, p, st);
            } else if (be instanceof MultiSlotMachineBlockEntity multi) {
                MultiSlotMachineBlockEntity.tick(lvl, p, st, multi);
            }
        };
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (soundSupplier != null && state.getValue(LIT) && random.nextDouble() < 0.1) {
            level.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    soundSupplier.get(), SoundSource.BLOCKS, 0.7F, 1.0F, false);
        }
    }
}
