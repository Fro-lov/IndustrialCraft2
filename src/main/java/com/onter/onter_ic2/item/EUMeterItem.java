package com.onter.onter_ic2.item;

import com.onter.onter_ic2.init.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EUMeterItem extends Item {
    public EUMeterItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();

        if (player == null) return InteractionResult.PASS;

        BlockEntity be = world.getBlockEntity(pos);
        if (be != null) {
            IEnergyStorage energyStorage = world.getCapability(Capabilities.EnergyStorage.BLOCK, pos, context.getClickedFace());
            if (energyStorage != null) {
                if (!world.isClientSide()) {
                    int stored = energyStorage.getEnergyStored();
                    int max = energyStorage.getMaxEnergyStored();
                    int euStored = stored / 4;
                    int euMax = max / 4;

                    player.displayClientMessage(Component.literal("=== EU-Meter Report ===").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD), false);
                    player.displayClientMessage(Component.literal(String.format("Storage: %,d / %,d EU (%,d / %,d FE)", euStored, euMax, stored, max)).withStyle(ChatFormatting.YELLOW), false);
                    player.displayClientMessage(Component.literal(String.format("Fill Level: %.1f%%", max > 0 ? (100.0 * stored / max) : 0.0)).withStyle(ChatFormatting.AQUA), false);
                    player.displayClientMessage(Component.literal(String.format("Input Supported: %s | Output Supported: %s", energyStorage.canReceive() ? "Yes" : "No", energyStorage.canExtract() ? "Yes" : "No")).withStyle(ChatFormatting.GRAY), false);
                }
                world.playSound(null, pos, ModSounds.WRENCH.get(), SoundSource.PLAYERS, 0.8F, 1.2F);
                return InteractionResult.sidedSuccess(world.isClientSide());
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level world, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        world.playSound(null, player.blockPosition(), ModSounds.WRENCH.get(), SoundSource.PLAYERS, 0.8F, 1.4F);
        return InteractionResultHolder.sidedSuccess(stack, world.isClientSide());
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.onter_ic2.eu_meter").withStyle(ChatFormatting.AQUA));
    }
}
