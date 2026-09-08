package com.onter.onter_ic2.init;

import com.onter.onter_ic2.OnterIC2;
import com.onter.onter_ic2.block.cables.CableBlockEntity;
import com.onter.onter_ic2.block.generators.GeneratorBlockEntity;
import com.onter.onter_ic2.block.generators.SolarPanelBlockEntity;
import com.onter.onter_ic2.block.machines.*;
import com.onter.onter_ic2.block.storage.EnergyStorageBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, OnterIC2.MODID);

    // --- Base Machines ---
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MaceratorBlockEntity>> MACERATOR =
            BLOCK_ENTITIES.register("macerator", () -> BlockEntityType.Builder.of(MaceratorBlockEntity::new, ModBlocks.MACERATOR.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ElectricFurnaceBlockEntity>> ELECTRIC_FURNACE =
            BLOCK_ENTITIES.register("electric_furnace", () -> BlockEntityType.Builder.of(ElectricFurnaceBlockEntity::new, ModBlocks.ELECTRIC_FURNACE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CompressorBlockEntity>> COMPRESSOR =
            BLOCK_ENTITIES.register("compressor", () -> BlockEntityType.Builder.of(CompressorBlockEntity::new, ModBlocks.COMPRESSOR.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ExtractorBlockEntity>> EXTRACTOR =
            BLOCK_ENTITIES.register("extractor", () -> BlockEntityType.Builder.of(ExtractorBlockEntity::new, ModBlocks.EXTRACTOR.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MetalFormerBlockEntity>> METAL_FORMER =
            BLOCK_ENTITIES.register("metal_former", () -> BlockEntityType.Builder.of(MetalFormerBlockEntity::new, ModBlocks.METAL_FORMER.get()).build(null));

    // --- Advanced Machines (x6) ---
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MultiSlotMachineBlockEntity>> ADVANCED_MACERATOR =
            BLOCK_ENTITIES.register("advanced_macerator", () -> BlockEntityType.Builder.of((pos, state) ->
                    new MultiSlotMachineBlockEntity(ModBlockEntities.ADVANCED_MACERATOR.get(), pos, state, MultiSlotMachineBlockEntity.MachineType.MACERATOR, 6, 200000, 48, 50), ModBlocks.ADVANCED_MACERATOR.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MultiSlotMachineBlockEntity>> ADVANCED_ELECTRIC_FURNACE =
            BLOCK_ENTITIES.register("advanced_electric_furnace", () -> BlockEntityType.Builder.of((pos, state) ->
                    new MultiSlotMachineBlockEntity(ModBlockEntities.ADVANCED_ELECTRIC_FURNACE.get(), pos, state, MultiSlotMachineBlockEntity.MachineType.ELECTRIC_FURNACE, 6, 200000, 72, 25), ModBlocks.ADVANCED_ELECTRIC_FURNACE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MultiSlotMachineBlockEntity>> ADVANCED_COMPRESSOR =
            BLOCK_ENTITIES.register("advanced_compressor", () -> BlockEntityType.Builder.of((pos, state) ->
                    new MultiSlotMachineBlockEntity(ModBlockEntities.ADVANCED_COMPRESSOR.get(), pos, state, MultiSlotMachineBlockEntity.MachineType.COMPRESSOR, 6, 200000, 48, 65), ModBlocks.ADVANCED_COMPRESSOR.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MultiSlotMachineBlockEntity>> ADVANCED_EXTRACTOR =
            BLOCK_ENTITIES.register("advanced_extractor", () -> BlockEntityType.Builder.of((pos, state) ->
                    new MultiSlotMachineBlockEntity(ModBlockEntities.ADVANCED_EXTRACTOR.get(), pos, state, MultiSlotMachineBlockEntity.MachineType.EXTRACTOR, 6, 200000, 48, 50), ModBlocks.ADVANCED_EXTRACTOR.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MultiSlotMachineBlockEntity>> ADVANCED_METAL_FORMER =
            BLOCK_ENTITIES.register("advanced_metal_former", () -> BlockEntityType.Builder.of((pos, state) ->
                    new MultiSlotMachineBlockEntity(ModBlockEntities.ADVANCED_METAL_FORMER.get(), pos, state, MultiSlotMachineBlockEntity.MachineType.METAL_FORMER, 6, 200000, 60, 35), ModBlocks.ADVANCED_METAL_FORMER.get()).build(null));

    // --- Maximum Machines (x12) ---
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MultiSlotMachineBlockEntity>> MAX_MACERATOR =
            BLOCK_ENTITIES.register("max_macerator", () -> BlockEntityType.Builder.of((pos, state) ->
                    new MultiSlotMachineBlockEntity(ModBlockEntities.MAX_MACERATOR.get(), pos, state, MultiSlotMachineBlockEntity.MachineType.MACERATOR, 12, 1000000, 96, 25), ModBlocks.MAX_MACERATOR.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MultiSlotMachineBlockEntity>> MAX_ELECTRIC_FURNACE =
            BLOCK_ENTITIES.register("max_electric_furnace", () -> BlockEntityType.Builder.of((pos, state) ->
                    new MultiSlotMachineBlockEntity(ModBlockEntities.MAX_ELECTRIC_FURNACE.get(), pos, state, MultiSlotMachineBlockEntity.MachineType.ELECTRIC_FURNACE, 12, 1000000, 144, 12), ModBlocks.MAX_ELECTRIC_FURNACE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MultiSlotMachineBlockEntity>> MAX_COMPRESSOR =
            BLOCK_ENTITIES.register("max_compressor", () -> BlockEntityType.Builder.of((pos, state) ->
                    new MultiSlotMachineBlockEntity(ModBlockEntities.MAX_COMPRESSOR.get(), pos, state, MultiSlotMachineBlockEntity.MachineType.COMPRESSOR, 12, 1000000, 96, 30), ModBlocks.MAX_COMPRESSOR.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MultiSlotMachineBlockEntity>> MAX_EXTRACTOR =
            BLOCK_ENTITIES.register("max_extractor", () -> BlockEntityType.Builder.of((pos, state) ->
                    new MultiSlotMachineBlockEntity(ModBlockEntities.MAX_EXTRACTOR.get(), pos, state, MultiSlotMachineBlockEntity.MachineType.EXTRACTOR, 12, 1000000, 96, 25), ModBlocks.MAX_EXTRACTOR.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MultiSlotMachineBlockEntity>> MAX_METAL_FORMER =
            BLOCK_ENTITIES.register("max_metal_former", () -> BlockEntityType.Builder.of((pos, state) ->
                    new MultiSlotMachineBlockEntity(ModBlockEntities.MAX_METAL_FORMER.get(), pos, state, MultiSlotMachineBlockEntity.MachineType.METAL_FORMER, 12, 1000000, 120, 18), ModBlocks.MAX_METAL_FORMER.get()).build(null));

    // --- Generators & Solar Panels ---
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GeneratorBlockEntity>> GENERATOR =
            BLOCK_ENTITIES.register("generator", () -> BlockEntityType.Builder.of(GeneratorBlockEntity::new, ModBlocks.GENERATOR.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SolarPanelBlockEntity>> SOLAR_PANEL =
            BLOCK_ENTITIES.register("solar_panel", () -> BlockEntityType.Builder.of((pos, state) ->
                    new SolarPanelBlockEntity(ModBlockEntities.SOLAR_PANEL.get(), pos, state, 4, 0, 800), ModBlocks.SOLAR_PANEL.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SolarPanelBlockEntity>> ADVANCED_SOLAR_PANEL =
            BLOCK_ENTITIES.register("advanced_solar_panel", () -> BlockEntityType.Builder.of((pos, state) ->
                    new SolarPanelBlockEntity(ModBlockEntities.ADVANCED_SOLAR_PANEL.get(), pos, state, 32, 4, 32000), ModBlocks.ADVANCED_SOLAR_PANEL.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SolarPanelBlockEntity>> HYBRID_SOLAR_PANEL =
            BLOCK_ENTITIES.register("hybrid_solar_panel", () -> BlockEntityType.Builder.of((pos, state) ->
                    new SolarPanelBlockEntity(ModBlockEntities.HYBRID_SOLAR_PANEL.get(), pos, state, 256, 32, 256000), ModBlocks.HYBRID_SOLAR_PANEL.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SolarPanelBlockEntity>> ULTIMATE_HYBRID_SOLAR_PANEL =
            BLOCK_ENTITIES.register("ultimate_hybrid_solar_panel", () -> BlockEntityType.Builder.of((pos, state) ->
                    new SolarPanelBlockEntity(ModBlockEntities.ULTIMATE_HYBRID_SOLAR_PANEL.get(), pos, state, 2048, 256, 2048000), ModBlocks.ULTIMATE_HYBRID_SOLAR_PANEL.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SolarPanelBlockEntity>> QUANTUM_SOLAR_PANEL =
            BLOCK_ENTITIES.register("quantum_solar_panel", () -> BlockEntityType.Builder.of((pos, state) ->
                    new SolarPanelBlockEntity(ModBlockEntities.QUANTUM_SOLAR_PANEL.get(), pos, state, 16384, 8192, 16384000), ModBlocks.QUANTUM_SOLAR_PANEL.get()).build(null));

    // --- Energy Storages ---
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnergyStorageBlockEntity>> BATBOX =
            BLOCK_ENTITIES.register("batbox", () -> BlockEntityType.Builder.of((pos, state) ->
                    new EnergyStorageBlockEntity(ModBlockEntities.BATBOX.get(), pos, state, "batbox", 40000, 128), ModBlocks.BATBOX.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnergyStorageBlockEntity>> CESU =
            BLOCK_ENTITIES.register("cesu", () -> BlockEntityType.Builder.of((pos, state) ->
                    new EnergyStorageBlockEntity(ModBlockEntities.CESU.get(), pos, state, "cesu", 300000, 512), ModBlocks.CESU.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnergyStorageBlockEntity>> MFE =
            BLOCK_ENTITIES.register("mfe", () -> BlockEntityType.Builder.of((pos, state) ->
                    new EnergyStorageBlockEntity(ModBlockEntities.MFE.get(), pos, state, "mfe", 4000000, 2048), ModBlocks.MFE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnergyStorageBlockEntity>> MFSU =
            BLOCK_ENTITIES.register("mfsu", () -> BlockEntityType.Builder.of((pos, state) ->
                    new EnergyStorageBlockEntity(ModBlockEntities.MFSU.get(), pos, state, "mfsu", 40000000, 8192), ModBlocks.MFSU.get()).build(null));

    // --- Cables ---
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CableBlockEntity>> COPPER_CABLE_UNINSULATED =
            BLOCK_ENTITIES.register("copper_cable_uninsulated", () -> BlockEntityType.Builder.of((pos, state) ->
                    new CableBlockEntity(ModBlockEntities.COPPER_CABLE_UNINSULATED.get(), pos, state, 128), ModBlocks.COPPER_CABLE_UNINSULATED.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CableBlockEntity>> COPPER_CABLE =
            BLOCK_ENTITIES.register("copper_cable", () -> BlockEntityType.Builder.of((pos, state) ->
                    new CableBlockEntity(ModBlockEntities.COPPER_CABLE.get(), pos, state, 128), ModBlocks.COPPER_CABLE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CableBlockEntity>> GOLD_CABLE_UNINSULATED =
            BLOCK_ENTITIES.register("gold_cable_uninsulated", () -> BlockEntityType.Builder.of((pos, state) ->
                    new CableBlockEntity(ModBlockEntities.GOLD_CABLE_UNINSULATED.get(), pos, state, 512), ModBlocks.GOLD_CABLE_UNINSULATED.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CableBlockEntity>> GOLD_CABLE_1X =
            BLOCK_ENTITIES.register("gold_cable_1x", () -> BlockEntityType.Builder.of((pos, state) ->
                    new CableBlockEntity(ModBlockEntities.GOLD_CABLE_1X.get(), pos, state, 512), ModBlocks.GOLD_CABLE_1X.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CableBlockEntity>> GOLD_CABLE_2X =
            BLOCK_ENTITIES.register("gold_cable_2x", () -> BlockEntityType.Builder.of((pos, state) ->
                    new CableBlockEntity(ModBlockEntities.GOLD_CABLE_2X.get(), pos, state, 512), ModBlocks.GOLD_CABLE_2X.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CableBlockEntity>> HV_CABLE_UNINSULATED =
            BLOCK_ENTITIES.register("hv_cable_uninsulated", () -> BlockEntityType.Builder.of((pos, state) ->
                    new CableBlockEntity(ModBlockEntities.HV_CABLE_UNINSULATED.get(), pos, state, 2048), ModBlocks.HV_CABLE_UNINSULATED.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CableBlockEntity>> HV_CABLE_1X =
            BLOCK_ENTITIES.register("hv_cable_1x", () -> BlockEntityType.Builder.of((pos, state) ->
                    new CableBlockEntity(ModBlockEntities.HV_CABLE_1X.get(), pos, state, 2048), ModBlocks.HV_CABLE_1X.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CableBlockEntity>> HV_CABLE_2X =
            BLOCK_ENTITIES.register("hv_cable_2x", () -> BlockEntityType.Builder.of((pos, state) ->
                    new CableBlockEntity(ModBlockEntities.HV_CABLE_2X.get(), pos, state, 2048), ModBlocks.HV_CABLE_2X.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CableBlockEntity>> HV_CABLE_3X =
            BLOCK_ENTITIES.register("hv_cable_3x", () -> BlockEntityType.Builder.of((pos, state) ->
                    new CableBlockEntity(ModBlockEntities.HV_CABLE_3X.get(), pos, state, 2048), ModBlocks.HV_CABLE_3X.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CableBlockEntity>> GLASS_FIBRE_CABLE =
            BLOCK_ENTITIES.register("glass_fibre_cable", () -> BlockEntityType.Builder.of((pos, state) ->
                    new CableBlockEntity(ModBlockEntities.GLASS_FIBRE_CABLE.get(), pos, state, 8192), ModBlocks.GLASS_FIBRE_CABLE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CableBlockEntity>> SUPERCONDUCTOR_CABLE =
            BLOCK_ENTITIES.register("superconductor_cable", () -> BlockEntityType.Builder.of((pos, state) ->
                    new CableBlockEntity(ModBlockEntities.SUPERCONDUCTOR_CABLE.get(), pos, state, 524288), ModBlocks.SUPERCONDUCTOR_CABLE.get()).build(null));
}

