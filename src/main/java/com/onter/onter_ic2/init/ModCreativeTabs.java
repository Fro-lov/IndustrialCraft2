package com.onter.onter_ic2.init;

import com.onter.onter_ic2.OnterIC2;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, OnterIC2.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> IC2_TAB =
            CREATIVE_MODE_TABS.register("onter_ic2_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.onter_ic2"))
                    .icon(() -> new ItemStack(ModBlocks.MACERATOR.get()))
                    .displayItems((parameters, output) -> {
                        // Machines (Base)
                        output.accept(ModBlocks.MACERATOR.get());
                        output.accept(ModBlocks.ELECTRIC_FURNACE.get());
                        output.accept(ModBlocks.COMPRESSOR.get());
                        output.accept(ModBlocks.EXTRACTOR.get());
                        output.accept(ModBlocks.METAL_FORMER.get());

                        // Machines (Advanced x6)
                        output.accept(ModBlocks.ADVANCED_MACERATOR.get());
                        output.accept(ModBlocks.ADVANCED_ELECTRIC_FURNACE.get());
                        output.accept(ModBlocks.ADVANCED_COMPRESSOR.get());
                        output.accept(ModBlocks.ADVANCED_EXTRACTOR.get());
                        output.accept(ModBlocks.ADVANCED_METAL_FORMER.get());

                        // Machines (Maximum x12)
                        output.accept(ModBlocks.MAX_MACERATOR.get());
                        output.accept(ModBlocks.MAX_ELECTRIC_FURNACE.get());
                        output.accept(ModBlocks.MAX_COMPRESSOR.get());
                        output.accept(ModBlocks.MAX_EXTRACTOR.get());
                        output.accept(ModBlocks.MAX_METAL_FORMER.get());

                        // Generators & Solars
                        output.accept(ModBlocks.GENERATOR.get());
                        output.accept(ModBlocks.SOLAR_PANEL.get());
                        output.accept(ModBlocks.ADVANCED_SOLAR_PANEL.get());
                        output.accept(ModBlocks.HYBRID_SOLAR_PANEL.get());
                        output.accept(ModBlocks.ULTIMATE_HYBRID_SOLAR_PANEL.get());
                        output.accept(ModBlocks.QUANTUM_SOLAR_PANEL.get());

                        // Storage
                        output.accept(ModBlocks.BATBOX.get());
                        output.accept(ModBlocks.CESU.get());
                        output.accept(ModBlocks.MFE.get());
                        output.accept(ModBlocks.MFSU.get());

                        // Cables
                        output.accept(ModBlocks.COPPER_CABLE_UNINSULATED.get());
                        output.accept(ModBlocks.COPPER_CABLE.get());
                        output.accept(ModBlocks.GOLD_CABLE_UNINSULATED.get());
                        output.accept(ModBlocks.GOLD_CABLE_1X.get());
                        output.accept(ModBlocks.GOLD_CABLE_2X.get());
                        output.accept(ModBlocks.HV_CABLE_UNINSULATED.get());
                        output.accept(ModBlocks.HV_CABLE_1X.get());
                        output.accept(ModBlocks.HV_CABLE_2X.get());
                        output.accept(ModBlocks.HV_CABLE_3X.get());
                        output.accept(ModBlocks.GLASS_FIBRE_CABLE.get());
                        output.accept(ModBlocks.SUPERCONDUCTOR_CABLE.get());

                        // Tools & Meters
                        output.accept(ModItems.WRENCH.get());
                        output.accept(ModItems.ELECTRIC_WRENCH.get());
                        output.accept(ModItems.EU_METER.get());

                        // Armor & Jetpacks & Batpacks
                        output.accept(ModItems.NANO_HELMET.get());
                        output.accept(ModItems.NANO_CHESTPLATE.get());
                        output.accept(ModItems.NANO_LEGGINGS.get());
                        output.accept(ModItems.NANO_BOOTS.get());
                        output.accept(ModItems.QUANTUM_HELMET.get());
                        output.accept(ModItems.QUANTUM_CHESTPLATE.get());
                        output.accept(ModItems.QUANTUM_LEGGINGS.get());
                        output.accept(ModItems.QUANTUM_BOOTS.get());
                        output.accept(ModItems.ELECTRIC_JETPACK.get());
                        output.accept(ModItems.BATPACK.get());
                        output.accept(ModItems.ADVANCED_BATPACK.get());
                        output.accept(ModItems.ENERGY_PACK.get());
                        output.accept(ModItems.LAPPACK.get());

                        // Nuclear Reactor & Chambers
                        output.accept(ModBlocks.NUCLEAR_REACTOR.get());
                        output.accept(ModBlocks.REACTOR_CHAMBER.get());

                        // Reactor Components
                        output.accept(ModItems.URANIUM_FUEL_ROD_SINGLE.get());
                        output.accept(ModItems.URANIUM_FUEL_ROD_DUAL.get());
                        output.accept(ModItems.URANIUM_FUEL_ROD_QUAD.get());
                        output.accept(ModItems.MOX_FUEL_ROD_SINGLE.get());
                        output.accept(ModItems.MOX_FUEL_ROD_DUAL.get());
                        output.accept(ModItems.MOX_FUEL_ROD_QUAD.get());
                        output.accept(ModItems.DEPLETED_URANIUM_ROD_SINGLE.get());
                        output.accept(ModItems.DEPLETED_URANIUM_ROD_DUAL.get());
                        output.accept(ModItems.DEPLETED_URANIUM_ROD_QUAD.get());
                        output.accept(ModItems.DEPLETED_MOX_ROD_SINGLE.get());
                        output.accept(ModItems.DEPLETED_MOX_ROD_DUAL.get());
                        output.accept(ModItems.DEPLETED_MOX_ROD_QUAD.get());
                        output.accept(ModItems.HEAT_VENT.get());
                        output.accept(ModItems.REACTOR_HEAT_VENT.get());
                        output.accept(ModItems.OVERCLOCKED_HEAT_VENT.get());
                        output.accept(ModItems.ADVANCED_HEAT_VENT.get());
                        output.accept(ModItems.COMPONENT_HEAT_VENT.get());
                        output.accept(ModItems.HEAT_EXCHANGER.get());
                        output.accept(ModItems.REACTOR_HEAT_EXCHANGER.get());
                        output.accept(ModItems.COMPONENT_HEAT_EXCHANGER.get());
                        output.accept(ModItems.ADVANCED_HEAT_EXCHANGER.get());
                        output.accept(ModItems.COOLANT_CELL_10K.get());
                        output.accept(ModItems.COOLANT_CELL_30K.get());
                        output.accept(ModItems.COOLANT_CELL_60K.get());
                        output.accept(ModItems.NEUTRON_REFLECTOR.get());
                        output.accept(ModItems.THICK_NEUTRON_REFLECTOR.get());
                        output.accept(ModItems.IRIDIUM_NEUTRON_REFLECTOR.get());
                        output.accept(ModItems.REACTOR_PLATING.get());
                        output.accept(ModItems.CONTAINMENT_PLATING.get());
                        output.accept(ModItems.HEAT_CAPACITY_PLATING.get());

                        // Matter & Replicator
                        output.accept(ModBlocks.MASS_FABRICATOR.get());
                        output.accept(ModBlocks.UNIFIED_REPLICATOR.get());
                        output.accept(ModItems.UU_MATTER.get());
                        output.accept(ModItems.SCRAP.get());
                        output.accept(ModItems.SCRAP_BOX.get());

                        // Resources
                        output.accept(ModItems.RUBBER.get());
                        output.accept(ModItems.STICKY_RESIN.get());
                        output.accept(ModItems.ELECTRONIC_CIRCUIT.get());
                        output.accept(ModItems.ADVANCED_CIRCUIT.get());
                        output.accept(ModItems.MIXED_METAL_INGOT.get());
                        output.accept(ModItems.ADVANCED_ALLOY.get());
                        output.accept(ModItems.CARBON_FIBRE.get());
                        output.accept(ModItems.CARBON_MESH.get());
                        output.accept(ModItems.CARBON_PLATE.get());
                        output.accept(ModItems.RAW_IRIDIUM.get());
                        output.accept(ModItems.IRIDIUM_INGOT.get());
                        output.accept(ModItems.IRIDIUM_PLATE.get());
                        output.accept(ModItems.REINFORCED_IRIDIUM_PLATE.get());
                        output.accept(ModItems.SUNNARIUM_PART.get());
                        output.accept(ModItems.SUNNARIUM.get());
                        output.accept(ModItems.SUNNARIUM_PLATE.get());

                        // Batteries
                        output.accept(ModItems.RE_BATTERY.get());
                        output.accept(ModItems.ENERGY_CRYSTAL.get());
                        output.accept(ModItems.LAPOTRON_CRYSTAL.get());

                        // Upgrades
                        output.accept(ModItems.OVERCLOCKER_UPGRADE.get());
                        output.accept(ModItems.ENERGY_STORAGE_UPGRADE.get());
                        output.accept(ModItems.TRANSFORMER_UPGRADE.get());
                        output.accept(ModItems.EJECTOR_UPGRADE.get());
                        output.accept(ModItems.PULLING_UPGRADE.get());
                        output.accept(ModItems.FLUID_EJECTOR_UPGRADE.get());
                        output.accept(ModItems.FLUID_PULLING_UPGRADE.get());
                    }).build());
}
