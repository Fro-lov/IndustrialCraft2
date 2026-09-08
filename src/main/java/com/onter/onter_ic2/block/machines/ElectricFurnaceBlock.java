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

public class ElectricFurnaceBlock extends BaseMachineBlock {
    public static final MapCodec<ElectricFurnaceBlock> CODEC = simpleCodec(ElectricFurnaceBlock::new);

    public ElectricFurnaceBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ElectricFurnaceBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : createTickerHelper(blockEntityType, ModBlockEntities.ELECTRIC_FURNACE.get(),
                (lvl, pos, st, be) -> ((ElectricFurnaceBlockEntity) be).tick(lvl, pos, st));
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, net.minecraft.util.RandomSource random) {
        if (state.getValue(LIT)) {
            if (random.nextDouble() < 0.1) {
                level.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        com.onter.onter_ic2.init.ModSounds.ELECTRO_FURNACE_LOOP.get(), net.minecraft.sounds.SoundSource.BLOCKS, 0.6F, 1.0F, false);
            }
            net.minecraft.core.Direction direction = state.getValue(FACING);
            net.minecraft.core.Direction.Axis axis = direction.getAxis();
            double d0 = (double)pos.getX() + 0.5;
            double d1 = (double)pos.getY();
            double d2 = (double)pos.getZ() + 0.5;
            double d3 = 0.52;
            double d4 = random.nextDouble() * 0.6 - 0.3;
            double d5 = axis == net.minecraft.core.Direction.Axis.X ? (double)direction.getStepX() * d3 : d4;
            double d6 = random.nextDouble() * 6.0 / 16.0;
            double d7 = axis == net.minecraft.core.Direction.Axis.Z ? (double)direction.getStepZ() * d3 : d4;
            level.addParticle(net.minecraft.core.particles.ParticleTypes.SMOKE, d0 + d5, d1 + d6 + 0.2, d2 + d7, 0.0, 0.0, 0.0);
        }
    }
}
