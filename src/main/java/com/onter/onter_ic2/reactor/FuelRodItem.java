package com.onter.onter_ic2.reactor;

import com.onter.onter_ic2.init.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.function.Supplier;

public class FuelRodItem extends ReactorComponentItem {
    public final int numberOfCells;
    public final boolean isMox;
    public final Supplier<ItemStack> depletedSupplier;

    public FuelRodItem(Properties properties, int cells, boolean isMox, Supplier<ItemStack> depletedSupplier) {
        super(properties, isMox ? 10000 : 20000);
        this.numberOfCells = cells;
        this.isMox = isMox;
        this.depletedSupplier = depletedSupplier;
    }

    @Override
    public void processChamber(ItemStack stack, IReactor reactor, int x, int y, boolean heatRun) {
        if (!reactor.produceEnergy()) return;

        int basePulses = 1 + this.numberOfCells / 2;

        for (int iteration = 0; iteration < this.numberOfCells; iteration++) {
            int pulses = basePulses;
            if (!heatRun) {
                for (int i = 0; i < pulses; i++) {
                    this.acceptUraniumPulse(stack, reactor, stack, x, y, x, y, heatRun);
                }

                pulses += checkPulseable(reactor, x - 1, y, stack, x, y, heatRun)
                        + checkPulseable(reactor, x + 1, y, stack, x, y, heatRun)
                        + checkPulseable(reactor, x, y - 1, stack, x, y, heatRun)
                        + checkPulseable(reactor, x, y + 1, stack, x, y, heatRun);
            } else {
                pulses += checkPulseable(reactor, x - 1, y, stack, x, y, heatRun)
                        + checkPulseable(reactor, x + 1, y, stack, x, y, heatRun)
                        + checkPulseable(reactor, x, y - 1, stack, x, y, heatRun)
                        + checkPulseable(reactor, x, y + 1, stack, x, y, heatRun);

                int heat = (pulses * (pulses + 1) / 2) * 4;
                Queue<Coord> heatAcceptors = new ArrayDeque<>();
                checkHeatAcceptor(reactor, x - 1, y, heatAcceptors);
                checkHeatAcceptor(reactor, x + 1, y, heatAcceptors);
                checkHeatAcceptor(reactor, x, y - 1, heatAcceptors);
                checkHeatAcceptor(reactor, x, y + 1, heatAcceptors);

                while (!heatAcceptors.isEmpty() && heat > 0) {
                    int dheat = heat / heatAcceptors.size();
                    heat -= dheat;
                    Coord acceptor = heatAcceptors.remove();
                    IReactorComponent acceptorComp = (IReactorComponent) acceptor.stack.getItem();
                    dheat = acceptorComp.alterHeat(acceptor.stack, reactor, acceptor.x, acceptor.y, dheat);
                    heat += dheat;
                }

                if (heat > 0) {
                    reactor.addHeat(heat);
                }
            }
        }

        if (!heatRun) {
            int currentUse = getCustomDamage(stack);
            if (currentUse >= this.maxDamageOrHeat - 1) {
                reactor.setItemAt(x, y, depletedSupplier != null ? depletedSupplier.get() : ItemStack.EMPTY);
            } else {
                setCustomDamage(stack, currentUse + 1);
            }
        }
    }

    private static int checkPulseable(IReactor reactor, int x, int y, ItemStack stack, int mex, int mey, boolean heatRun) {
        ItemStack other = reactor.getItemAt(x, y);
        if (!other.isEmpty() && other.getItem() instanceof IReactorComponent comp) {
            return comp.acceptUraniumPulse(other, reactor, stack, x, y, mex, mey, heatRun) ? 1 : 0;
        }
        return 0;
    }

    private static void checkHeatAcceptor(IReactor reactor, int x, int y, Queue<Coord> heatAcceptors) {
        ItemStack stack = reactor.getItemAt(x, y);
        if (!stack.isEmpty() && stack.getItem() instanceof IReactorComponent comp && comp.canStoreHeat(stack, reactor, x, y)) {
            heatAcceptors.add(new Coord(stack, x, y));
        }
    }

    @Override
    public boolean acceptUraniumPulse(ItemStack stack, IReactor reactor, ItemStack pulsingStack, int youX, int youY, int pulseX, int pulseY, boolean heatRun) {
        if (!heatRun) {
            if (isMox) {
                float heatRatio = (float) reactor.getHeat() / (float) Math.max(1, reactor.getMaxHeat());
                float moxOutput = 4.0F * heatRatio + 1.0F; // Scales up to 5x EU at max heat!
                reactor.addOutput(moxOutput);
            } else {
                reactor.addOutput(1.0F);
            }
        }
        return true;
    }

    @Override
    public float influenceExplosion(ItemStack stack, IReactor reactor) {
        return 2.0F * this.numberOfCells;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        if (isMox) {
            tooltip.add(Component.translatable("tooltip.onter_ic2.mox_fuel").withStyle(ChatFormatting.GREEN));
        } else {
            tooltip.add(Component.translatable("tooltip.onter_ic2.uranium_fuel").withStyle(ChatFormatting.DARK_GREEN));
        }
    }

    private record Coord(ItemStack stack, int x, int y) {}
}
