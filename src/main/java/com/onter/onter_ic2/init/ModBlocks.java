package com.onter.onter_ic2.init;

import com.onter.onter_ic2.OnterIC2;
import com.onter.onter_ic2.block.cables.CableBlock;
import com.onter.onter_ic2.block.generators.GeneratorBlock;
import com.onter.onter_ic2.block.generators.SolarPanelBlock;
import com.onter.onter_ic2.block.machines.*;
import com.onter.onter_ic2.block.storage.EnergyStorageBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(OnterIC2.MODID);

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> blockSupplier) {
        DeferredBlock<T> block = BLOCKS.register(name, blockSupplier);
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }

    // --- Base Machines ---
    public static final DeferredBlock<MaceratorBlock> MACERATOR = registerBlock("macerator",
            () -> new MaceratorBlock(BlockBehaviour.Properties.of().strength(3.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()));

    public static final DeferredBlock<ElectricFurnaceBlock> ELECTRIC_FURNACE = registerBlock("electric_furnace",
            () -> new ElectricFurnaceBlock(BlockBehaviour.Properties.of().strength(3.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()));

    public static final DeferredBlock<CompressorBlock> COMPRESSOR = registerBlock("compressor",
            () -> new CompressorBlock(BlockBehaviour.Properties.of().strength(3.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()));

    public static final DeferredBlock<ExtractorBlock> EXTRACTOR = registerBlock("extractor",
            () -> new ExtractorBlock(BlockBehaviour.Properties.of().strength(3.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()));

    public static final DeferredBlock<MetalFormerBlock> METAL_FORMER = registerBlock("metal_former",
            () -> new MetalFormerBlock(BlockBehaviour.Properties.of().strength(3.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()));

    // --- Advanced Machines (x6) ---
    public static final DeferredBlock<GenericMachineBlock> ADVANCED_MACERATOR = registerBlock("advanced_macerator",
            () -> new GenericMachineBlock(
                    (pos, state) -> new MultiSlotMachineBlockEntity(ModBlockEntities.ADVANCED_MACERATOR.get(), pos, state, MultiSlotMachineBlockEntity.MachineType.MACERATOR, 6, 200000, 48, 50),
                    () -> ModSounds.MACERATOR_OP.get(), BlockBehaviour.Properties.of().strength(4.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()));

    public static final DeferredBlock<GenericMachineBlock> ADVANCED_ELECTRIC_FURNACE = registerBlock("advanced_electric_furnace",
            () -> new GenericMachineBlock(
                    (pos, state) -> new MultiSlotMachineBlockEntity(ModBlockEntities.ADVANCED_ELECTRIC_FURNACE.get(), pos, state, MultiSlotMachineBlockEntity.MachineType.ELECTRIC_FURNACE, 6, 200000, 72, 25),
                    () -> ModSounds.ELECTRO_FURNACE_LOOP.get(), BlockBehaviour.Properties.of().strength(4.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()));

    public static final DeferredBlock<GenericMachineBlock> ADVANCED_COMPRESSOR = registerBlock("advanced_compressor",
            () -> new GenericMachineBlock(
                    (pos, state) -> new MultiSlotMachineBlockEntity(ModBlockEntities.ADVANCED_COMPRESSOR.get(), pos, state, MultiSlotMachineBlockEntity.MachineType.COMPRESSOR, 6, 200000, 48, 65),
                    () -> ModSounds.COMPRESSOR_OP.get(), BlockBehaviour.Properties.of().strength(4.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()));

    public static final DeferredBlock<GenericMachineBlock> ADVANCED_EXTRACTOR = registerBlock("advanced_extractor",
            () -> new GenericMachineBlock(
                    (pos, state) -> new MultiSlotMachineBlockEntity(ModBlockEntities.ADVANCED_EXTRACTOR.get(), pos, state, MultiSlotMachineBlockEntity.MachineType.EXTRACTOR, 6, 200000, 48, 50),
                    () -> ModSounds.EXTRACTOR_OP.get(), BlockBehaviour.Properties.of().strength(4.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()));

    public static final DeferredBlock<GenericMachineBlock> ADVANCED_METAL_FORMER = registerBlock("advanced_metal_former",
            () -> new GenericMachineBlock(
                    (pos, state) -> new MultiSlotMachineBlockEntity(ModBlockEntities.ADVANCED_METAL_FORMER.get(), pos, state, MultiSlotMachineBlockEntity.MachineType.METAL_FORMER, 6, 200000, 60, 35),
                    () -> ModSounds.COMPRESSOR_OP.get(), BlockBehaviour.Properties.of().strength(4.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()));

    // --- Maximum Machines (x12) ---
    public static final DeferredBlock<GenericMachineBlock> MAX_MACERATOR = registerBlock("max_macerator",
            () -> new GenericMachineBlock(
                    (pos, state) -> new MultiSlotMachineBlockEntity(ModBlockEntities.MAX_MACERATOR.get(), pos, state, MultiSlotMachineBlockEntity.MachineType.MACERATOR, 12, 1000000, 96, 25),
                    () -> ModSounds.MACERATOR_OP.get(), BlockBehaviour.Properties.of().strength(5.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()));

    public static final DeferredBlock<GenericMachineBlock> MAX_ELECTRIC_FURNACE = registerBlock("max_electric_furnace",
            () -> new GenericMachineBlock(
                    (pos, state) -> new MultiSlotMachineBlockEntity(ModBlockEntities.MAX_ELECTRIC_FURNACE.get(), pos, state, MultiSlotMachineBlockEntity.MachineType.ELECTRIC_FURNACE, 12, 1000000, 144, 12),
                    () -> ModSounds.ELECTRO_FURNACE_LOOP.get(), BlockBehaviour.Properties.of().strength(5.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()));

    public static final DeferredBlock<GenericMachineBlock> MAX_COMPRESSOR = registerBlock("max_compressor",
            () -> new GenericMachineBlock(
                    (pos, state) -> new MultiSlotMachineBlockEntity(ModBlockEntities.MAX_COMPRESSOR.get(), pos, state, MultiSlotMachineBlockEntity.MachineType.COMPRESSOR, 12, 1000000, 96, 30),
                    () -> ModSounds.COMPRESSOR_OP.get(), BlockBehaviour.Properties.of().strength(5.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()));

    public static final DeferredBlock<GenericMachineBlock> MAX_EXTRACTOR = registerBlock("max_extractor",
            () -> new GenericMachineBlock(
                    (pos, state) -> new MultiSlotMachineBlockEntity(ModBlockEntities.MAX_EXTRACTOR.get(), pos, state, MultiSlotMachineBlockEntity.MachineType.EXTRACTOR, 12, 1000000, 96, 25),
                    () -> ModSounds.EXTRACTOR_OP.get(), BlockBehaviour.Properties.of().strength(5.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()));

    public static final DeferredBlock<GenericMachineBlock> MAX_METAL_FORMER = registerBlock("max_metal_former",
            () -> new GenericMachineBlock(
                    (pos, state) -> new MultiSlotMachineBlockEntity(ModBlockEntities.MAX_METAL_FORMER.get(), pos, state, MultiSlotMachineBlockEntity.MachineType.METAL_FORMER, 12, 1000000, 120, 18),
                    () -> ModSounds.COMPRESSOR_OP.get(), BlockBehaviour.Properties.of().strength(5.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()));

    // --- Generators & Solar Panels ---
    public static final DeferredBlock<GeneratorBlock> GENERATOR = registerBlock("generator",
            () -> new GeneratorBlock(BlockBehaviour.Properties.of().strength(3.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()));

    public static final DeferredBlock<SolarPanelBlock> SOLAR_PANEL = registerBlock("solar_panel",
            () -> new SolarPanelBlock(4, 0, 800, () -> ModBlockEntities.SOLAR_PANEL.get(),
                    BlockBehaviour.Properties.of().strength(2.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()));

    public static final DeferredBlock<SolarPanelBlock> ADVANCED_SOLAR_PANEL = registerBlock("advanced_solar_panel",
            () -> new SolarPanelBlock(32, 4, 32000, () -> ModBlockEntities.ADVANCED_SOLAR_PANEL.get(),
                    BlockBehaviour.Properties.of().strength(3.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()));

    public static final DeferredBlock<SolarPanelBlock> HYBRID_SOLAR_PANEL = registerBlock("hybrid_solar_panel",
            () -> new SolarPanelBlock(256, 32, 256000, () -> ModBlockEntities.HYBRID_SOLAR_PANEL.get(),
                    BlockBehaviour.Properties.of().strength(3.5F).sound(SoundType.METAL).requiresCorrectToolForDrops()));

    public static final DeferredBlock<SolarPanelBlock> ULTIMATE_HYBRID_SOLAR_PANEL = registerBlock("ultimate_hybrid_solar_panel",
            () -> new SolarPanelBlock(2048, 256, 2048000, () -> ModBlockEntities.ULTIMATE_HYBRID_SOLAR_PANEL.get(),
                    BlockBehaviour.Properties.of().strength(4.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()));

    public static final DeferredBlock<SolarPanelBlock> QUANTUM_SOLAR_PANEL = registerBlock("quantum_solar_panel",
            () -> new SolarPanelBlock(16384, 8192, 16384000, () -> ModBlockEntities.QUANTUM_SOLAR_PANEL.get(),
                    BlockBehaviour.Properties.of().strength(5.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()));

    // --- Energy Storages ---
    public static final DeferredBlock<EnergyStorageBlock> BATBOX = registerBlock("batbox",
            () -> new EnergyStorageBlock("batbox", 40000, 128, () -> ModBlockEntities.BATBOX.get(),
                    BlockBehaviour.Properties.of().strength(2.0F).sound(SoundType.WOOD)));

    public static final DeferredBlock<EnergyStorageBlock> CESU = registerBlock("cesu",
            () -> new EnergyStorageBlock("cesu", 300000, 512, () -> ModBlockEntities.CESU.get(),
                    BlockBehaviour.Properties.of().strength(3.0F).sound(SoundType.METAL)));

    public static final DeferredBlock<EnergyStorageBlock> MFE = registerBlock("mfe",
            () -> new EnergyStorageBlock("mfe", 4000000, 2048, () -> ModBlockEntities.MFE.get(),
                    BlockBehaviour.Properties.of().strength(3.5F).sound(SoundType.METAL)));

    public static final DeferredBlock<EnergyStorageBlock> MFSU = registerBlock("mfsu",
            () -> new EnergyStorageBlock("mfsu", 40000000, 8192, () -> ModBlockEntities.MFSU.get(),
                    BlockBehaviour.Properties.of().strength(4.0F).sound(SoundType.METAL)));

    // --- Cables & Wires ---
    // Copper (32 EU/t = 128 FE/t)
    public static final DeferredBlock<CableBlock> COPPER_CABLE_UNINSULATED = registerBlock("copper_cable_uninsulated",
            () -> new CableBlock(128, () -> ModBlockEntities.COPPER_CABLE_UNINSULATED.get(),
                    BlockBehaviour.Properties.of().strength(0.4F).sound(SoundType.WOOL)));

    public static final DeferredBlock<CableBlock> COPPER_CABLE = registerBlock("copper_cable",
            () -> new CableBlock(128, () -> ModBlockEntities.COPPER_CABLE.get(),
                    BlockBehaviour.Properties.of().strength(0.5F).sound(SoundType.WOOL)));

    // Gold (128 EU/t = 512 FE/t)
    public static final DeferredBlock<CableBlock> GOLD_CABLE_UNINSULATED = registerBlock("gold_cable_uninsulated",
            () -> new CableBlock(512, () -> ModBlockEntities.GOLD_CABLE_UNINSULATED.get(),
                    BlockBehaviour.Properties.of().strength(0.5F).sound(SoundType.WOOL)));

    public static final DeferredBlock<CableBlock> GOLD_CABLE_1X = registerBlock("gold_cable_1x",
            () -> new CableBlock(512, () -> ModBlockEntities.GOLD_CABLE_1X.get(),
                    BlockBehaviour.Properties.of().strength(0.6F).sound(SoundType.WOOL)));

    public static final DeferredBlock<CableBlock> GOLD_CABLE_2X = registerBlock("gold_cable_2x",
            () -> new CableBlock(512, () -> ModBlockEntities.GOLD_CABLE_2X.get(),
                    BlockBehaviour.Properties.of().strength(0.6F).sound(SoundType.WOOL)));

    // HV / Iron (512 EU/t = 2048 FE/t)
    public static final DeferredBlock<CableBlock> HV_CABLE_UNINSULATED = registerBlock("hv_cable_uninsulated",
            () -> new CableBlock(2048, () -> ModBlockEntities.HV_CABLE_UNINSULATED.get(),
                    BlockBehaviour.Properties.of().strength(0.6F).sound(SoundType.WOOL)));

    public static final DeferredBlock<CableBlock> HV_CABLE_1X = registerBlock("hv_cable_1x",
            () -> new CableBlock(2048, () -> ModBlockEntities.HV_CABLE_1X.get(),
                    BlockBehaviour.Properties.of().strength(0.7F).sound(SoundType.WOOL)));

    public static final DeferredBlock<CableBlock> HV_CABLE_2X = registerBlock("hv_cable_2x",
            () -> new CableBlock(2048, () -> ModBlockEntities.HV_CABLE_2X.get(),
                    BlockBehaviour.Properties.of().strength(0.7F).sound(SoundType.WOOL)));

    public static final DeferredBlock<CableBlock> HV_CABLE_3X = registerBlock("hv_cable_3x",
            () -> new CableBlock(2048, () -> ModBlockEntities.HV_CABLE_3X.get(),
                    BlockBehaviour.Properties.of().strength(0.7F).sound(SoundType.WOOL)));

    // Glass Fibre & Superconductor
    public static final DeferredBlock<CableBlock> GLASS_FIBRE_CABLE = registerBlock("glass_fibre_cable",
            () -> new CableBlock(8192, () -> ModBlockEntities.GLASS_FIBRE_CABLE.get(),
                    BlockBehaviour.Properties.of().strength(0.8F).sound(SoundType.GLASS)));

    public static final DeferredBlock<CableBlock> SUPERCONDUCTOR_CABLE = registerBlock("superconductor_cable",
            () -> new CableBlock(524288, () -> ModBlockEntities.SUPERCONDUCTOR_CABLE.get(),
                    BlockBehaviour.Properties.of().strength(1.0F).sound(SoundType.AMETHYST)));

    // --- Nuclear Reactor & Chambers ---
    public static final DeferredBlock<com.onter.onter_ic2.block.reactor.NuclearReactorBlock> NUCLEAR_REACTOR = registerBlock("nuclear_reactor",
            () -> new com.onter.onter_ic2.block.reactor.NuclearReactorBlock(BlockBehaviour.Properties.of().strength(6.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()));

    public static final DeferredBlock<com.onter.onter_ic2.block.reactor.ReactorChamberBlock> REACTOR_CHAMBER = registerBlock("reactor_chamber",
            () -> new com.onter.onter_ic2.block.reactor.ReactorChamberBlock(BlockBehaviour.Properties.of().strength(5.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()));

    // --- Mass Fabricator & Unified Replicator ---
    public static final DeferredBlock<MassFabricatorBlock> MASS_FABRICATOR = registerBlock("mass_fabricator",
            () -> new MassFabricatorBlock(BlockBehaviour.Properties.of().strength(5.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()));

    public static final DeferredBlock<UnifiedReplicatorBlock> UNIFIED_REPLICATOR = registerBlock("unified_replicator",
            () -> new UnifiedReplicatorBlock(BlockBehaviour.Properties.of().strength(5.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()));

    // --- Advanced Solar Panels Blocks ---
    public static final DeferredBlock<MolecularTransformerBlock> MOLECULAR_TRANSFORMER = registerBlock("molecular_transformer",
            () -> new MolecularTransformerBlock(BlockBehaviour.Properties.of().strength(5.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()));

    public static final DeferredBlock<com.onter.onter_ic2.block.generators.QuantumGeneratorBlock> QUANTUM_GENERATOR = registerBlock("quantum_generator",
            () -> new com.onter.onter_ic2.block.generators.QuantumGeneratorBlock(BlockBehaviour.Properties.of().strength(5.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()));
}
