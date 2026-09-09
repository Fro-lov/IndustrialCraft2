package com.onter.onter_ic2.reactor;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HeatExchangerItem extends ReactorComponentItem {
    public final int switchSide;
    public final int switchReactor;

    public HeatExchangerItem(Properties properties, int heatStorage, int switchSide, int switchReactor) {
        super(properties, heatStorage);
        this.switchSide = switchSide;
        this.switchReactor = switchReactor;
    }

    @Override
    public boolean canStoreHeat(ItemStack stack, IReactor reactor, int x, int y) {
        return true;
    }

    @Override
    public void processChamber(ItemStack stack, IReactor reactor, int x, int y, boolean heatRun) {
        if (!heatRun) return;

        // Exchange heat with 4 adjacent components
        if (switchSide > 0) {
            exchangeWithSide(reactor, stack, x - 1, y, x, y);
            exchangeWithSide(reactor, stack, x + 1, y, x, y);
            exchangeWithSide(reactor, stack, x, y - 1, x, y);
            exchangeWithSide(reactor, stack, x, y + 1, x, y);
        }

        // Exchange heat with reactor core
        if (switchReactor > 0) {
            int reactorHeat = reactor.getHeat();
            int maxReactorHeat = Math.max(1, reactor.getMaxHeat());
            double reactorRatio = (double) reactorHeat / maxReactorHeat;

            int myHeat = getCurrentCustomHeat(stack, reactor, x, y);
            double myRatio = (double) myHeat / maxDamageOrHeat;

            if (reactorRatio > myRatio) {
                int transfer = Math.min(reactorHeat, (int) Math.min(switchReactor, (reactorRatio - myRatio) * maxDamageOrHeat));
                if (transfer > 0) {
                    reactor.setHeat(reactorHeat - transfer);
                    alterHeat(stack, reactor, x, y, transfer);
                }
            } else if (myRatio > reactorRatio) {
                int transfer = Math.min(myHeat, (int) Math.min(switchReactor, (myRatio - reactorRatio) * maxDamageOrHeat));
                if (transfer > 0) {
                    alterHeat(stack, reactor, x, y, -transfer);
                    reactor.setHeat(reactorHeat + transfer);
                }
            }
        }
    }

    private void exchangeWithSide(IReactor reactor, ItemStack myStack, int targetX, int targetY, int myX, int myY) {
        ItemStack target = reactor.getItemAt(targetX, targetY);
        if (!target.isEmpty() && target.getItem() instanceof IReactorComponent comp && comp.canStoreHeat(target, reactor, targetX, targetY)) {
            int targetHeat = comp.getCurrentCustomHeat(target, reactor, targetX, targetY);
            int targetMax = comp.getMaxCustomHeat(target, reactor, targetX, targetY);
            if (targetMax <= 0) return;

            double targetRatio = (double) targetHeat / targetMax;
            int myHeat = getCurrentCustomHeat(myStack, reactor, myX, myY);
            double myRatio = (double) myHeat / maxDamageOrHeat;

            if (targetRatio > myRatio) {
                int transfer = Math.min(targetHeat, (int) Math.min(switchSide, (targetRatio - myRatio) * maxDamageOrHeat));
                if (transfer > 0) {
                    comp.alterHeat(target, reactor, targetX, targetY, -transfer);
                    alterHeat(myStack, reactor, myX, myY, transfer);
                }
            } else if (myRatio > targetRatio) {
                int transfer = Math.min(myHeat, (int) Math.min(switchSide, (myRatio - targetRatio) * maxDamageOrHeat));
                if (transfer > 0) {
                    alterHeat(myStack, reactor, myX, myY, -transfer);
                    comp.alterHeat(target, reactor, targetX, targetY, transfer);
                }
            }
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        if (switchSide > 0) tooltip.add(Component.literal(String.format("Component Transfer: %d/t", switchSide)).withStyle(ChatFormatting.AQUA));
        if (switchReactor > 0) tooltip.add(Component.literal(String.format("Reactor Transfer: %d/t", switchReactor)).withStyle(ChatFormatting.BLUE));
    }
}
