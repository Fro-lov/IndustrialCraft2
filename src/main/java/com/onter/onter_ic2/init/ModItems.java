package com.onter.onter_ic2.init;

import com.onter.onter_ic2.OnterIC2;
import com.onter.onter_ic2.item.BatteryItem;
import com.onter.onter_ic2.item.EjectorUpgradeItem;
import com.onter.onter_ic2.item.PullingUpgradeItem;
import com.onter.onter_ic2.item.UpgradeItem;
import com.onter.onter_ic2.item.WrenchItem;
import net.minecraft.world.item.Item;
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

    // Batteries
    public static final DeferredItem<BatteryItem> RE_BATTERY = ITEMS.register("re_battery",
            () -> new BatteryItem(40000, 400, new Item.Properties())); // 10k EU
    public static final DeferredItem<BatteryItem> ENERGY_CRYSTAL = ITEMS.register("energy_crystal",
            () -> new BatteryItem(4000000, 8000, new Item.Properties())); // 1M EU
    public static final DeferredItem<BatteryItem> LAPOTRON_CRYSTAL = ITEMS.register("lapotron_crystal",
            () -> new BatteryItem(40000000, 32000, new Item.Properties())); // 10M EU

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
    public static final DeferredItem<com.onter.onter_ic2.item.FluidEjectorUpgradeItem> FLUID_EJECTOR_UPGRADE = ITEMS.register("fluid_ejector_upgrade",
            () -> new com.onter.onter_ic2.item.FluidEjectorUpgradeItem(new Item.Properties()));
    public static final DeferredItem<com.onter.onter_ic2.item.FluidPullingUpgradeItem> FLUID_PULLING_UPGRADE = ITEMS.register("fluid_pulling_upgrade",
            () -> new com.onter.onter_ic2.item.FluidPullingUpgradeItem(new Item.Properties()));

    // Tools
    public static final DeferredItem<WrenchItem> WRENCH = ITEMS.register("wrench",
            () -> new WrenchItem(new Item.Properties()));
    public static final DeferredItem<com.onter.onter_ic2.item.ElectricWrenchItem> ELECTRIC_WRENCH = ITEMS.register("electric_wrench",
            () -> new com.onter.onter_ic2.item.ElectricWrenchItem(new Item.Properties()));
}
