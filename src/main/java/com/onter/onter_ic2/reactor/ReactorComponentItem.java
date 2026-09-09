package com.onter.onter_ic2.reactor;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ReactorComponentItem extends Item implements IReactorComponent {
    protected final int maxDamageOrHeat;

    public ReactorComponentItem(Properties properties, int maxDamageOrHeat) {
        super(properties.stacksTo(1));
        this.maxDamageOrHeat = maxDamageOrHeat;
    }

    public static int getCustomDamage(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            return customData.copyTag().getInt("adv_damage");
        }
        return 0;
    }

    public static void setCustomDamage(ItemStack stack, int damage) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.putInt("adv_damage", Math.max(0, damage));
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public int getMaxDamageOrHeat() {
        return maxDamageOrHeat;
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return getCustomDamage(stack) > 0;
    }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        if (maxDamageOrHeat <= 0) return 0;
        float ratio = 1.0F - (float) getCustomDamage(stack) / (float) maxDamageOrHeat;
        return Math.round(13.0F * Math.max(0.0F, Math.min(1.0F, ratio)));
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        if (maxDamageOrHeat <= 0) return 0xFFFFFF;
        float ratio = 1.0F - (float) getCustomDamage(stack) / (float) maxDamageOrHeat;
        return Mth.hsvToRgb(Math.max(0.0F, ratio) / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public void processChamber(ItemStack stack, IReactor reactor, int x, int y, boolean heatRun) {}

    @Override
    public boolean acceptUraniumPulse(ItemStack stack, IReactor reactor, ItemStack pulsingStack, int youX, int youY, int pulseX, int pulseY, boolean heatRun) {
        return false;
    }

    @Override
    public boolean canStoreHeat(ItemStack stack, IReactor reactor, int x, int y) {
        return false;
    }

    @Override
    public int getMaxCustomHeat(ItemStack stack, IReactor reactor, int x, int y) {
        return maxDamageOrHeat;
    }

    @Override
    public int getCurrentCustomHeat(ItemStack stack, IReactor reactor, int x, int y) {
        return getCustomDamage(stack);
    }

    @Override
    public int alterHeat(ItemStack stack, IReactor reactor, int x, int y, int heat) {
        int current = getCurrentCustomHeat(stack, reactor, x, y);
        int max = getMaxCustomHeat(stack, reactor, x, y);
        int after = current + heat;
        if (after > max) {
            setCustomDamage(stack, max);
            return after - max;
        } else if (after < 0) {
            setCustomDamage(stack, 0);
            return after;
        } else {
            setCustomDamage(stack, after);
            return 0;
        }
    }

    @Override
    public float influenceExplosion(ItemStack stack, IReactor reactor) {
        return 0;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        if (maxDamageOrHeat > 0) {
            int used = getCustomDamage(stack);
            tooltip.add(Component.literal(String.format("%,d / %,d", maxDamageOrHeat - used, maxDamageOrHeat)).withStyle(ChatFormatting.GRAY));
        }
    }
}
