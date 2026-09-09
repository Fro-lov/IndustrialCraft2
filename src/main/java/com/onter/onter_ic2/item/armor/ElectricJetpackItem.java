package com.onter.onter_ic2.item.armor;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ElectricJetpackItem extends ElectricArmorItem {
    public static final int JETPACK_CAPACITY = 120_000; // 30,000 EU
    public static final int JETPACK_TRANSFER = 240;     // 60 EU/t
    public static final int JETPACK_TIER = 1;

    public ElectricJetpackItem(Properties properties) {
        super(Ic2ArmorMaterials.JET_PACK, Type.CHESTPLATE, properties, JETPACK_CAPACITY, JETPACK_TRANSFER, JETPACK_TIER);
    }

    @Override
    public int getEnergyPerDamage() {
        return 0;
    }

    @Override
    public double getDamageAbsorptionRatio() {
        return 0.0;
    }

    public static boolean isHoverMode(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            return customData.copyTag().getBoolean("hover_mode");
        }
        return false;
    }

    public static void setHoverMode(ItemStack stack, boolean hover) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.putBoolean("hover_mode", hover);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level world, @NotNull Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if (!(entity instanceof Player player)) return;
        if (player.getItemBySlot(EquipmentSlot.CHEST) != stack) return;

        int currentEnergy = getEnergy(stack);
        if (currentEnergy <= 0) return;

        boolean hover = isHoverMode(stack);

        // Jetpack flight logic
        if (player.isFallFlying()) return;

        boolean isJumping = false;
        if (world.isClientSide()) {
            isJumping = net.minecraft.client.Minecraft.getInstance().options.keyJump.isDown();
        } else {
            isJumping = player.getDeltaMovement().y > 0.0;
        }

        if (isJumping || (hover && !player.onGround())) {
            double motionY = player.getDeltaMovement().y;
            double targetY = hover ? (player.isShiftKeyDown() ? -0.15 : (isJumping ? 0.25 : 0.0)) : 0.35;
            
            Vec3 motion = player.getDeltaMovement();
            player.setDeltaMovement(motion.x, Math.min(motionY + 0.15, targetY), motion.z);
            player.resetFallDistance();

            if (!world.isClientSide()) {
                extractEnergy(stack, hover ? 24 : 36, maxTransfer, false);
            }
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.onter_ic2.jetpack_desc").withStyle(ChatFormatting.AQUA));
        boolean hover = isHoverMode(stack);
        tooltip.add(Component.translatable("tooltip.onter_ic2.jetpack_hover", hover ? Component.translatable("tooltip.onter_ic2.enabled").withStyle(ChatFormatting.GREEN) : Component.translatable("tooltip.onter_ic2.disabled").withStyle(ChatFormatting.RED)));
    }
}
