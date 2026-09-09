package com.onter.onter_ic2.compat.jei;

import com.onter.onter_ic2.OnterIC2;
import com.onter.onter_ic2.block.machines.MolecularTransformerBlockEntity;
import com.onter.onter_ic2.init.ModBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MolecularTransformerCategory implements IRecipeCategory<MolecularTransformerBlockEntity.MTRecipe> {
    public static final RecipeType<MolecularTransformerBlockEntity.MTRecipe> TYPE =
            RecipeType.create(OnterIC2.MODID, "molecular_transformer", MolecularTransformerBlockEntity.MTRecipe.class);

    private static final int WIDTH = 150;
    private static final int HEIGHT = 44;

    private final IDrawable icon;
    private final IDrawableAnimated arrow;

    public MolecularTransformerCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModBlocks.MOLECULAR_TRANSFORMER.get()));
        this.arrow = guiHelper.createAnimatedRecipeArrow(100);
    }

    @Override
    public @NotNull RecipeType<MolecularTransformerBlockEntity.MTRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("block.onter_ic2.molecular_transformer");
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull MolecularTransformerBlockEntity.MTRecipe recipe, @NotNull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 12, 8)
                .addItemStack(new ItemStack(recipe.input(), recipe.inputCount()))
                .setStandardSlotBackground();
        builder.addSlot(RecipeIngredientRole.OUTPUT, 122, 8)
                .addItemStack(recipe.output())
                .setOutputSlotBackground();
    }

    @Override
    public void draw(@NotNull MolecularTransformerBlockEntity.MTRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        arrow.draw(guiGraphics, 63, 8);
        String energyStr = String.format("%,d EU", recipe.totalEU());
        Component energy = Component.translatable("advanced_solar_panels.gui.energyPerOperation").append(" ").append(energyStr);
        var font = Minecraft.getInstance().font;
        guiGraphics.drawString(font, energy, (WIDTH - font.width(energy)) / 2, 32, 0x404040, false);
    }
}
