package com.onter.onter_ic2.block.storage;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class EnergyStorageBlock extends BaseEntityBlock {
    private final String nameKey;
    private final int capacity;
    private final int maxTransfer;
    private final Supplier<BlockEntityType<? extends EnergyStorageBlockEntity>> blockEntityTypeSupplier;

    public EnergyStorageBlock(String nameKey, int capacity, int maxTransfer,
                              Supplier<BlockEntityType<? extends EnergyStorageBlockEntity>> blockEntityTypeSupplier,
                              Properties properties) {
        super(properties);
        this.nameKey = nameKey;
        this.capacity = capacity;
        this.maxTransfer = maxTransfer;
        this.blockEntityTypeSupplier = blockEntityTypeSupplier;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof MenuProvider menuProvider && player instanceof ServerPlayer serverPlayer) {
                serverPlayer.openMenu(menuProvider, pos);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof EnergyStorageBlockEntity storage) {
                Containers.dropContents(level, pos, storage.getDrops());
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EnergyStorageBlockEntity(blockEntityTypeSupplier.get(), pos, state, nameKey, capacity, maxTransfer);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : (lvl, p, st, be) -> {
            if (be instanceof EnergyStorageBlockEntity storage) {
                storage.tick(lvl, p, st);
            }
        };
    }
}
