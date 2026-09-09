package com.onter.onter_ic2.item.armor;

import com.onter.onter_ic2.OnterIC2;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

public final class Ic2ArmorMaterials {
    private static EnumMap<ArmorItem.Type, Integer> createDefense(int boots, int leggings, int chestplate, int helmet) {
        EnumMap<ArmorItem.Type, Integer> map = new EnumMap<>(ArmorItem.Type.class);
        map.put(ArmorItem.Type.BOOTS, boots);
        map.put(ArmorItem.Type.LEGGINGS, leggings);
        map.put(ArmorItem.Type.CHESTPLATE, chestplate);
        map.put(ArmorItem.Type.HELMET, helmet);
        map.put(ArmorItem.Type.BODY, 0);
        return map;
    }

    private static Holder<ArmorMaterial> create(String name, EnumMap<ArmorItem.Type, Integer> defense, int enchantValue, Holder<SoundEvent> equipSound, float toughness, float knockbackResistance, Supplier<Ingredient> repairIngredient) {
        return Holder.direct(new ArmorMaterial(
                defense,
                enchantValue,
                equipSound,
                repairIngredient,
                List.of(new ArmorMaterial.Layer(OnterIC2.loc(name))),
                toughness,
                knockbackResistance
        ));
    }

    public static final Holder<ArmorMaterial> NANO_SUIT = create("nano_suit", createDefense(3, 6, 8, 3), 0, SoundEvents.ARMOR_EQUIP_DIAMOND, 3.0F, 0.1F, Ingredient::of);
    public static final Holder<ArmorMaterial> QUANTUM_SUIT = create("quantum_suit", createDefense(4, 8, 10, 4), 0, SoundEvents.ARMOR_EQUIP_NETHERITE, 4.0F, 0.2F, Ingredient::of);
    public static final Holder<ArmorMaterial> JET_PACK = create("jetpack_electric", createDefense(0, 0, 5, 0), 0, SoundEvents.ARMOR_EQUIP_IRON, 1.0F, 0.0F, Ingredient::of);
    public static final Holder<ArmorMaterial> BAT_PACK = create("batpack", createDefense(0, 0, 3, 0), 0, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, 0.0F, Ingredient::of);
}
