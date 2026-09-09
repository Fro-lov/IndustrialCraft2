package com.onter.onter_ic2.item.armor;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SolarQuantumHelmetItem extends QuantumArmorItem {
    private final int dayPower;   // in EU/t (512)
    private final int nightPower; // in EU/t (64)

    public SolarQuantumHelmetItem(Properties properties, int dayPower, int nightPower) {
        super(Type.HELMET, properties.rarity(Rarity.RARE));
        this.dayPower = dayPower;
        this.nightPower = nightPower;
    }

    public int getDayPower() {
        return dayPower;
    }

    public int getNightPower() {
        return nightPower;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level world, @NotNull Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if (world.isClientSide() || !(entity instanceof Player player)) return;
        if (player.getItemBySlot(EquipmentSlot.HEAD) != stack) return;

        BlockPos headPos = player.blockPosition().above();
        if (!world.dimensionType().hasSkyLight() || !world.canSeeSky(headPos)) {
            return;
        }

        boolean canRain = world.getBiome(headPos).value().getPrecipitationAt(headPos) != Biome.Precipitation.NONE;
        boolean isDay = world.isDay() && (!canRain || (!world.isRaining() && !world.isThundering()));
        int currentPowerEU = isDay ? dayPower : nightPower;
        int currentPowerFE = currentPowerEU * 4;

        if (currentPowerFE <= 0) return;

        int remainingFE = currentPowerFE;

        // 1. Charge worn armor (FEET, LEGS, CHEST)
        for (EquipmentSlot slotType : new EquipmentSlot[]{EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST}) {
            ItemStack armorPiece = player.getItemBySlot(slotType);
            remainingFE = chargeStack(armorPiece, remainingFE);
            if (remainingFE <= 0) break;
        }

        // 2. Charge offhand
        if (remainingFE > 0) {
            for (ItemStack offhandStack : player.getInventory().offhand) {
                remainingFE = chargeStack(offhandStack, remainingFE);
                if (remainingFE <= 0) break;
            }
        }

        // 3. Charge main inventory
        if (remainingFE > 0) {
            for (ItemStack invStack : player.getInventory().items) {
                if (invStack != stack) {
                    remainingFE = chargeStack(invStack, remainingFE);
                    if (remainingFE <= 0) break;
                }
            }
        }

        // 4. Charge helmet itself with any leftover
        if (remainingFE > 0) {
            receiveEnergy(stack, remainingFE, capacity, maxTransfer, false);
        }
    }

    private int chargeStack(ItemStack target, int maxAmountFE) {
        if (target.isEmpty() || maxAmountFE <= 0) return maxAmountFE;
        IEnergyStorage storage = target.getCapability(Capabilities.EnergyStorage.ITEM, null);
        if (storage != null && storage.canReceive()) {
            int accepted = storage.receiveEnergy(maxAmountFE, false);
            return maxAmountFE - accepted;
        }
        return maxAmountFE;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("advanced_solar_panels.tooltip.day_gen", dayPower).withStyle(ChatFormatting.YELLOW));
        tooltip.add(Component.translatable("advanced_solar_panels.tooltip.night_gen", nightPower).withStyle(ChatFormatting.BLUE));
    }
}
