package com.onter.onter_ic2.block.generators;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
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

public class SolarPanelBlock extends BaseEntityBlock {
    private final int dayGen;
    private final int nightGen;
    private final int capacity;
    private final Supplier<BlockEntityType<? extends SolarPanelBlockEntity>> blockEntityTypeSupplier;

    public SolarPanelBlock(int dayGen, int nightGen, int capacity,
                           Supplier<BlockEntityType<? extends SolarPanelBlockEntity>> blockEntityTypeSupplier,
                           Properties properties) {
        super(properties);
        this.dayGen = dayGen;
        this.nightGen = nightGen;
        this.capacity = capacity;
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
            if (blockEntity instanceof net.minecraft.world.MenuProvider menuProvider && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                serverPlayer.openMenu(menuProvider, pos);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof SolarPanelBlockEntity solar) {
                Containers.dropContents(level, pos, solar.getDrops());
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SolarPanelBlockEntity(blockEntityTypeSupplier.get(), pos, state, dayGen, nightGen, capacity);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : (lvl, p, st, be) -> {
            if (be instanceof SolarPanelBlockEntity solar) {
                solar.tick(lvl, p, st);
            }
        };
    }
}
