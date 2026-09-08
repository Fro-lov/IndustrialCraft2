package com.onter.onter_ic2.item;

import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FluidPullingUpgradeItem extends UpgradeItem {
    public FluidPullingUpgradeItem(Properties properties) {
        super(UpgradeType.FLUID_PULLING, properties);
    }

    @Nullable
    public static Direction getDirection(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            CompoundTag tag = customData.copyTag();
            if (tag.contains("Direction")) {
                return Direction.byName(tag.getString("Direction"));
            }
        }
        return null;
    }

    public static void setDirection(ItemStack stack, @Nullable Direction dir) {
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = customData.copyTag();
        if (dir != null) {
            tag.putString("Direction", dir.getName());
        } else {
            tag.remove("Direction");
        }
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public static String getDirName(@Nullable Direction dir) {
        if (dir == null) return "Любое направление (Авто)";
        return switch (dir) {
            case UP -> "Вверх (Up)";
            case DOWN -> "Вниз (Down)";
            case NORTH -> "Север (North)";
            case SOUTH -> "Юг (South)";
            case WEST -> "Запад (West)";
            case EAST -> "Восток (East)";
        };
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        Direction clickedFace = context.getClickedFace();

        setDirection(stack, clickedFace);
        if (player != null && context.getLevel().isClientSide) {
            player.displayClientMessage(Component.literal("§a[IC2] Жидкостный затягиватель настроен на: §e" + getDirName(clickedFace)), true);
        }
        return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        Direction current = getDirection(stack);
        // Order: West(0), East(1), Down(2), Up(3), North(4), South(5), null(Auto)
        Direction[] order = new Direction[]{Direction.WEST, Direction.EAST, Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH};
        Direction next = null;
        if (current == null) {
            next = order[0];
        } else {
            for (int i = 0; i < order.length; i++) {
                if (order[i] == current) {
                    if (i + 1 < order.length) {
                        next = order[i + 1];
                    } else {
                        next = null;
                    }
                    break;
                }
            }
        }

        setDirection(stack, next);
        if (level.isClientSide) {
            player.displayClientMessage(Component.literal("§a[IC2] Жидкостный затягиватель настроен на: §e" + getDirName(next)), true);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("§6Улучшение «Жидкостный затягиватель»"));
        tooltipComponents.add(Component.literal("§7Автоматически затягивает жидкость из соседнего резервуара/трубы."));
        tooltipComponents.add(Component.literal("§eНаправление: §f" + getDirName(getDirection(stack))));
        tooltipComponents.add(Component.literal("§7[ПКМ по стороне блока или в воздух для смены]"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
