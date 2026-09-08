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

public class PullingUpgradeItem extends UpgradeItem {
    public PullingUpgradeItem(Properties properties) {
        super(UpgradeType.PULLING, properties);
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
            case UP -> "Сверху (Up)";
            case DOWN -> "Снизу (Down)";
            case NORTH -> "С севера (North)";
            case SOUTH -> "С юга (South)";
            case WEST -> "С запада (West)";
            case EAST -> "С востока (East)";
        };
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        Direction clickedFace = context.getClickedFace();

        setDirection(stack, clickedFace);
        if (player != null && context.getLevel().isClientSide) {
            player.displayClientMessage(Component.literal("§a[IC2] Затягиватель настроен на сторону: §e" + getDirName(clickedFace)), true);
        }
        return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        Direction current = getDirection(stack);
        Direction next;
        if (current == null) {
            next = Direction.UP;
        } else if (current == Direction.EAST) {
            next = null;
        } else {
            next = Direction.values()[current.ordinal() + 1];
        }

        setDirection(stack, next);
        if (level.isClientSide) {
            player.displayClientMessage(Component.literal("§a[IC2] Затягиватель настроен на сторону: §e" + getDirName(next)), true);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("§6Улучшение «Затягиватель»"));
        tooltipComponents.add(Component.literal("§7Автоматически затягивает сырье из соседнего сундука/инвентаря."));
        tooltipComponents.add(Component.literal("§eНаправление: §f" + getDirName(getDirection(stack))));
        tooltipComponents.add(Component.literal("§7[ПКМ по стороне блока или в воздух для смены]"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
