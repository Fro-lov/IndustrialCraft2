package com.onter.onter_ic2.item;

import com.onter.onter_ic2.block.base.BaseMachineBlock;
import com.onter.onter_ic2.block.base.BaseMachineBlockEntity;
import com.onter.onter_ic2.block.storage.EnergyStorageBlock;
import com.onter.onter_ic2.block.storage.EnergyStorageBlockEntity;
import com.onter.onter_ic2.init.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class WrenchItem extends Item {
    public WrenchItem(Properties properties) {
        super(properties.durability(160));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        Direction clickedFace = context.getClickedFace();

        // 1. Energy Storage Block interaction
        if (block instanceof EnergyStorageBlock) {
            if (player != null && player.isShiftKeyDown()) {
                // Dismantle storage block safely
                if (!level.isClientSide) {
                    BlockEntity be = level.getBlockEntity(pos);
                    ItemStack dropStack = new ItemStack(block);
                    if (be instanceof EnergyStorageBlockEntity storage) {
                        // Drop inventory contents
                        Containers.dropContents(level, pos, storage.getDrops());
                    }
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                    level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, dropStack));
                    level.playSound(null, pos, ModSounds.WRENCH.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
                    context.getItemInHand().hurtAndBreak(1, player, player.getEquipmentSlotForItem(context.getItemInHand()));
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            } else {
                // Rotate output face (FACING)
                if (!level.isClientSide) {
                    Direction currentFacing = state.getValue(EnergyStorageBlock.FACING);
                    Direction newFacing;
                    if (clickedFace != currentFacing) {
                        newFacing = clickedFace;
                    } else {
                        // Cycle to next direction
                        Direction[] dirs = Direction.values();
                        newFacing = dirs[(currentFacing.ordinal() + 1) % dirs.length];
                    }
                    level.setBlock(pos, state.setValue(EnergyStorageBlock.FACING, newFacing), 3);
                    level.playSound(null, pos, ModSounds.WRENCH.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
                    if (player != null) {
                        context.getItemInHand().hurtAndBreak(1, player, player.getEquipmentSlotForItem(context.getItemInHand()));
                    }
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        // 2. Base Machine Block interaction
        if (block instanceof BaseMachineBlock) {
            if (player != null && player.isShiftKeyDown()) {
                // Dismantle machine safely
                if (!level.isClientSide) {
                    BlockEntity be = level.getBlockEntity(pos);
                    ItemStack dropStack = new ItemStack(block);
                    if (be instanceof BaseMachineBlockEntity machine) {
                        Containers.dropContents(level, pos, machine.getDrops());
                    }
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                    level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, dropStack));
                    level.playSound(null, pos, ModSounds.WRENCH.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
                    context.getItemInHand().hurtAndBreak(1, player, player.getEquipmentSlotForItem(context.getItemInHand()));
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            } else {
                // Rotate horizontal facing
                if (!level.isClientSide) {
                    Direction currentFacing = state.getValue(BaseMachineBlock.FACING);
                    Direction newFacing;
                    if (clickedFace.getAxis().isHorizontal() && clickedFace != currentFacing) {
                        newFacing = clickedFace;
                    } else {
                        newFacing = currentFacing.getClockWise();
                    }
                    level.setBlock(pos, state.setValue(BaseMachineBlock.FACING, newFacing), 3);
                    level.playSound(null, pos, ModSounds.WRENCH.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
                    if (player != null) {
                        context.getItemInHand().hurtAndBreak(1, player, player.getEquipmentSlotForItem(context.getItemInHand()));
                    }
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        return super.useOn(context);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("§6Гаечный ключ (Wrench)"));
        tooltipComponents.add(Component.literal("§7- §eПКМ: §fСмена направления вывода энергии/лицевой стороны"));
        tooltipComponents.add(Component.literal("§7- §eShift+ПКМ: §fБезопасный демонтаж механизма/хранилища"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
