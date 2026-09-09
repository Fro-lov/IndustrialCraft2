package com.onter.onter_ic2.item.armor;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NanoArmorItem extends ElectricArmorItem {
    public static final int NANO_CAPACITY = 4_000_000; // 1,000,000 EU
    public static final int NANO_TRANSFER = 6_400;     // 1,600 EU/t
    public static final int NANO_TIER = 3;

    public NanoArmorItem(Type type, Properties properties) {
        super(Ic2ArmorMaterials.NANO_SUIT, type, properties, NANO_CAPACITY, NANO_TRANSFER, NANO_TIER);
    }

    @Override
    public int getEnergyPerDamage() {
        return 20_000; // 5,000 EU per damage point
    }

    @Override
    public double getDamageAbsorptionRatio() {
        return 0.90; // 90% absorption
    }

    public boolean absorbFall(ItemStack stack, float distance) {
        int fallDamage = Math.max((int) distance - 3, 0);
        if (fallDamage <= 0) return true;
        if (fallDamage >= 8) return false;

        int energyCost = getEnergyPerDamage() * fallDamage;
        if (getEnergy(stack) >= energyCost) {
            extractEnergy(stack, energyCost, maxTransfer, false);
            return true;
        }
        return false;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level world, @NotNull Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if (!world.isClientSide() && entity instanceof Player player) {
            if (this.getType() == Type.HELMET && player.getItemBySlot(EquipmentSlot.HEAD) == stack) {
                if (getEnergy(stack) >= 4) {
                    extractEnergy(stack, 4, maxTransfer, false);
                    int light = world.getMaxLocalRawBrightness(BlockPos.containing(player.position()));
                    if (light <= 8) {
                        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 300, 0, false, false, false));
                    }
                }
            }
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        if (this.getType() == Type.HELMET) {
            tooltip.add(Component.translatable("tooltip.onter_ic2.nano_helmet").withStyle(ChatFormatting.AQUA));
        } else if (this.getType() == Type.BOOTS) {
            tooltip.add(Component.translatable("tooltip.onter_ic2.nano_boots").withStyle(ChatFormatting.AQUA));
        }
    }
}
