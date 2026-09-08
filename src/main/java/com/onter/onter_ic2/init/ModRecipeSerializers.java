package com.onter.onter_ic2.init;

import com.onter.onter_ic2.OnterIC2;
import com.onter.onter_ic2.recipe.BaseMachineRecipe;
import com.onter.onter_ic2.recipe.CompressorRecipe;
import com.onter.onter_ic2.recipe.ExtractorRecipe;
import com.onter.onter_ic2.recipe.MaceratorRecipe;
import com.onter.onter_ic2.recipe.MetalFormerRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, OnterIC2.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MaceratorRecipe>> MACERATOR_SERIALIZER =
            RECIPE_SERIALIZERS.register("macerating", () -> new BaseMachineRecipe.Serializer<>(MaceratorRecipe::new));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CompressorRecipe>> COMPRESSOR_SERIALIZER =
            RECIPE_SERIALIZERS.register("compressing", () -> new BaseMachineRecipe.Serializer<>(CompressorRecipe::new));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ExtractorRecipe>> EXTRACTOR_SERIALIZER =
            RECIPE_SERIALIZERS.register("extracting", () -> new BaseMachineRecipe.Serializer<>(ExtractorRecipe::new));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MetalFormerRecipe>> METAL_FORMER_SERIALIZER =
            RECIPE_SERIALIZERS.register("metal_forming", MetalFormerRecipe.Serializer::new);
}
