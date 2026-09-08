package com.onter.onter_ic2.init;

import com.onter.onter_ic2.OnterIC2;
import com.onter.onter_ic2.recipe.CompressorRecipe;
import com.onter.onter_ic2.recipe.ExtractorRecipe;
import com.onter.onter_ic2.recipe.MaceratorRecipe;
import com.onter.onter_ic2.recipe.MetalFormerRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, OnterIC2.MODID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<MaceratorRecipe>> MACERATOR_RECIPE_TYPE =
            RECIPE_TYPES.register("macerating", () -> RecipeType.simple(OnterIC2.loc("macerating")));

    public static final DeferredHolder<RecipeType<?>, RecipeType<CompressorRecipe>> COMPRESSOR_RECIPE_TYPE =
            RECIPE_TYPES.register("compressing", () -> RecipeType.simple(OnterIC2.loc("compressing")));

    public static final DeferredHolder<RecipeType<?>, RecipeType<ExtractorRecipe>> EXTRACTOR_RECIPE_TYPE =
            RECIPE_TYPES.register("extracting", () -> RecipeType.simple(OnterIC2.loc("extracting")));

    public static final DeferredHolder<RecipeType<?>, RecipeType<MetalFormerRecipe>> METAL_FORMER_RECIPE_TYPE =
            RECIPE_TYPES.register("metal_forming", () -> RecipeType.simple(OnterIC2.loc("metal_forming")));
}
