package com.onter.onter_ic2.item.armor;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;

@EventBusSubscriber(modid = com.onter.onter_ic2.OnterIC2.MODID)
public class ArmorEventHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();
        float amount = event.getNewDamage();

        if (source == null || amount <= 0.0F || source.is(DamageTypeTags.BYPASSES_ENCHANTMENTS) || source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return;
        }

        float remainingDamage = amount;

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() != EquipmentSlot.Type.HUMANOID_ARMOR) continue;

            ItemStack stack = entity.getItemBySlot(slot);
            if (stack.getItem() instanceof ElectricArmorItem electricArmor) {
                double absorptionRatio = electricArmor.getBaseAbsorptionRatio() * electricArmor.getDamageAbsorptionRatio();
                if (absorptionRatio <= 0.0) continue;

                int energyPerDamage = electricArmor.getEnergyPerDamage();
                if (energyPerDamage <= 0) continue;

                int availableEnergy = ElectricArmorItem.getEnergy(stack);
                double maxAbsorbDamage = (double) availableEnergy / energyPerDamage;
                double absorbedDamage = Math.min(remainingDamage * absorptionRatio, maxAbsorbDamage);

                if (absorbedDamage > 0.0) {
                    electricArmor.absorbDamage(entity, stack, source, (float) absorbedDamage);
                    remainingDamage -= (float) absorbedDamage;
                    if (remainingDamage <= 0.0F) {
                        break;
                    }
                }
            }
        }

        event.setNewDamage(Math.max(0.0F, remainingDamage));
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onLivingFall(LivingFallEvent event) {
        LivingEntity entity = event.getEntity();
        ItemStack feetStack = entity.getItemBySlot(EquipmentSlot.FEET);

        if (feetStack.getItem() instanceof NanoArmorItem nanoBoots) {
            if (nanoBoots.absorbFall(feetStack, event.getDistance())) {
                event.setCanceled(true);
            }
        } else if (feetStack.getItem() instanceof QuantumArmorItem quantumBoots) {
            if (quantumBoots.absorbFall(feetStack, event.getDistance())) {
                event.setCanceled(true);
            }
        }
    }
}
