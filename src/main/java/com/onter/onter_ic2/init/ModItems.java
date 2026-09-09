package com.onter.onter_ic2.init;

import com.onter.onter_ic2.OnterIC2;
import com.onter.onter_ic2.item.*;
import com.onter.onter_ic2.item.armor.*;
import com.onter.onter_ic2.reactor.*;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(OnterIC2.MODID);

    // Basic Resources & Components
    public static final DeferredItem<Item> RUBBER = ITEMS.registerSimpleItem("rubber", new Item.Properties());
    public static final DeferredItem<Item> STICKY_RESIN = ITEMS.registerSimpleItem("sticky_resin", new Item.Properties());
    public static final DeferredItem<Item> ELECTRONIC_CIRCUIT = ITEMS.registerSimpleItem("electronic_circuit", new Item.Properties());
    public static final DeferredItem<Item> ADVANCED_CIRCUIT = ITEMS.registerSimpleItem("advanced_circuit", new Item.Properties());

    // Metals & Alloys
    public static final DeferredItem<Item> MIXED_METAL_INGOT = ITEMS.registerSimpleItem("mixed_metal_ingot", new Item.Properties());
    public static final DeferredItem<Item> ADVANCED_ALLOY = ITEMS.registerSimpleItem("advanced_alloy", new Item.Properties());
    public static final DeferredItem<Item> CARBON_FIBRE = ITEMS.registerSimpleItem("carbon_fibre", new Item.Properties());
    public static final DeferredItem<Item> CARBON_MESH = ITEMS.registerSimpleItem("carbon_mesh", new Item.Properties());
    public static final DeferredItem<Item> CARBON_PLATE = ITEMS.registerSimpleItem("carbon_plate", new Item.Properties());

    // Iridium line
    public static final DeferredItem<Item> RAW_IRIDIUM = ITEMS.registerSimpleItem("raw_iridium", new Item.Properties());
    public static final DeferredItem<Item> IRIDIUM_INGOT = ITEMS.registerSimpleItem("iridium_ingot", new Item.Properties());
    public static final DeferredItem<Item> IRIDIUM_PLATE = ITEMS.registerSimpleItem("iridium_plate", new Item.Properties());
    public static final DeferredItem<Item> REINFORCED_IRIDIUM_PLATE = ITEMS.registerSimpleItem("reinforced_iridium_plate", new Item.Properties());

    // Sunnarium line (Super Solar Panels)
    public static final DeferredItem<Item> SUNNARIUM_PART = ITEMS.registerSimpleItem("sunnarium_part", new Item.Properties());
    public static final DeferredItem<Item> SUNNARIUM = ITEMS.registerSimpleItem("sunnarium", new Item.Properties());
    public static final DeferredItem<Item> SUNNARIUM_PLATE = ITEMS.registerSimpleItem("sunnarium_plate", new Item.Properties());

    // UU-Matter & Scrap
    public static final DeferredItem<Item> UU_MATTER = ITEMS.registerSimpleItem("uu_matter", new Item.Properties());
    public static final DeferredItem<Item> SCRAP = ITEMS.registerSimpleItem("scrap", new Item.Properties());
    public static final DeferredItem<ScrapBoxItem> SCRAP_BOX = ITEMS.register("scrap_box",
            () -> new ScrapBoxItem(new Item.Properties()));

    // Batteries
    public static final DeferredItem<BatteryItem> RE_BATTERY = ITEMS.register("re_battery",
            () -> new BatteryItem(40000, 400, new Item.Properties())); // 10k EU
    public static final DeferredItem<BatteryItem> ENERGY_CRYSTAL = ITEMS.register("energy_crystal",
            () -> new BatteryItem(4000000, 8000, new Item.Properties())); // 1M EU
    public static final DeferredItem<BatteryItem> LAPOTRON_CRYSTAL = ITEMS.register("lapotron_crystal",
            () -> new BatteryItem(40000000, 32000, new Item.Properties())); // 10M EU

    // Nano Suit
    public static final DeferredItem<NanoArmorItem> NANO_HELMET = ITEMS.register("nano_helmet",
            () -> new NanoArmorItem(ArmorItem.Type.HELMET, new Item.Properties()));
    public static final DeferredItem<NanoArmorItem> NANO_CHESTPLATE = ITEMS.register("nano_chestplate",
            () -> new NanoArmorItem(ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final DeferredItem<NanoArmorItem> NANO_LEGGINGS = ITEMS.register("nano_leggings",
            () -> new NanoArmorItem(ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final DeferredItem<NanoArmorItem> NANO_BOOTS = ITEMS.register("nano_boots",
            () -> new NanoArmorItem(ArmorItem.Type.BOOTS, new Item.Properties()));

    // Quantum Suit
    public static final DeferredItem<QuantumArmorItem> QUANTUM_HELMET = ITEMS.register("quantum_helmet",
            () -> new QuantumArmorItem(ArmorItem.Type.HELMET, new Item.Properties()));
    public static final DeferredItem<QuantumArmorItem> QUANTUM_CHESTPLATE = ITEMS.register("quantum_chestplate",
            () -> new QuantumArmorItem(ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final DeferredItem<QuantumArmorItem> QUANTUM_LEGGINGS = ITEMS.register("quantum_leggings",
            () -> new QuantumArmorItem(ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final DeferredItem<QuantumArmorItem> QUANTUM_BOOTS = ITEMS.register("quantum_boots",
            () -> new QuantumArmorItem(ArmorItem.Type.BOOTS, new Item.Properties()));

    // Jetpacks & Batpacks
    public static final DeferredItem<ElectricJetpackItem> ELECTRIC_JETPACK = ITEMS.register("electric_jetpack",
            () -> new ElectricJetpackItem(new Item.Properties()));
    public static final DeferredItem<BatpackItem> BATPACK = ITEMS.register("batpack",
            () -> new BatpackItem(new Item.Properties(), 240_000, 512, 1)); // 60k EU
    public static final DeferredItem<BatpackItem> ADVANCED_BATPACK = ITEMS.register("advanced_batpack",
            () -> new BatpackItem(new Item.Properties(), 2_400_000, 4096, 2)); // 600k EU
    public static final DeferredItem<BatpackItem> ENERGY_PACK = ITEMS.register("energy_pack",
            () -> new BatpackItem(new Item.Properties(), 8_000_000, 16384, 3)); // 2M EU
    public static final DeferredItem<BatpackItem> LAPPACK = ITEMS.register("lappack",
            () -> new BatpackItem(new Item.Properties(), 80_000_000, 65536, 4)); // 20M EU

    // Reactor Fuel Rods (Uranium & MOX)
    public static final DeferredItem<FuelRodItem> URANIUM_FUEL_ROD_SINGLE = ITEMS.register("uranium_fuel_rod_single",
            () -> new FuelRodItem(new Item.Properties(), 1, false, () -> new ItemStack(ModItems.DEPLETED_URANIUM_ROD_SINGLE.get())));
    public static final DeferredItem<FuelRodItem> URANIUM_FUEL_ROD_DUAL = ITEMS.register("uranium_fuel_rod_dual",
            () -> new FuelRodItem(new Item.Properties(), 2, false, () -> new ItemStack(ModItems.DEPLETED_URANIUM_ROD_DUAL.get())));
    public static final DeferredItem<FuelRodItem> URANIUM_FUEL_ROD_QUAD = ITEMS.register("uranium_fuel_rod_quad",
            () -> new FuelRodItem(new Item.Properties(), 4, false, () -> new ItemStack(ModItems.DEPLETED_URANIUM_ROD_QUAD.get())));

    public static final DeferredItem<FuelRodItem> MOX_FUEL_ROD_SINGLE = ITEMS.register("mox_fuel_rod_single",
            () -> new FuelRodItem(new Item.Properties(), 1, true, () -> new ItemStack(ModItems.DEPLETED_MOX_ROD_SINGLE.get())));
    public static final DeferredItem<FuelRodItem> MOX_FUEL_ROD_DUAL = ITEMS.register("mox_fuel_rod_dual",
            () -> new FuelRodItem(new Item.Properties(), 2, true, () -> new ItemStack(ModItems.DEPLETED_MOX_ROD_DUAL.get())));
    public static final DeferredItem<FuelRodItem> MOX_FUEL_ROD_QUAD = ITEMS.register("mox_fuel_rod_quad",
            () -> new FuelRodItem(new Item.Properties(), 4, true, () -> new ItemStack(ModItems.DEPLETED_MOX_ROD_QUAD.get())));

    // Depleted Fuel Rods
    public static final DeferredItem<Item> DEPLETED_URANIUM_ROD_SINGLE = ITEMS.registerSimpleItem("depleted_uranium_rod_single", new Item.Properties());
    public static final DeferredItem<Item> DEPLETED_URANIUM_ROD_DUAL = ITEMS.registerSimpleItem("depleted_uranium_rod_dual", new Item.Properties());
    public static final DeferredItem<Item> DEPLETED_URANIUM_ROD_QUAD = ITEMS.registerSimpleItem("depleted_uranium_rod_quad", new Item.Properties());
    public static final DeferredItem<Item> DEPLETED_MOX_ROD_SINGLE = ITEMS.registerSimpleItem("depleted_mox_rod_single", new Item.Properties());
    public static final DeferredItem<Item> DEPLETED_MOX_ROD_DUAL = ITEMS.registerSimpleItem("depleted_mox_rod_dual", new Item.Properties());
    public static final DeferredItem<Item> DEPLETED_MOX_ROD_QUAD = ITEMS.registerSimpleItem("depleted_mox_rod_quad", new Item.Properties());

    // Reactor Vents
    public static final DeferredItem<HeatVentItem> HEAT_VENT = ITEMS.register("heat_vent",
            () -> new HeatVentItem(new Item.Properties(), 1000, 6, 0, 0));
    public static final DeferredItem<HeatVentItem> REACTOR_HEAT_VENT = ITEMS.register("reactor_heat_vent",
            () -> new HeatVentItem(new Item.Properties(), 1000, 5, 5, 0));
    public static final DeferredItem<HeatVentItem> OVERCLOCKED_HEAT_VENT = ITEMS.register("overclocked_heat_vent",
            () -> new HeatVentItem(new Item.Properties(), 1000, 20, 36, 0));
    public static final DeferredItem<HeatVentItem> ADVANCED_HEAT_VENT = ITEMS.register("advanced_heat_vent",
            () -> new HeatVentItem(new Item.Properties(), 1000, 12, 0, 0));
    public static final DeferredItem<HeatVentItem> COMPONENT_HEAT_VENT = ITEMS.register("component_heat_vent",
            () -> new HeatVentItem(new Item.Properties(), 0, 0, 0, 4));

    // Reactor Heat Exchangers
    public static final DeferredItem<HeatExchangerItem> HEAT_EXCHANGER = ITEMS.register("heat_exchanger",
            () -> new HeatExchangerItem(new Item.Properties(), 2500, 12, 4));
    public static final DeferredItem<HeatExchangerItem> REACTOR_HEAT_EXCHANGER = ITEMS.register("reactor_heat_exchanger",
            () -> new HeatExchangerItem(new Item.Properties(), 5000, 0, 72));
    public static final DeferredItem<HeatExchangerItem> COMPONENT_HEAT_EXCHANGER = ITEMS.register("component_heat_exchanger",
            () -> new HeatExchangerItem(new Item.Properties(), 5000, 36, 0));
    public static final DeferredItem<HeatExchangerItem> ADVANCED_HEAT_EXCHANGER = ITEMS.register("advanced_heat_exchanger",
            () -> new HeatExchangerItem(new Item.Properties(), 10000, 24, 8));

    // Coolant Cells
    public static final DeferredItem<CoolantCellItem> COOLANT_CELL_10K = ITEMS.register("coolant_cell_10k",
            () -> new CoolantCellItem(new Item.Properties(), 10000));
    public static final DeferredItem<CoolantCellItem> COOLANT_CELL_30K = ITEMS.register("coolant_cell_30k",
            () -> new CoolantCellItem(new Item.Properties(), 30000));
    public static final DeferredItem<CoolantCellItem> COOLANT_CELL_60K = ITEMS.register("coolant_cell_60k",
            () -> new CoolantCellItem(new Item.Properties(), 60000));

    // Reflectors & Platings
    public static final DeferredItem<NeutronReflectorItem> NEUTRON_REFLECTOR = ITEMS.register("neutron_reflector",
            () -> new NeutronReflectorItem(new Item.Properties(), 30000));
    public static final DeferredItem<NeutronReflectorItem> THICK_NEUTRON_REFLECTOR = ITEMS.register("thick_neutron_reflector",
            () -> new NeutronReflectorItem(new Item.Properties(), 120000));
    public static final DeferredItem<NeutronReflectorItem> IRIDIUM_NEUTRON_REFLECTOR = ITEMS.register("iridium_neutron_reflector",
            () -> new NeutronReflectorItem(new Item.Properties(), 0));

    public static final DeferredItem<ReactorPlatingItem> REACTOR_PLATING = ITEMS.register("reactor_plating",
            () -> new ReactorPlatingItem(new Item.Properties(), 1000, 0.95F));
    public static final DeferredItem<ReactorPlatingItem> CONTAINMENT_PLATING = ITEMS.register("containment_plating",
            () -> new ReactorPlatingItem(new Item.Properties(), 500, 0.90F));
    public static final DeferredItem<ReactorPlatingItem> HEAT_CAPACITY_PLATING = ITEMS.register("heat_capacity_plating",
            () -> new ReactorPlatingItem(new Item.Properties(), 1700, 0.99F));

    // Upgrades
    public static final DeferredItem<UpgradeItem> OVERCLOCKER_UPGRADE = ITEMS.register("overclocker_upgrade",
            () -> new UpgradeItem(UpgradeItem.UpgradeType.OVERCLOCKER, new Item.Properties()));
    public static final DeferredItem<UpgradeItem> ENERGY_STORAGE_UPGRADE = ITEMS.register("energy_storage_upgrade",
            () -> new UpgradeItem(UpgradeItem.UpgradeType.ENERGY_STORAGE, new Item.Properties()));
    public static final DeferredItem<UpgradeItem> TRANSFORMER_UPGRADE = ITEMS.register("transformer_upgrade",
            () -> new UpgradeItem(UpgradeItem.UpgradeType.TRANSFORMER, new Item.Properties()));
    public static final DeferredItem<EjectorUpgradeItem> EJECTOR_UPGRADE = ITEMS.register("ejector_upgrade",
            () -> new EjectorUpgradeItem(new Item.Properties()));
    public static final DeferredItem<PullingUpgradeItem> PULLING_UPGRADE = ITEMS.register("pulling_upgrade",
            () -> new PullingUpgradeItem(new Item.Properties()));
    public static final DeferredItem<FluidEjectorUpgradeItem> FLUID_EJECTOR_UPGRADE = ITEMS.register("fluid_ejector_upgrade",
            () -> new FluidEjectorUpgradeItem(new Item.Properties()));
    public static final DeferredItem<FluidPullingUpgradeItem> FLUID_PULLING_UPGRADE = ITEMS.register("fluid_pulling_upgrade",
            () -> new FluidPullingUpgradeItem(new Item.Properties()));

    // Tools
    public static final DeferredItem<WrenchItem> WRENCH = ITEMS.register("wrench",
            () -> new WrenchItem(new Item.Properties()));
    public static final DeferredItem<ElectricWrenchItem> ELECTRIC_WRENCH = ITEMS.register("electric_wrench",
            () -> new ElectricWrenchItem(new Item.Properties()));
    public static final DeferredItem<EUMeterItem> EU_METER = ITEMS.register("eu_meter",
            () -> new EUMeterItem(new Item.Properties()));

    // Advanced Solar Panels Items & Helmets
    public static final DeferredItem<SolarHelmetItem> ADVANCED_SOLAR_HELMET = ITEMS.register("advanced_solar_helmet",
            () -> new SolarHelmetItem(Ic2ArmorMaterials.NANO_SUIT, new Item.Properties(), 1_000_000, 3_000, 3, 8, 1, 2_000, 0.90, false));
    public static final DeferredItem<SolarHelmetItem> HYBRID_SOLAR_HELMET = ITEMS.register("hybrid_solar_helmet",
            () -> new SolarHelmetItem(Ic2ArmorMaterials.NANO_SUIT, new Item.Properties(), 10_000_000, 10_000, 4, 64, 8, 5_000, 1.00, true));
    public static final DeferredItem<SolarQuantumHelmetItem> ULTIMATE_SOLAR_HELMET = ITEMS.register("ultimate_solar_helmet",
            () -> new SolarQuantumHelmetItem(new Item.Properties(), 512, 64));

    public static final DeferredItem<Item> SUNNARIUM_ALLOY = ITEMS.registerSimpleItem("sunnarium_alloy", new Item.Properties());
    public static final DeferredItem<Item> ENRICHED_SUNNARIUM = ITEMS.registerSimpleItem("enriched_sunnarium", new Item.Properties());
    public static final DeferredItem<Item> ENRICHED_SUNNARIUM_ALLOY = ITEMS.registerSimpleItem("enriched_sunnarium_alloy", new Item.Properties());
    public static final DeferredItem<Item> IRRADIANT_GLASS_PANE = ITEMS.registerSimpleItem("irradiant_glass_pane", new Item.Properties());
    public static final DeferredItem<Item> IRRADIANT_URANIUM = ITEMS.registerSimpleItem("irradiant_uranium", new Item.Properties());
    public static final DeferredItem<Item> IRRADIANT_REINFORCED_PLATE = ITEMS.registerSimpleItem("irradiant_reinforced_plate", new Item.Properties());
    public static final DeferredItem<Item> IRIDIUM_IRON_PLATE = ITEMS.registerSimpleItem("iridium_iron_plate", new Item.Properties());
    public static final DeferredItem<Item> REINFORCED_IRIDIUM_IRON_PLATE = ITEMS.registerSimpleItem("reinforced_iridium_iron_plate", new Item.Properties());
    public static final DeferredItem<Item> MT_CORE = ITEMS.registerSimpleItem("mt_core", new Item.Properties());
    public static final DeferredItem<Item> QUANTUM_CORE = ITEMS.registerSimpleItem("quantum_core", new Item.Properties());
    public static final DeferredItem<Item> URANIUM_INGOT = ITEMS.registerSimpleItem("uranium_ingot", new Item.Properties());
}
