package com.onter.onter_ic2.reactor;

import net.minecraft.world.item.ItemStack;

public interface IReactorComponent {
    void processChamber(ItemStack stack, IReactor reactor, int x, int y, boolean heatRun);
    boolean acceptUraniumPulse(ItemStack stack, IReactor reactor, ItemStack pulsingStack, int youX, int youY, int pulseX, int pulseY, boolean heatRun);
    boolean canStoreHeat(ItemStack stack, IReactor reactor, int x, int y);
    int getMaxCustomHeat(ItemStack stack, IReactor reactor, int x, int y);
    int getCurrentCustomHeat(ItemStack stack, IReactor reactor, int x, int y);
    int alterHeat(ItemStack stack, IReactor reactor, int x, int y, int heat);
    float influenceExplosion(ItemStack stack, IReactor reactor);
}
