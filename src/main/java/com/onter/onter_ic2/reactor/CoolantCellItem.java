package com.onter.onter_ic2.reactor;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CoolantCellItem extends ReactorComponentItem {
    public CoolantCellItem(Properties properties, int heatCapacity) {
        super(properties, heatCapacity);
    }

    @Override
    public boolean canStoreHeat(ItemStack stack, IReactor reactor, int x, int y) {
        return true;
    }

    @Override
    public int alterHeat(ItemStack stack, IReactor reactor, int x, int y, int heat) {
        int current = getCurrentCustomHeat(stack, reactor, x, y);
        int after = current + heat;
        if (after >= maxDamageOrHeat) {
            // Coolant cell is depleted / destroyed when filled with max heat
            reactor.setItemAt(x, y, ItemStack.EMPTY);
            return after - maxDamageOrHeat;
        } else if (after < 0) {
            setCustomDamage(stack, 0);
            return after;
        } else {
            setCustomDamage(stack, after);
            return 0;
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.onter_ic2.coolant_cell").withStyle(ChatFormatting.AQUA));
    }
}
