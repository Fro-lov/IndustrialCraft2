package com.onter.onter_ic2.reactor;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ReactorPlatingItem extends ReactorComponentItem {
    public final int maxHeatAddition;
    public final float explosionModifier;

    public ReactorPlatingItem(Properties properties, int maxHeatAddition, float explosionModifier) {
        super(properties, 0);
        this.maxHeatAddition = maxHeatAddition;
        this.explosionModifier = explosionModifier;
    }

    @Override
    public void processChamber(ItemStack stack, IReactor reactor, int x, int y, boolean heatRun) {
        // Platings passively increase max heat limit
    }

    @Override
    public float influenceExplosion(ItemStack stack, IReactor reactor) {
        return explosionModifier;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        if (maxHeatAddition > 0) {
            tooltip.add(Component.literal(String.format("Max Heat +%,d", maxHeatAddition)).withStyle(ChatFormatting.GREEN));
        }
    }
}
