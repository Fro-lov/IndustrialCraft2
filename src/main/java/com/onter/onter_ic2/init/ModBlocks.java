package com.onter.onter_ic2.init;

import com.onter.onter_ic2.OnterIC2;
import com.onter.onter_ic2.block.cables.CableBlock;
import com.onter.onter_ic2.block.generators.GeneratorBlock;
import com.onter.onter_ic2.block.generators.SolarPanelBlock;
import com.onter.onter_ic2.block.machines.CompressorBlock;
import com.onter.onter_ic2.block.machines.ElectricFurnaceBlock;
import com.onter.onter_ic2.block.machines.ExtractorBlock;
import com.onter.onter_ic2.block.machines.MaceratorBlock;
import com.onter.onter_ic2.block.machines.MetalFormerBlock;
import com.onter.onter_ic2.block.storage.EnergyStorageBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(OnterIC2.MODID);

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> blockSupplier) {
        DeferredBlock<T> block = BLOCKS.register(name, blockSupplier);
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }

    // Machines
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

    // Generators & Solar Panels
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

    // Energy Storages
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

    // Cables
    public static final DeferredBlock<CableBlock> COPPER_CABLE = registerBlock("copper_cable",
            () -> new CableBlock(512, () -> ModBlockEntities.COPPER_CABLE.get(),
                    BlockBehaviour.Properties.of().strength(0.5F).sound(SoundType.WOOL)));

    public static final DeferredBlock<CableBlock> GOLD_CABLE = registerBlock("gold_cable",
            () -> new CableBlock(2048, () -> ModBlockEntities.GOLD_CABLE.get(),
                    BlockBehaviour.Properties.of().strength(0.6F).sound(SoundType.WOOL)));

    public static final DeferredBlock<CableBlock> HV_CABLE = registerBlock("hv_cable",
            () -> new CableBlock(8192, () -> ModBlockEntities.HV_CABLE.get(),
                    BlockBehaviour.Properties.of().strength(0.7F).sound(SoundType.WOOL)));

    public static final DeferredBlock<CableBlock> GLASS_FIBRE_CABLE = registerBlock("glass_fibre_cable",
            () -> new CableBlock(32768, () -> ModBlockEntities.GLASS_FIBRE_CABLE.get(),
                    BlockBehaviour.Properties.of().strength(0.8F).sound(SoundType.GLASS)));

    public static final DeferredBlock<CableBlock> SUPERCONDUCTOR_CABLE = registerBlock("superconductor_cable",
            () -> new CableBlock(Integer.MAX_VALUE, () -> ModBlockEntities.SUPERCONDUCTOR_CABLE.get(),
                    BlockBehaviour.Properties.of().strength(1.0F).sound(SoundType.AMETHYST)));
}
