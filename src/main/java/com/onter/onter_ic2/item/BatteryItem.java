package com.onter.onter_ic2.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.nbt.CompoundTag;

import java.util.List;

public class BatteryItem extends Item {
    private final int capacity;
    private final int maxTransfer;

    public BatteryItem(int capacity, int maxTransfer, Properties properties) {
        super(properties.stacksTo(1));
        this.capacity = capacity;
        this.maxTransfer = maxTransfer;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getMaxTransfer() {
        return maxTransfer;
    }

    public static int getEnergy(ItemStack stack) {
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = customData.copyTag();
        return tag.getInt("Energy");
    }

    public static void setEnergy(ItemStack stack, int energy) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putInt("Energy", energy));
    }

    public static int receiveEnergy(ItemStack stack, int maxReceive, int capacity, int maxTransfer, boolean simulate) {
        int energy = getEnergy(stack);
        int energyReceived = Math.min(capacity - energy, Math.min(maxTransfer, maxReceive));
        if (!simulate) {
            setEnergy(stack, energy + energyReceived);
        }
        return energyReceived;
    }

    public static int extractEnergy(ItemStack stack, int maxExtract, int maxTransfer, boolean simulate) {
        int energy = getEnergy(stack);
        int energyExtracted = Math.min(energy, Math.min(maxTransfer, maxExtract));
        if (!simulate) {
            setEnergy(stack, energy - energyExtracted);
        }
        return energyExtracted;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        int energy = getEnergy(stack);
        return Math.round(13.0F * energy / capacity);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0x00FF00;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        int energy = getEnergy(stack);
        tooltipComponents.add(Component.translatable("tooltip.onter_ic2.energy", energy, capacity, energy / 4, capacity / 4));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
