package com.onter.onter_ic2.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class ScrapBoxItem extends Item {
    private static final Random RANDOM = new Random();

    public ScrapBoxItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            stack.shrink(1);
            ItemStack drop = getRandomDrop();
            if (!player.getInventory().add(drop)) {
                player.drop(drop, false);
            }
        }
        level.playSound(null, player.blockPosition(), SoundEvents.BUNDLE_DROP_CONTENTS, SoundSource.PLAYERS, 0.8F, 1.2F);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    private static ItemStack getRandomDrop() {
        int roll = RANDOM.nextInt(100);
        if (roll < 1) return new ItemStack(Items.DIAMOND, 1);
        if (roll < 3) return new ItemStack(Items.GOLD_INGOT, 1 + RANDOM.nextInt(2));
        if (roll < 8) return new ItemStack(Items.IRON_INGOT, 1 + RANDOM.nextInt(3));
        if (roll < 15) return new ItemStack(Items.REDSTONE, 1 + RANDOM.nextInt(4));
        if (roll < 22) return new ItemStack(Items.GLOWSTONE_DUST, 1 + RANDOM.nextInt(4));
        if (roll < 32) return new ItemStack(Items.COAL, 1 + RANDOM.nextInt(3));
        if (roll < 45) return new ItemStack(Items.COPPER_INGOT, 1 + RANDOM.nextInt(3));
        if (roll < 60) return new ItemStack(Items.STICK, 1 + RANDOM.nextInt(5));
        if (roll < 75) return new ItemStack(Items.DIRT, 1 + RANDOM.nextInt(8));
        return new ItemStack(Items.GRAVEL, 1 + RANDOM.nextInt(4));
    }
}
