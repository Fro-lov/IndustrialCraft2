package com.onter.onter_ic2.item.armor;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BatpackItem extends ElectricArmorItem {
    public BatpackItem(Properties properties, int capacity, int maxTransfer, int tier) {
        super(Ic2ArmorMaterials.BAT_PACK, Type.CHESTPLATE, properties, capacity, maxTransfer, tier);
    }

    @Override
    public int getEnergyPerDamage() {
        return 0;
    }

    @Override
    public double getDamageAbsorptionRatio() {
        return 0.0;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level world, @NotNull Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if (!world.isClientSide() && entity instanceof Player player) {
            if (player.getItemBySlot(EquipmentSlot.CHEST) == stack) {
                int stored = getEnergy(stack);
                if (stored <= 0) return;

                // Charge main hand and off hand items
                chargeItem(player.getMainHandItem(), stack);
                chargeItem(player.getOffhandItem(), stack);
            }
        }
    }

    private void chargeItem(ItemStack targetStack, ItemStack batpackStack) {
        if (targetStack.isEmpty() || targetStack == batpackStack) return;
        IEnergyStorage storage = targetStack.getCapability(Capabilities.EnergyStorage.ITEM, null);
        if (storage != null && storage.canReceive()) {
            int stored = getEnergy(batpackStack);
            int toTransfer = Math.min(stored, maxTransfer);
            int received = storage.receiveEnergy(toTransfer, false);
            if (received > 0) {
                extractEnergy(batpackStack, received, maxTransfer, false);
            }
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.onter_ic2.batpack_desc").withStyle(ChatFormatting.GRAY));
    }
}
