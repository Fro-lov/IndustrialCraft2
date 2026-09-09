package com.onter.onter_ic2.block.cables;

import com.onter.onter_ic2.energy.network.EnergyNetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
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

public class CableBlock extends Block implements EntityBlock {
    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty WEST = BooleanProperty.create("west");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty UP = BooleanProperty.create("up");
    public static final BooleanProperty DOWN = BooleanProperty.create("down");

    private static final VoxelShape CORE = Block.box(6, 6, 6, 10, 10, 10);
    private static final VoxelShape SHAPE_N = Block.box(6, 6, 0, 10, 10, 6);
    private static final VoxelShape SHAPE_S = Block.box(6, 6, 10, 10, 10, 16);
    private static final VoxelShape SHAPE_W = Block.box(0, 6, 6, 6, 10, 10);
    private static final VoxelShape SHAPE_E = Block.box(10, 6, 6, 16, 10, 10);
    private static final VoxelShape SHAPE_U = Block.box(6, 10, 6, 10, 16, 10);
    private static final VoxelShape SHAPE_D = Block.box(6, 0, 6, 10, 6, 10);

    private final int maxTransfer;
    private final Supplier<BlockEntityType<? extends CableBlockEntity>> blockEntityTypeSupplier;

    public CableBlock(int maxTransfer, Supplier<BlockEntityType<? extends CableBlockEntity>> blockEntityTypeSupplier, Properties properties) {
        super(properties);
        this.maxTransfer = maxTransfer;
        this.blockEntityTypeSupplier = blockEntityTypeSupplier;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false)
                .setValue(EAST, false)
                .setValue(UP, false)
                .setValue(DOWN, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, WEST, EAST, UP, DOWN);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape shape = CORE;
        if (state.getValue(NORTH)) shape = Shapes.or(shape, SHAPE_N);
        if (state.getValue(SOUTH)) shape = Shapes.or(shape, SHAPE_S);
        if (state.getValue(WEST)) shape = Shapes.or(shape, SHAPE_W);
        if (state.getValue(EAST)) shape = Shapes.or(shape, SHAPE_E);
        if (state.getValue(UP)) shape = Shapes.or(shape, SHAPE_U);
        if (state.getValue(DOWN)) shape = Shapes.or(shape, SHAPE_D);
        return shape;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        return makeConnectionState(level, pos, defaultBlockState());
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (level instanceof Level actualLevel) {
            return makeConnectionState(actualLevel, pos, state);
        }
        return state;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, isMoving);
        if (!level.isClientSide) {
            EnergyNetworkManager.invalidateAt(level, pos);
        }
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
}
