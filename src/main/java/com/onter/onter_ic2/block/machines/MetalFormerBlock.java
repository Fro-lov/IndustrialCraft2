package com.onter.onter_ic2.block.machines;

import com.mojang.serialization.MapCodec;
import com.onter.onter_ic2.block.base.BaseMachineBlock;
import com.onter.onter_ic2.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class MetalFormerBlock extends BaseMachineBlock {
    public static final MapCodec<MetalFormerBlock> CODEC = simpleCodec(MetalFormerBlock::new);

    public MetalFormerBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                BlockEntity blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof MetalFormerBlockEntity metalFormer) {
                    metalFormer.cycleMode();
                    player.displayClientMessage(Component.translatable("message.onter_ic2.metal_former_mode",
                            metalFormer.getMode().getSerializedName()), true);
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MetalFormerBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : createTickerHelper(blockEntityType, ModBlockEntities.METAL_FORMER.get(),
                (lvl, pos, st, be) -> ((MetalFormerBlockEntity) be).tick(lvl, pos, st));
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, net.minecraft.util.RandomSource random) {
        if (state.getValue(LIT) && random.nextDouble() < 0.1) {
            level.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    com.onter.onter_ic2.init.ModSounds.COMPRESSOR_OP.get(), net.minecraft.sounds.SoundSource.BLOCKS, 0.7F, 1.0F, false);
        }
    }
}
