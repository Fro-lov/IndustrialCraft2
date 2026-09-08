package com.onter.onter_ic2.init;

import com.onter.onter_ic2.OnterIC2;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, OnterIC2.MODID);

    public static final Supplier<SoundEvent> MACERATOR_OP = registerSoundEvent("machine.macerator", "macerator_op");
    public static final Supplier<SoundEvent> ELECTRO_FURNACE_LOOP = registerSoundEvent("machine.electric_furnace", "electro_furnace_loop");
    public static final Supplier<SoundEvent> COMPRESSOR_OP = registerSoundEvent("machine.compressor", "compressor_op");
    public static final Supplier<SoundEvent> EXTRACTOR_OP = registerSoundEvent("machine.extractor", "extractor_op");
    public static final Supplier<SoundEvent> GENERATOR_OP = registerSoundEvent("machine.generator", "generator_op");

    private static Supplier<SoundEvent> registerSoundEvent(String name, String soundLocation) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(OnterIC2.MODID, soundLocation)));
    }
}
