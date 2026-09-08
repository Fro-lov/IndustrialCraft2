package com.onter.onter_ic2.recipe;

import com.onter.onter_ic2.init.ModRecipeSerializers;
import com.onter.onter_ic2.init.ModRecipeTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class CompressorRecipe extends BaseMachineRecipe {
    public CompressorRecipe(Ingredient ingredient, ItemStack result, int energyCost, int processTime) {
        super(ingredient, result, energyCost, processTime);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.COMPRESSOR_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.COMPRESSOR_RECIPE_TYPE.get();
    }
}
