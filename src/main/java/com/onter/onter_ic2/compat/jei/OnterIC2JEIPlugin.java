package com.onter.onter_ic2.compat.jei;

import com.onter.onter_ic2.OnterIC2;
import com.onter.onter_ic2.client.screen.BaseMachineScreen;
import com.onter.onter_ic2.init.ModBlocks;
import com.onter.onter_ic2.init.ModRecipeTypes;
import com.onter.onter_ic2.recipe.CompressorRecipe;
import com.onter.onter_ic2.recipe.ExtractorRecipe;
import com.onter.onter_ic2.recipe.MaceratorRecipe;
import com.onter.onter_ic2.recipe.MetalFormerRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;

@JeiPlugin
public class OnterIC2JEIPlugin implements IModPlugin {
    public static final ResourceLocation PLUGIN_ID = OnterIC2.loc("jei_plugin");

    public static final RecipeType<MaceratorRecipe> MACERATOR_TYPE = RecipeType.create(OnterIC2.MODID, "macerating", MaceratorRecipe.class);
    public static final RecipeType<CompressorRecipe> COMPRESSOR_TYPE = RecipeType.create(OnterIC2.MODID, "compressing", CompressorRecipe.class);
    public static final RecipeType<ExtractorRecipe> EXTRACTOR_TYPE = RecipeType.create(OnterIC2.MODID, "extracting", ExtractorRecipe.class);
    public static final RecipeType<MetalFormerRecipe> METAL_FORMER_TYPE = MetalFormerCategory.TYPE;

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var helper = registration.getJeiHelpers().getGuiHelper();

        registration.addRecipeCategories(
                new BaseMachineCategory<>(helper, MACERATOR_TYPE, ModBlocks.MACERATOR.get(), "block.onter_ic2.macerator", OnterIC2.loc("textures/gui/container/macerator.png")),
                new BaseMachineCategory<>(helper, COMPRESSOR_TYPE, ModBlocks.COMPRESSOR.get(), "block.onter_ic2.compressor", OnterIC2.loc("textures/gui/container/compressor.png")),
                new BaseMachineCategory<>(helper, EXTRACTOR_TYPE, ModBlocks.EXTRACTOR.get(), "block.onter_ic2.extractor", OnterIC2.loc("textures/gui/container/extractor.png")),
                new MetalFormerCategory(helper)
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        if (Minecraft.getInstance().level == null) return;
        RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();

        List<MaceratorRecipe> maceratorRecipes = manager.getAllRecipesFor(ModRecipeTypes.MACERATOR_RECIPE_TYPE.get()).stream().map(RecipeHolder::value).toList();
        registration.addRecipes(MACERATOR_TYPE, maceratorRecipes);

        List<CompressorRecipe> compressorRecipes = manager.getAllRecipesFor(ModRecipeTypes.COMPRESSOR_RECIPE_TYPE.get()).stream().map(RecipeHolder::value).toList();
        registration.addRecipes(COMPRESSOR_TYPE, compressorRecipes);

        List<ExtractorRecipe> extractorRecipes = manager.getAllRecipesFor(ModRecipeTypes.EXTRACTOR_RECIPE_TYPE.get()).stream().map(RecipeHolder::value).toList();
        registration.addRecipes(EXTRACTOR_TYPE, extractorRecipes);

        List<MetalFormerRecipe> metalFormerRecipes = manager.getAllRecipesFor(ModRecipeTypes.METAL_FORMER_RECIPE_TYPE.get()).stream().map(RecipeHolder::value).toList();
        registration.addRecipes(METAL_FORMER_TYPE, metalFormerRecipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.MACERATOR.get()), MACERATOR_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.COMPRESSOR.get()), COMPRESSOR_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.EXTRACTOR.get()), EXTRACTOR_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.METAL_FORMER.get()), METAL_FORMER_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.ELECTRIC_FURNACE.get()), RecipeTypes.SMELTING);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.GENERATOR.get()), RecipeTypes.FUELING);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(BaseMachineScreen.class, 79, 34, 24, 17, MACERATOR_TYPE, COMPRESSOR_TYPE, EXTRACTOR_TYPE, RecipeTypes.SMELTING);
        registration.addRecipeClickArea(com.onter.onter_ic2.client.screen.MetalFormerScreen.class, 54, 39, 51, 13, METAL_FORMER_TYPE);
    }
}
