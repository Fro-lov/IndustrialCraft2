package com.onter.onter_ic2.reactor;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HeatVentItem extends ReactorComponentItem {
    public final int selfVent;
    public final int reactorVent;
    public final int componentVent;

    public HeatVentItem(Properties properties, int heatStorage, int selfVent, int reactorVent, int componentVent) {
        super(properties, heatStorage);
        this.selfVent = selfVent;
        this.reactorVent = reactorVent;
        this.componentVent = componentVent;
    }

    @Override
    public boolean canStoreHeat(ItemStack stack, IReactor reactor, int x, int y) {
        return maxDamageOrHeat > 0;
    }

    @Override
    public void processChamber(ItemStack stack, IReactor reactor, int x, int y, boolean heatRun) {
        if (!heatRun) return;

        // Component vent logic (cools 4 adjacent components)
        if (componentVent > 0) {
            coolComponent(reactor, x - 1, y, componentVent);
            coolComponent(reactor, x + 1, y, componentVent);
            coolComponent(reactor, x, y - 1, componentVent);
            coolComponent(reactor, x, y + 1, componentVent);
        }

        // Reactor vent logic (pulls heat from reactor core into itself)
        if (reactorVent > 0) {
            int rheat = reactor.getHeat();
            int drain = Math.min(rheat, reactorVent);
            if (drain > 0) {
                reactor.setHeat(rheat - drain);
                alterHeat(stack, reactor, x, y, drain);
            }
        }

        // Self vent logic (dissipates heat stored in this vent into the environment)
        if (selfVent > 0) {
            int self = alterHeat(stack, reactor, x, y, -selfVent);
            if (self <= 0) {
                reactor.addEmitHeat(self + selfVent);
            }
        }
    }

    private void coolComponent(IReactor reactor, int x, int y, int amount) {
        ItemStack target = reactor.getItemAt(x, y);
        if (!target.isEmpty() && target.getItem() instanceof IReactorComponent comp && comp.canStoreHeat(target, reactor, x, y)) {
            comp.alterHeat(target, reactor, x, y, -amount);
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        if (selfVent > 0) tooltip.add(Component.literal(String.format("Self Vent: %d/t", selfVent)).withStyle(ChatFormatting.AQUA));
        if (reactorVent > 0) tooltip.add(Component.literal(String.format("Reactor Vent: %d/t", reactorVent)).withStyle(ChatFormatting.BLUE));
        if (componentVent > 0) tooltip.add(Component.literal(String.format("Component Vent: %d/t", componentVent)).withStyle(ChatFormatting.GOLD));
    }
}
