package com.onter.onter_ic2.reactor;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface IReactor {
    Level getReactorLevel();
    BlockPos getReactorPos();
    int getHeat();
    void setHeat(int heat);
    void addHeat(int heat);
    int getMaxHeat();
    void addEmitHeat(int heat);
    void addOutput(float energy);
    ItemStack getItemAt(int x, int y);
    void setItemAt(int x, int y, ItemStack stack);
    boolean produceEnergy();
    int getGridWidth();
    int getGridHeight();
}
