package com.onter.onter_ic2.block.cables;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class CableBlock extends BaseEntityBlock {
    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty WEST = BooleanProperty.create("west");
    public static final BooleanProperty UP = BooleanProperty.create("up");
    public static final BooleanProperty DOWN = BooleanProperty.create("down");

    private static final VoxelShape CORE_SHAPE = Block.box(5.0, 5.0, 5.0, 11.0, 11.0, 11.0);
    private static final VoxelShape NORTH_SHAPE = Block.box(5.0, 5.0, 0.0, 11.0, 11.0, 5.0);
    private static final VoxelShape SOUTH_SHAPE = Block.box(5.0, 5.0, 11.0, 11.0, 11.0, 16.0);
    private static final VoxelShape WEST_SHAPE = Block.box(0.0, 5.0, 5.0, 5.0, 11.0, 11.0);
    private static final VoxelShape EAST_SHAPE = Block.box(11.0, 5.0, 5.0, 16.0, 11.0, 11.0);
    private static final VoxelShape DOWN_SHAPE = Block.box(5.0, 0.0, 5.0, 11.0, 5.0, 11.0);
    private static final VoxelShape UP_SHAPE = Block.box(5.0, 11.0, 5.0, 11.0, 16.0, 11.0);

    private final int maxTransfer;
    private final Supplier<BlockEntityType<? extends CableBlockEntity>> blockEntityTypeSupplier;

    public CableBlock(int maxTransfer, Supplier<BlockEntityType<? extends CableBlockEntity>> blockEntityTypeSupplier, Properties properties) {
        super(properties);
        this.maxTransfer = maxTransfer;
        this.blockEntityTypeSupplier = blockEntityTypeSupplier;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, false).setValue(SOUTH, false)
                .setValue(EAST, false).setValue(WEST, false)
                .setValue(UP, false).setValue(DOWN, false));
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
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape shape = CORE_SHAPE;
        if (state.getValue(NORTH)) shape = Shapes.or(shape, NORTH_SHAPE);
        if (state.getValue(SOUTH)) shape = Shapes.or(shape, SOUTH_SHAPE);
        if (state.getValue(WEST)) shape = Shapes.or(shape, WEST_SHAPE);
        if (state.getValue(EAST)) shape = Shapes.or(shape, EAST_SHAPE);
        if (state.getValue(DOWN)) shape = Shapes.or(shape, DOWN_SHAPE);
        if (state.getValue(UP)) shape = Shapes.or(shape, UP_SHAPE);
        return shape;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        return makeConnectionState(level, pos, this.defaultBlockState());
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (level instanceof Level actualLevel) {
            return makeConnectionState(actualLevel, pos, state);
        }
        return state;
    }

    private BlockState makeConnectionState(Level level, BlockPos pos, BlockState state) {
        return state
                .setValue(NORTH, connectsTo(level, pos.north(), Direction.SOUTH))
                .setValue(SOUTH, connectsTo(level, pos.south(), Direction.NORTH))
                .setValue(WEST, connectsTo(level, pos.west(), Direction.EAST))
                .setValue(EAST, connectsTo(level, pos.east(), Direction.WEST))
                .setValue(DOWN, connectsTo(level, pos.below(), Direction.UP))
                .setValue(UP, connectsTo(level, pos.above(), Direction.DOWN));
    }

    private boolean connectsTo(Level level, BlockPos pos, Direction side) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof CableBlock) return true;
        return level.getCapability(Capabilities.EnergyStorage.BLOCK, pos, side) != null;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CableBlockEntity(blockEntityTypeSupplier.get(), pos, state, maxTransfer);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : (lvl, p, st, be) -> {
            if (be instanceof CableBlockEntity cable) {
                cable.tick(lvl, p, st);
            }
        };
    }
}
