package com.onter.onter_ic2.item;

import com.onter.onter_ic2.init.ModSounds;
import com.onter.onter_ic2.menu.MeterMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
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
        Direction face = context.getClickedFace();

        if (player == null) return InteractionResult.PASS;

        BlockEntity be = world.getBlockEntity(pos);
        IEnergyStorage energyStorage = world.getCapability(Capabilities.EnergyStorage.BLOCK, pos, face);

        if (be != null || energyStorage != null) {
            if (!world.isClientSide() && player instanceof ServerPlayer serverPlayer) {
                serverPlayer.openMenu(new SimpleMenuProvider(
                        (containerId, playerInventory, p) -> new MeterMenu(containerId, playerInventory, pos, face, MeterMenu.createServerContainerData()),
                        Component.translatable("item.onter_ic2.eu_meter")
                ), buf -> {
                    buf.writeBlockPos(pos);
                    buf.writeEnum(face);
                });
            }
            world.playSound(null, pos, ModSounds.WRENCH.get(), SoundSource.PLAYERS, 0.8F, 1.2F);
            return InteractionResult.sidedSuccess(world.isClientSide());
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
