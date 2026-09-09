package com.onter.onter_ic2.reactor;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NeutronReflectorItem extends ReactorComponentItem {
    public NeutronReflectorItem(Properties properties, int maxDurability) {
        super(properties, maxDurability);
    }

    @Override
    public boolean acceptUraniumPulse(ItemStack stack, IReactor reactor, ItemStack pulsingStack, int youX, int youY, int pulseX, int pulseY, boolean heatRun) {
        if (!heatRun && maxDamageOrHeat > 0) {
            int current = getCustomDamage(stack);
            if (current >= maxDamageOrHeat - 1) {
                reactor.setItemAt(youX, youY, ItemStack.EMPTY);
            } else {
                setCustomDamage(stack, current + 1);
            }
        }
        return true;
    }

    @Override
    public float influenceExplosion(ItemStack stack, IReactor reactor) {
        return -1.0F;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        if (maxDamageOrHeat <= 0) {
            tooltip.add(Component.translatable("tooltip.onter_ic2.infinite_durability").withStyle(ChatFormatting.GOLD));
        }
    }
}
