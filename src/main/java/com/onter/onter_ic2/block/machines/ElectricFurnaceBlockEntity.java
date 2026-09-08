package com.onter.onter_ic2.block.machines;

import com.onter.onter_ic2.block.base.BaseMachineBlockEntity;
import com.onter.onter_ic2.init.ModBlockEntities;
import com.onter.onter_ic2.menu.BaseMachineMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ElectricFurnaceBlockEntity extends BaseMachineBlockEntity {
    public ElectricFurnaceBlockEntity(BlockPos pos, BlockState blockState) {
        this(ModBlockEntities.ELECTRIC_FURNACE.get(), pos, blockState, 40000, 12, 130, 1); // 12 FE/t (3 EU/t), 130 ticks
    }

    public ElectricFurnaceBlockEntity(net.minecraft.world.level.block.entity.BlockEntityType<?> type, BlockPos pos, BlockState blockState,
                                      int capacity, int energyPerTick, int maxProgress, int batchMultiplier) {
        super(type, pos, blockState, capacity, energyPerTick, maxProgress, batchMultiplier);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(getBlockState().getBlock().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new BaseMachineMenu(containerId, playerInventory, this, this.dataAccess);
    }

    @Override
    public boolean isValidInput(ItemStack stack) {
        if (stack.isEmpty() || level == null) return false;
        return level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(stack), level).isPresent();
    }

    private Optional<RecipeHolder<SmeltingRecipe>> getCurrentRecipe() {
        if (level == null) return Optional.empty();
        ItemStack input = itemHandler.getStackInSlot(SLOT_INPUT);
        if (input.isEmpty()) return Optional.empty();
        return level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(input), level);
    }

    @Override
    protected boolean canProcess() {
        if (!surplusOutputBuffer.isEmpty()) return false;
        Optional<RecipeHolder<SmeltingRecipe>> recipe = getCurrentRecipe();
        if (recipe.isEmpty()) return false;

        ItemStack result = recipe.get().value().getResultItem(level.registryAccess());
        ItemStack outputSlot = itemHandler.getStackInSlot(SLOT_OUTPUT);

        if (outputSlot.isEmpty()) return true;
        return ItemStack.isSameItemSameComponents(outputSlot, result) && outputSlot.getCount() < outputSlot.getMaxStackSize();
    }

    @Override
    protected void processItem() {
        Optional<RecipeHolder<SmeltingRecipe>> recipe = getCurrentRecipe();
        if (recipe.isPresent() && canProcess()) {
            ItemStack input = itemHandler.getStackInSlot(SLOT_INPUT);
            int toExtract = Math.min(batchMultiplier, input.getCount());
            ItemStack result = recipe.get().value().assemble(new SingleRecipeInput(input), level.registryAccess());
            itemHandler.extractItem(SLOT_INPUT, toExtract, false);
            produceOutput(result, toExtract);
        }
    }
}
