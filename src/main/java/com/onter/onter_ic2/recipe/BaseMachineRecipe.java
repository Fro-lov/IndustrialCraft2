package com.onter.onter_ic2.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

public abstract class BaseMachineRecipe implements Recipe<SingleRecipeInput> {
    protected final Ingredient ingredient;
    protected final ItemStack result;
    protected final int energyCost;
    protected final int processTime;

    public BaseMachineRecipe(Ingredient ingredient, ItemStack result, int energyCost, int processTime) {
        this.ingredient = ingredient;
        this.result = result;
        this.energyCost = energyCost;
        this.processTime = processTime;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public int getEnergyCost() {
        return energyCost;
    }

    public int getProcessTime() {
        return processTime;
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return this.ingredient.test(input.item());
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return this.result;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(ingredient);
        return list;
    }

    public static class Serializer<T extends BaseMachineRecipe> implements RecipeSerializer<T> {
        private final RecipeFactory<T> factory;
        private final MapCodec<T> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

        public interface RecipeFactory<T extends BaseMachineRecipe> {
            T create(Ingredient ingredient, ItemStack result, int energyCost, int processTime);
        }

        public Serializer(RecipeFactory<T> factory) {
            this.factory = factory;
            this.codec = RecordCodecBuilder.mapCodec(inst -> inst.group(
                    Ingredient.CODEC.fieldOf("ingredient").forGetter(BaseMachineRecipe::getIngredient),
                    ItemStack.CODEC.fieldOf("result").forGetter(r -> r.result),
                    Codec.INT.optionalFieldOf("energyCost", 800).forGetter(BaseMachineRecipe::getEnergyCost),
                    Codec.INT.optionalFieldOf("processTime", 200).forGetter(BaseMachineRecipe::getProcessTime)
            ).apply(inst, factory::create));

            this.streamCodec = StreamCodec.of(
                    (buf, recipe) -> {
                        Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.ingredient);
                        ItemStack.STREAM_CODEC.encode(buf, recipe.result);
                        buf.writeInt(recipe.energyCost);
                        buf.writeInt(recipe.processTime);
                    },
                    buf -> {
                        Ingredient ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
                        ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
                        int energy = buf.readInt();
                        int time = buf.readInt();
                        return factory.create(ingredient, result, energy, time);
                    }
            );
        }

        @Override
        public MapCodec<T> codec() {
            return codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
            return streamCodec;
        }
    }
}
