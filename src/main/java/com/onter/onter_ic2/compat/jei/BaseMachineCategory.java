package com.onter.onter_ic2.compat.jei;

import com.onter.onter_ic2.OnterIC2;
import com.onter.onter_ic2.recipe.BaseMachineRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class BaseMachineCategory<T extends BaseMachineRecipe> implements IRecipeCategory<T> {
    private final RecipeType<T> recipeType;
    private final Component title;
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableAnimated arrow;
    private final IDrawableAnimated energy;

    public BaseMachineCategory(IGuiHelper helper, RecipeType<T> recipeType, Block iconBlock, String titleKey, ResourceLocation guiTexture) {
        this.recipeType = recipeType;
        this.title = Component.translatable(titleKey);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(iconBlock));
        this.background = helper.createDrawable(guiTexture, 50, 15, 95, 56);

        this.arrow = helper.drawableBuilder(guiTexture, 176, 14, 24, 17)
                .buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, false);

        this.energy = helper.drawableBuilder(guiTexture, 176, 0, 14, 14)
                .buildAnimated(40, IDrawableAnimated.StartDirection.TOP, true);
    }

    @Override
    public RecipeType<T> getRecipeType() {
        return recipeType;
    }

    @Override
    public Component getTitle() {
        return title;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void draw(T recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        arrow.draw(guiGraphics, 79 - 50, 34 - 15);
        energy.draw(guiGraphics, 56 - 50, 37 - 15);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, T recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 56 - 50 + 1, 17 - 15 + 1)
                .addIngredients(recipe.getIngredient());

        builder.addSlot(RecipeIngredientRole.OUTPUT, 116 - 50 + 1, 35 - 15 + 1)
                .addItemStack(recipe.getResultItem(null));
    }
}
