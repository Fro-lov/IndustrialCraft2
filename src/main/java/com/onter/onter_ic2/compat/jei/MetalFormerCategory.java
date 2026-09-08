package com.onter.onter_ic2.compat.jei;

import com.onter.onter_ic2.OnterIC2;
import com.onter.onter_ic2.init.ModBlocks;
import com.onter.onter_ic2.recipe.MetalFormerRecipe;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class MetalFormerCategory implements IRecipeCategory<MetalFormerRecipe> {
    public static final RecipeType<MetalFormerRecipe> TYPE = RecipeType.create(OnterIC2.MODID, "metal_forming", MetalFormerRecipe.class);

    private final Component title;
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable slotDrawable;
    private final IDrawable arrowStatic;
    private final IDrawableAnimated arrowAnimated;

    public MetalFormerCategory(IGuiHelper helper) {
        ResourceLocation guiTexture = OnterIC2.loc("textures/gui/container/metal_former.png");
        this.title = Component.translatable("block.onter_ic2.metal_former");
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.METAL_FORMER.get()));
        this.background = helper.createBlankDrawable(140, 50);
        this.slotDrawable = helper.getSlotDrawable();

        this.arrowStatic = helper.createDrawable(guiTexture, 176, 14, 24, 17);
        this.arrowAnimated = helper.drawableBuilder(guiTexture, 176, 14, 24, 17)
                .buildAnimated(150, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public RecipeType<MetalFormerRecipe> getRecipeType() {
        return TYPE;
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
    public void draw(MetalFormerRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        // Draw slots background
        slotDrawable.draw(guiGraphics, 12, 14);
        slotDrawable.draw(guiGraphics, 104, 14);

        // Draw animated progress arrow
        arrowAnimated.draw(guiGraphics, 54, 16);

        // Draw mode badge text centered
        MetalFormerRecipe.Mode mode = recipe.getMode();
        String modeStr = switch (mode) {
            case ROLLING -> "§6[ПРОКАТ]";
            case EXTRUDING -> "§e[ВЫДАВЛИВАНИЕ]";
            case CUTTING -> "§c[РЕЗКА]";
        };
        int textX = 70 - Minecraft.getInstance().font.width(modeStr) / 2;
        guiGraphics.drawString(Minecraft.getInstance().font, modeStr, textX, 4, 0x404040, false);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MetalFormerRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 13, 15)
                .addIngredients(recipe.getIngredient());

        builder.addSlot(RecipeIngredientRole.OUTPUT, 105, 15)
                .addItemStack(recipe.getResultItem(null));
    }
}
