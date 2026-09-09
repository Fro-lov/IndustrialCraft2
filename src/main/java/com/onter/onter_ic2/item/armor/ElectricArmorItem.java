package com.onter.onter_ic2.item.armor;

import com.onter.onter_ic2.item.BatteryItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class ElectricArmorItem extends ArmorItem {
    protected final int capacity;
    protected final int maxTransfer;
    protected final int tier;

    public ElectricArmorItem(Holder<ArmorMaterial> material, Type type, Properties properties, int capacity, int maxTransfer, int tier) {
        super(material, type, properties.stacksTo(1));
        this.capacity = capacity;
        this.maxTransfer = maxTransfer;
        this.tier = tier;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getMaxTransfer() {
        return maxTransfer;
    }

    public int getTier() {
        return tier;
    }

    public abstract int getEnergyPerDamage();

    public abstract double getDamageAbsorptionRatio();

    public static int getEnergy(ItemStack stack) {
        return BatteryItem.getEnergy(stack);
    }

    public static void setEnergy(ItemStack stack, int energy) {
        BatteryItem.setEnergy(stack, energy);
    }

    public static int receiveEnergy(ItemStack stack, int maxReceive, int capacity, int maxTransfer, boolean simulate) {
        return BatteryItem.receiveEnergy(stack, maxReceive, capacity, maxTransfer, simulate);
    }

    public static int extractEnergy(ItemStack stack, int maxExtract, int maxTransfer, boolean simulate) {
        return BatteryItem.extractEnergy(stack, maxExtract, maxTransfer, simulate);
    }

    public double getBaseAbsorptionRatio() {
        return switch (this.getType()) {
            case HELMET -> 0.15;
            case CHESTPLATE, BODY -> 0.40;
            case LEGGINGS -> 0.30;
            case BOOTS -> 0.15;
        };
    }

    public boolean absorbDamage(LivingEntity entity, ItemStack stack, DamageSource source, float amount) {
        int energyPerDmg = getEnergyPerDamage();
        if (energyPerDmg <= 0) return false;
        int currentEnergy = getEnergy(stack);
        int needed = (int) (amount * energyPerDmg);
        int toUse = Math.min(currentEnergy, needed);
        if (toUse > 0) {
            extractEnergy(stack, toUse, maxTransfer, false);
            return true;
        }
        return false;
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        return Math.round(13.0F * (float) getEnergy(stack) / (float) capacity);
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        float ratio = Math.max(0.0F, (float) getEnergy(stack) / (float) capacity);
        return Mth.hsvToRgb(ratio / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        int energy = getEnergy(stack);
        int eu = energy / 4;
        int maxEu = capacity / 4;
        tooltip.add(Component.literal(String.format("%,d / %,d EU", eu, maxEu)).withStyle(ChatFormatting.YELLOW));
        tooltip.add(Component.literal(String.format("%,d / %,d FE", energy, capacity)).withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.translatable("tooltip.onter_ic2.tier", tier).withStyle(ChatFormatting.BLUE));
    }
}
