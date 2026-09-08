package com.onter.onter_ic2.block.machines;

import com.onter.onter_ic2.block.base.BaseMachineBlockEntity;
import com.onter.onter_ic2.init.ModBlockEntities;
import com.onter.onter_ic2.init.ModRecipeTypes;
import com.onter.onter_ic2.menu.BaseMachineMenu;
import com.onter.onter_ic2.recipe.ExtractorRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ExtractorBlockEntity extends BaseMachineBlockEntity {
    public ExtractorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.EXTRACTOR.get(), pos, blockState, 40000, 8, 300); // 8 FE/t (2 EU/t), 300 ticks
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.onter_ic2.extractor");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new BaseMachineMenu(containerId, playerInventory, this, this.dataAccess);
    }

    private Optional<RecipeHolder<ExtractorRecipe>> getCurrentRecipe() {
        if (level == null) return Optional.empty();
        ItemStack input = itemHandler.getStackInSlot(SLOT_INPUT);
        if (input.isEmpty()) return Optional.empty();
        return level.getRecipeManager().getRecipeFor(ModRecipeTypes.EXTRACTOR_RECIPE_TYPE.get(), new SingleRecipeInput(input), level);
    }

    @Override
    protected boolean canProcess() {
        Optional<RecipeHolder<ExtractorRecipe>> recipe = getCurrentRecipe();
        if (recipe.isEmpty()) return false;

        ItemStack result = recipe.get().value().getResultItem(level.registryAccess());
        ItemStack outputSlot = itemHandler.getStackInSlot(SLOT_OUTPUT);

        if (outputSlot.isEmpty()) return true;
        if (!ItemStack.isSameItemSameComponents(outputSlot, result)) return false;
        return outputSlot.getCount() + result.getCount() <= outputSlot.getMaxStackSize();
    }

    @Override
    protected void processItem() {
        Optional<RecipeHolder<ExtractorRecipe>> recipe = getCurrentRecipe();
        if (recipe.isPresent() && canProcess()) {
            ItemStack result = recipe.get().value().assemble(new SingleRecipeInput(itemHandler.getStackInSlot(SLOT_INPUT)), level.registryAccess());
            itemHandler.extractItem(SLOT_INPUT, 1, false);

            ItemStack outputSlot = itemHandler.getStackInSlot(SLOT_OUTPUT);
            if (outputSlot.isEmpty()) {
                itemHandler.setStackInSlot(SLOT_OUTPUT, result.copy());
            } else {
                outputSlot.grow(result.getCount());
            }
        }
    }
}
