package com.onter.onter_ic2.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.onter.onter_ic2.init.ModRecipeSerializers;
import com.onter.onter_ic2.init.ModRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

public class MetalFormerRecipe implements Recipe<SingleRecipeInput> {
    public enum Mode implements StringRepresentable {
        ROLLING("rolling"),
        EXTRUDING("extruding"),
        CUTTING("cutting");

        public static final Codec<Mode> CODEC = StringRepresentable.fromEnum(Mode::values);
        private final String name;

        Mode(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

    private final Mode mode;
    private final Ingredient ingredient;
    private final ItemStack result;
    private final int energyCost;
    private final int processTime;

    public MetalFormerRecipe(Mode mode, Ingredient ingredient, ItemStack result, int energyCost, int processTime) {
        this.mode = mode;
        this.ingredient = ingredient;
        this.result = result;
        this.energyCost = energyCost;
        this.processTime = processTime;
    }

    public Mode getMode() {
        return mode;
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

    public boolean matches(SingleRecipeInput input, Mode currentMode) {
        return this.mode == currentMode && this.ingredient.test(input.item());
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

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.METAL_FORMER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.METAL_FORMER_RECIPE_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<MetalFormerRecipe> {
        private final MapCodec<MetalFormerRecipe> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, MetalFormerRecipe> streamCodec;

        public Serializer() {
            this.codec = RecordCodecBuilder.mapCodec(inst -> inst.group(
                    Mode.CODEC.optionalFieldOf("mode", Mode.ROLLING).forGetter(MetalFormerRecipe::getMode),
                    Ingredient.CODEC.fieldOf("ingredient").forGetter(MetalFormerRecipe::getIngredient),
                    ItemStack.CODEC.fieldOf("result").forGetter(r -> r.result),
                    Codec.INT.optionalFieldOf("energyCost", 800).forGetter(MetalFormerRecipe::getEnergyCost),
                    Codec.INT.optionalFieldOf("processTime", 200).forGetter(MetalFormerRecipe::getProcessTime)
            ).apply(inst, MetalFormerRecipe::new));

            this.streamCodec = StreamCodec.of(
                    (buf, recipe) -> {
                        buf.writeEnum(recipe.mode);
                        Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.ingredient);
                        ItemStack.STREAM_CODEC.encode(buf, recipe.result);
                        buf.writeInt(recipe.energyCost);
                        buf.writeInt(recipe.processTime);
                    },
                    buf -> {
                        Mode mode = buf.readEnum(Mode.class);
                        Ingredient ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
                        ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
                        int energy = buf.readInt();
                        int time = buf.readInt();
                        return new MetalFormerRecipe(mode, ingredient, result, energy, time);
                    }
            );
        }

        @Override
        public MapCodec<MetalFormerRecipe> codec() {
            return codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, MetalFormerRecipe> streamCodec() {
            return streamCodec;
        }
    }
}
