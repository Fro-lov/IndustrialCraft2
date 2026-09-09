package com.onter.onter_ic2.item.armor;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class QuantumArmorItem extends ElectricArmorItem {
    public static final int QUANTUM_CAPACITY = 40_000_000; // 10,000,000 EU
    public static final int QUANTUM_TRANSFER = 48_000;     // 12,000 EU/t
    public static final int QUANTUM_TIER = 4;

    public QuantumArmorItem(Type type, Properties properties) {
        super(Ic2ArmorMaterials.QUANTUM_SUIT, type, properties, QUANTUM_CAPACITY, QUANTUM_TRANSFER, QUANTUM_TIER);
    }

    @Override
    public int getEnergyPerDamage() {
        return 80_000; // 20,000 EU per damage point
    }

    @Override
    public double getDamageAbsorptionRatio() {
        return this.getType() == Type.CHESTPLATE ? 1.20 : 1.00; // 100% absorption
    }

    public boolean absorbFall(ItemStack stack, float distance) {
        int fallDamage = Math.max((int) distance - 10, 0);
        if (fallDamage <= 0) return true;

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
        if (!(entity instanceof Player player)) return;

        EquipmentSlot equipSlot = switch (this.getType()) {
            case HELMET -> EquipmentSlot.HEAD;
            case CHESTPLATE, BODY -> EquipmentSlot.CHEST;
            case LEGGINGS -> EquipmentSlot.LEGS;
            case BOOTS -> EquipmentSlot.FEET;
        };

        if (player.getItemBySlot(equipSlot) != stack) return;

        if (equipSlot == EquipmentSlot.HEAD) {
            if (!world.isClientSide()) {
                // Water breathing
                int air = player.getAirSupply();
                if (getEnergy(stack) >= 4000 && air < player.getMaxAirSupply()) {
                    player.setAirSupply(Math.min(player.getMaxAirSupply(), air + 200));
                    extractEnergy(stack, 4000, maxTransfer, false);
                }

                // Night vision
                if (getEnergy(stack) >= 4) {
                    extractEnergy(stack, 4, maxTransfer, false);
                    int light = world.getMaxLocalRawBrightness(BlockPos.containing(player.position()));
                    if (light <= 8) {
                        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 300, 0, false, false, false));
                    }
                }

                // Cleanse negative effects
                if (getEnergy(stack) >= 40000) {
                    for (MobEffectInstance effect : new LinkedList<>(player.getActiveEffects())) {
                        Holder<MobEffect> mobEffect = effect.getEffect();
                        if (mobEffect.equals(MobEffects.POISON) || mobEffect.equals(MobEffects.WITHER) || mobEffect.equals(MobEffects.BLINDNESS) || mobEffect.equals(MobEffects.CONFUSION)) {
                            extractEnergy(stack, 40000, maxTransfer, false);
                            player.removeEffect(mobEffect);
                        }
                    }
                }

                // Auto-feed if hungry
                if (getEnergy(stack) >= 4000 && player.getFoodData().needsFood()) {
                    player.getFoodData().eat(1, 0.5F);
                    extractEnergy(stack, 4000, maxTransfer, false);
                }
            }
        } else if (equipSlot == EquipmentSlot.CHEST) {
            if (!world.isClientSide() && player.isOnFire()) {
                player.clearFire();
            }
        } else if (equipSlot == EquipmentSlot.LEGS) {
            // Sprint speed boost
            if (getEnergy(stack) >= 4000 && player.isSprinting() && (player.onGround() || player.isInWater())) {
                extractEnergy(stack, 40, maxTransfer, false);
                float speed = player.isInWater() ? 0.08F : 0.18F;
                float yaw = player.getYRot() * (float) Math.PI / 180.0F;
                Vec3 motion = player.getDeltaMovement();
                player.setDeltaMovement(motion.x + (-Math.sin(yaw) * speed), motion.y, motion.z + (Math.cos(yaw) * speed));
            }
        } else if (equipSlot == EquipmentSlot.FEET) {
            // Super jump
            if (getEnergy(stack) >= 16000 && player.isSprinting() && !player.onGround() && player.getDeltaMovement().y > 0.0 && !player.isInWater()) {
                extractEnergy(stack, 40, maxTransfer, false);
                Vec3 motion = player.getDeltaMovement();
                player.setDeltaMovement(motion.x * 1.05, Math.min(motion.y + 0.05, 0.75), motion.z * 1.05);
            }
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        switch (this.getType()) {
            case HELMET -> tooltip.add(Component.translatable("tooltip.onter_ic2.quantum_helmet").withStyle(ChatFormatting.LIGHT_PURPLE));
            case CHESTPLATE -> tooltip.add(Component.translatable("tooltip.onter_ic2.quantum_chestplate").withStyle(ChatFormatting.LIGHT_PURPLE));
            case LEGGINGS -> tooltip.add(Component.translatable("tooltip.onter_ic2.quantum_leggings").withStyle(ChatFormatting.LIGHT_PURPLE));
            case BOOTS -> tooltip.add(Component.translatable("tooltip.onter_ic2.quantum_boots").withStyle(ChatFormatting.LIGHT_PURPLE));
        }
    }
}
