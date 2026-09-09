package com.onter.onter_ic2.item;

import com.onter.onter_ic2.block.base.BaseMachineBlockEntity;
import com.onter.onter_ic2.block.cables.CableBlockEntity;
import com.onter.onter_ic2.block.generators.GeneratorBlockEntity;
import com.onter.onter_ic2.block.generators.QuantumGeneratorBlockEntity;
import com.onter.onter_ic2.block.generators.SolarPanelBlockEntity;
import com.onter.onter_ic2.block.machines.MolecularTransformerBlockEntity;
import com.onter.onter_ic2.block.machines.MultiSlotMachineBlockEntity;
import com.onter.onter_ic2.block.storage.EnergyStorageBlockEntity;
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
            if (player.isShiftKeyDown()) {
                // Shift + ПКМ: Открыть графический интерфейс ваттметра
                if (!world.isClientSide() && player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.openMenu(new SimpleMenuProvider(
                            (containerId, playerInventory, p) -> new MeterMenu(containerId, playerInventory, pos, face, MeterMenu.createServerContainerData()),
                            Component.translatable("item.onter_ic2.eu_meter")
                    ), buf -> {
                        buf.writeBlockPos(pos);
                        buf.writeEnum(face);
                    });
                }
            } else {
                // Обычный ПКМ: Быстрая сводка в чат
                if (!world.isClientSide()) {
                    sendQuickChatReport(player, world, pos, face, be, energyStorage);
                }
            }
            world.playSound(null, pos, ModSounds.WRENCH.get(), SoundSource.PLAYERS, 0.8F, 1.2F);
            return InteractionResult.sidedSuccess(world.isClientSide());
        }

        return InteractionResult.PASS;
    }

    private void sendQuickChatReport(Player player, Level world, BlockPos pos, Direction face, BlockEntity be, IEnergyStorage storage) {
        String blockName = world.getBlockState(pos).getBlock().getName().getString();
        int stored = storage != null ? storage.getEnergyStored() / 4 : 0;
        int max = storage != null ? storage.getMaxEnergyStored() / 4 : 0;

        int limitEU = 32;
        int tier = 1;

        if (be instanceof CableBlockEntity cable) {
            limitEU = cable.getMaxTransfer() / 4;
            tier = limitEU <= 32 ? 1 : (limitEU <= 128 ? 2 : (limitEU <= 512 ? 3 : (limitEU <= 2048 ? 4 : 5)));
            player.displayClientMessage(Component.literal(String.format("§6[Ваттметр] §f%s §7| §eПропускная способность: §a%d EU/t §7(Tier %d)", blockName, limitEU, tier)), false);
            return;
        }

        if (be instanceof EnergyStorageBlockEntity esbe) {
            int transferEU = esbe.getMaxTransfer() / 4;
            tier = transferEU <= 32 ? 1 : (transferEU <= 128 ? 2 : (transferEU <= 512 ? 3 : 4));
            player.displayClientMessage(Component.literal(String.format("§6[Ваттметр] §f%s §7| §eЗаряд: §f%,d / %,d EU §7| §bВыход: §a%d EU/t §7(Tier %d)", blockName, stored, max, transferEU, tier)), false);
            return;
        }

        if (be instanceof SolarPanelBlockEntity solar) {
            int dayGen = solar.getDayGen() / 4;
            int nightGen = solar.getNightGen() / 4;
            player.displayClientMessage(Component.literal(String.format("§6[Ваттметр] §f%s §7| §eЗаряд: §f%,d / %,d EU §7| §aГенерация: %d EU/t (День) / %d EU/t (Ночь)", blockName, stored, max, dayGen, nightGen)), false);
            return;
        }

        if (be instanceof QuantumGeneratorBlockEntity qg) {
            player.displayClientMessage(Component.literal(String.format("§6[Ваттметр] §f%s §7| §eВыход: §a%,d EU/t §7| §bTier: %d §7| §fСтатус: %s", blockName, qg.getProduction(), qg.getTier(), qg.isActive() ? "§aВкл" : "§cВыкл")), false);
            return;
        }

        if (be instanceof MolecularTransformerBlockEntity mt) {
            player.displayClientMessage(Component.literal(String.format("§6[Ваттметр] §f%s §7| §eПоток энергии: §a%,d EU/t §7| §bМакс. вход: §a8,192 EU/t §7(Tier 5)", blockName, mt.getLastEnergyGiven())), false);
            return;
        }

        if (be instanceof MultiSlotMachineBlockEntity multiMachine) {
            limitEU = multiMachine.getNumChannels() > 6 ? 512 : 128;
            tier = multiMachine.getNumChannels() > 6 ? 3 : 2;
            int powerEU = multiMachine.getBaseEnergyPerTick() / 4;
            player.displayClientMessage(Component.literal(String.format("§6[Ваттметр] §f%s §7| §eЭнергия: §f%,d / %,d EU §7| §bРасход: §c%d EU/t §7| §aМакс. вход: %d EU/t (Tier %d)", blockName, stored, max, powerEU, limitEU, tier)), false);
            return;
        }

        if (be instanceof BaseMachineBlockEntity machine) {
            int powerEU = machine.getBaseEnergyPerTick() / 4;
            player.displayClientMessage(Component.literal(String.format("§6[Ваттметр] §f%s §7| §eЭнергия: §f%,d / %,d EU §7| §bРасход: §c%d EU/t §7| §aМакс. вход: 32 EU/t (Tier 1)", blockName, stored, max, powerEU)), false);
            return;
        }

        if (be instanceof GeneratorBlockEntity) {
            player.displayClientMessage(Component.literal(String.format("§6[Ваттметр] §f%s §7| §eЭнергия: §f%,d / %,d EU §7| §aВыход: 10 EU/t (Tier 1)", blockName, stored, max)), false);
            return;
        }

        // Generic energy block fallback
        player.displayClientMessage(Component.literal(String.format("§6[Ваттметр] §f%s §7| §eЭнергия: §f%,d / %,d EU", blockName, stored, max)), false);
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
        tooltip.add(Component.literal("ПКМ по блоку: быстрая сводка в чат").withStyle(ChatFormatting.GREEN));
        tooltip.add(Component.literal("Shift + ПКМ по блоку: открыть интерфейс ваттметра").withStyle(ChatFormatting.AQUA));
    }
}
