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
import net.minecraft.util.Mth;
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

public class ElectricWrenchItem extends Item {
    public static final int CAPACITY = 40000; // 10k EU
    public static final int USAGE_PER_OP = 2000; // 500 EU
    public static final int MAX_TRANSFER = 500;

    public ElectricWrenchItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        int energy = BatteryItem.getEnergy(stack);
        return Math.round(13.0f * (float) energy / (float) CAPACITY);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        float energyRatio = Math.max(0.0f, (float) BatteryItem.getEnergy(stack) / (float) CAPACITY);
        return Mth.hsvToRgb(energyRatio / 3.0f, 1.0f, 1.0f);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        Direction clickedFace = context.getClickedFace();
        ItemStack wrenchStack = context.getItemInHand();

        int storedEnergy = BatteryItem.getEnergy(wrenchStack);
        if (storedEnergy < USAGE_PER_OP && (player == null || !player.isCreative())) {
            if (level.isClientSide && player != null) {
                player.displayClientMessage(Component.literal("§cНедостаточно энергии в электроключе!"), true);
            }
            return InteractionResult.FAIL;
        }

        // 1. Energy Storage Block interaction
        if (block instanceof EnergyStorageBlock) {
            if (player != null && player.isShiftKeyDown()) {
                // Dismantle storage block safely
                if (!level.isClientSide) {
                    BlockEntity be = level.getBlockEntity(pos);
                    ItemStack dropStack = new ItemStack(block);
                    if (be instanceof EnergyStorageBlockEntity storage) {
                        Containers.dropContents(level, pos, storage.getDrops());
                    }
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                    level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, dropStack));
                    level.playSound(null, pos, ModSounds.WRENCH.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
                    if (player != null && !player.isCreative()) {
                        BatteryItem.extractEnergy(wrenchStack, USAGE_PER_OP, MAX_TRANSFER, false);
                    }
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            } else {
                // Set clicked face as output
                if (!level.isClientSide) {
                    Direction currentFacing = state.getValue(EnergyStorageBlock.FACING);
                    Direction newFacing = (clickedFace != currentFacing) ? clickedFace :
                            Direction.values()[(currentFacing.ordinal() + 1) % Direction.values().length];
                    level.setBlock(pos, state.setValue(EnergyStorageBlock.FACING, newFacing), 3);
                    level.playSound(null, pos, ModSounds.WRENCH.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
                    if (player != null && !player.isCreative()) {
                        BatteryItem.extractEnergy(wrenchStack, USAGE_PER_OP, MAX_TRANSFER, false);
                    }
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        // 2. Machine Block interaction
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
                    if (player != null && !player.isCreative()) {
                        BatteryItem.extractEnergy(wrenchStack, USAGE_PER_OP, MAX_TRANSFER, false);
                    }
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            } else {
                // Rotate horizontal facing
                if (!level.isClientSide) {
                    Direction currentFacing = state.getValue(BaseMachineBlock.FACING);
                    Direction newFacing = (clickedFace.getAxis().isHorizontal() && clickedFace != currentFacing) ? clickedFace : currentFacing.getClockWise();
                    level.setBlock(pos, state.setValue(BaseMachineBlock.FACING, newFacing), 3);
                    level.playSound(null, pos, ModSounds.WRENCH.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
                    if (player != null && !player.isCreative()) {
                        BatteryItem.extractEnergy(wrenchStack, USAGE_PER_OP, MAX_TRANSFER, false);
                    }
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        return super.useOn(context);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        int energy = BatteryItem.getEnergy(stack);
        tooltipComponents.add(Component.literal("§eЭнергия: §f" + (energy / 4) + " / " + (CAPACITY / 4) + " EU §7(" + energy + " / " + CAPACITY + " FE)"));
        tooltipComponents.add(Component.literal("§7ПКМ: Установка стороны вывода"));
        tooltipComponents.add(Component.literal("§7Shift + ПКМ: 100% безопасный демонтаж механизма"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
