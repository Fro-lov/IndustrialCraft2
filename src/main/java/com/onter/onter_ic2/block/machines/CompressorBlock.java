package com.onter.onter_ic2.block.machines;

import com.mojang.serialization.MapCodec;
import com.onter.onter_ic2.block.base.BaseMachineBlock;
import com.onter.onter_ic2.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CompressorBlock extends BaseMachineBlock {
    public static final MapCodec<CompressorBlock> CODEC = simpleCodec(CompressorBlock::new);

    public CompressorBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CompressorBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : createTickerHelper(blockEntityType, ModBlockEntities.COMPRESSOR.get(),
                (lvl, pos, st, be) -> ((CompressorBlockEntity) be).tick(lvl, pos, st));
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, net.minecraft.util.RandomSource random) {
        if (state.getValue(LIT) && random.nextDouble() < 0.1) {
            level.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    com.onter.onter_ic2.init.ModSounds.COMPRESSOR_OP.get(), net.minecraft.sounds.SoundSource.BLOCKS, 0.7F, 1.0F, false);
        }
    }
}
