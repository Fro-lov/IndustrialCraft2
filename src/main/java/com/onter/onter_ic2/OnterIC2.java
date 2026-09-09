package com.onter.onter_ic2;

import com.onter.onter_ic2.block.base.BaseMachineBlockEntity;
import com.onter.onter_ic2.block.cables.CableBlockEntity;
import com.onter.onter_ic2.block.generators.GeneratorBlockEntity;
import com.onter.onter_ic2.block.generators.SolarPanelBlockEntity;
import com.onter.onter_ic2.block.storage.EnergyStorageBlockEntity;
import com.onter.onter_ic2.init.*;
import com.onter.onter_ic2.item.BatteryItem;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(OnterIC2.MODID)
public class OnterIC2 {
    public static final String MODID = "onter_ic2";
    public static final Logger LOGGER = LoggerFactory.getLogger(OnterIC2.class);

    public OnterIC2(IEventBus modEventBus) {
        LOGGER.info("Initializing Onter IC2 Mod (1.21.1 NeoForge)...");

        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModSounds.SOUND_EVENTS.register(modEventBus);
        ModMenuTypes.MENUS.register(modEventBus);
        ModRecipeTypes.RECIPE_TYPES.register(modEventBus);
        ModRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);

        modEventBus.addListener(this::registerCapabilities);
    }

    public static ResourceLocation loc(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        // Machines Energy & Item capabilities
        registerMachineCapabilities(event, ModBlockEntities.MACERATOR.get());
        registerMachineCapabilities(event, ModBlockEntities.ELECTRIC_FURNACE.get());
        registerMachineCapabilities(event, ModBlockEntities.COMPRESSOR.get());
        registerMachineCapabilities(event, ModBlockEntities.EXTRACTOR.get());
        registerMachineCapabilities(event, ModBlockEntities.METAL_FORMER.get());

        // Advanced Machines (x6)
        registerMultiSlotMachineCapabilities(event, ModBlockEntities.ADVANCED_MACERATOR.get());
        registerMultiSlotMachineCapabilities(event, ModBlockEntities.ADVANCED_ELECTRIC_FURNACE.get());
        registerMultiSlotMachineCapabilities(event, ModBlockEntities.ADVANCED_COMPRESSOR.get());
        registerMultiSlotMachineCapabilities(event, ModBlockEntities.ADVANCED_EXTRACTOR.get());
        registerMultiSlotMachineCapabilities(event, ModBlockEntities.ADVANCED_METAL_FORMER.get());

        // Maximum Machines (x12)
        registerMultiSlotMachineCapabilities(event, ModBlockEntities.MAX_MACERATOR.get());
        registerMultiSlotMachineCapabilities(event, ModBlockEntities.MAX_ELECTRIC_FURNACE.get());
        registerMultiSlotMachineCapabilities(event, ModBlockEntities.MAX_COMPRESSOR.get());
        registerMultiSlotMachineCapabilities(event, ModBlockEntities.MAX_EXTRACTOR.get());
        registerMultiSlotMachineCapabilities(event, ModBlockEntities.MAX_METAL_FORMER.get());

        // Generator
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.GENERATOR.get(), (be, side) -> be.getEnergyStorage());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.GENERATOR.get(), (be, side) -> be.getItemHandler());

        // Solar Panels
        registerSolarCapabilities(event, ModBlockEntities.SOLAR_PANEL.get());
        registerSolarCapabilities(event, ModBlockEntities.ADVANCED_SOLAR_PANEL.get());
        registerSolarCapabilities(event, ModBlockEntities.HYBRID_SOLAR_PANEL.get());
        registerSolarCapabilities(event, ModBlockEntities.ULTIMATE_HYBRID_SOLAR_PANEL.get());
        registerSolarCapabilities(event, ModBlockEntities.QUANTUM_SOLAR_PANEL.get());

        // Energy Storages
        registerStorageCapabilities(event, ModBlockEntities.BATBOX.get());
        registerStorageCapabilities(event, ModBlockEntities.CESU.get());
        registerStorageCapabilities(event, ModBlockEntities.MFE.get());
        registerStorageCapabilities(event, ModBlockEntities.MFSU.get());

        // Cables
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.COPPER_CABLE_UNINSULATED.get(), (be, side) -> be.getEnergyStorage());
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.COPPER_CABLE.get(), (be, side) -> be.getEnergyStorage());
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.GOLD_CABLE_UNINSULATED.get(), (be, side) -> be.getEnergyStorage());
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.GOLD_CABLE_1X.get(), (be, side) -> be.getEnergyStorage());
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.GOLD_CABLE_2X.get(), (be, side) -> be.getEnergyStorage());
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.HV_CABLE_UNINSULATED.get(), (be, side) -> be.getEnergyStorage());
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.HV_CABLE_1X.get(), (be, side) -> be.getEnergyStorage());
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.HV_CABLE_2X.get(), (be, side) -> be.getEnergyStorage());
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.HV_CABLE_3X.get(), (be, side) -> be.getEnergyStorage());
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.GLASS_FIBRE_CABLE.get(), (be, side) -> be.getEnergyStorage());
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.SUPERCONDUCTOR_CABLE.get(), (be, side) -> be.getEnergyStorage());

        // Battery Items Energy Capability
        registerBatteryItemCapability(event, ModItems.RE_BATTERY.get());
        registerBatteryItemCapability(event, ModItems.ENERGY_CRYSTAL.get());
        registerBatteryItemCapability(event, ModItems.LAPOTRON_CRYSTAL.get());

        // Nuclear Reactor & Chambers
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.NUCLEAR_REACTOR.get(), (be, side) -> be.getEnergyStorage());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.NUCLEAR_REACTOR.get(), (be, side) -> be.getItemHandler());

        // Mass Fabricator
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.MASS_FABRICATOR.get(), (be, side) -> be.getEnergyStorage());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.MASS_FABRICATOR.get(), (be, side) -> be.getItemHandler());

        // Unified Replicator
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.UNIFIED_REPLICATOR.get(), (be, side) -> be.getEnergyStorage());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.UNIFIED_REPLICATOR.get(), (be, side) -> be.getItemHandler());

        // Molecular Transformer
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.MOLECULAR_TRANSFORMER.get(), (be, side) -> be.getEnergyStorage());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.MOLECULAR_TRANSFORMER.get(), (be, side) -> be.getItemHandler());

        // Quantum Generator
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.QUANTUM_GENERATOR.get(), (be, side) -> be.getEnergyStorage());

        // Electric Armor & Jetpack & Batpacks & Solar Helmets Energy Capability
        registerElectricArmorCapability(event, ModItems.NANO_HELMET.get());
        registerElectricArmorCapability(event, ModItems.NANO_CHESTPLATE.get());
        registerElectricArmorCapability(event, ModItems.NANO_LEGGINGS.get());
        registerElectricArmorCapability(event, ModItems.NANO_BOOTS.get());
        registerElectricArmorCapability(event, ModItems.QUANTUM_HELMET.get());
        registerElectricArmorCapability(event, ModItems.QUANTUM_CHESTPLATE.get());
        registerElectricArmorCapability(event, ModItems.QUANTUM_LEGGINGS.get());
        registerElectricArmorCapability(event, ModItems.QUANTUM_BOOTS.get());
        registerElectricArmorCapability(event, ModItems.ELECTRIC_JETPACK.get());
        registerElectricArmorCapability(event, ModItems.BATPACK.get());
        registerElectricArmorCapability(event, ModItems.ADVANCED_BATPACK.get());
        registerElectricArmorCapability(event, ModItems.ENERGY_PACK.get());
        registerElectricArmorCapability(event, ModItems.LAPPACK.get());
        registerElectricArmorCapability(event, ModItems.ADVANCED_SOLAR_HELMET.get());
        registerElectricArmorCapability(event, ModItems.HYBRID_SOLAR_HELMET.get());
        registerElectricArmorCapability(event, ModItems.ULTIMATE_SOLAR_HELMET.get());
    }

    private void registerElectricArmorCapability(RegisterCapabilitiesEvent event, com.onter.onter_ic2.item.armor.ElectricArmorItem armorItem) {
        event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, ctx) -> new IEnergyStorage() {
            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                return com.onter.onter_ic2.item.armor.ElectricArmorItem.receiveEnergy(stack, maxReceive, armorItem.getCapacity(), armorItem.getMaxTransfer(), simulate);
            }

            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
                return com.onter.onter_ic2.item.armor.ElectricArmorItem.extractEnergy(stack, maxExtract, armorItem.getMaxTransfer(), simulate);
            }

            @Override
            public int getEnergyStored() {
                return com.onter.onter_ic2.item.armor.ElectricArmorItem.getEnergy(stack);
            }

            @Override
            public int getMaxEnergyStored() {
                return armorItem.getCapacity();
            }

            @Override
            public boolean canExtract() {
                return true;
            }

            @Override
            public boolean canReceive() {
                return true;
            }
        }, armorItem);
    }

    private void registerMachineCapabilities(RegisterCapabilitiesEvent event, net.minecraft.world.level.block.entity.BlockEntityType<? extends BaseMachineBlockEntity> type) {
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, type, (be, side) -> be.getEnergyStorage());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, type, (be, side) -> be.getItemHandler(side));
    }

    private void registerMultiSlotMachineCapabilities(RegisterCapabilitiesEvent event, net.minecraft.world.level.block.entity.BlockEntityType<? extends com.onter.onter_ic2.block.machines.MultiSlotMachineBlockEntity> type) {
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, type, (be, side) -> be.getEnergyStorage());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, type, (be, side) -> be.getItemHandler(side));
    }

    private void registerSolarCapabilities(RegisterCapabilitiesEvent event, net.minecraft.world.level.block.entity.BlockEntityType<? extends SolarPanelBlockEntity> type) {
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, type, (be, side) -> be.getEnergyStorage());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, type, (be, side) -> be.getItemHandler());
    }

    private void registerStorageCapabilities(RegisterCapabilitiesEvent event, net.minecraft.world.level.block.entity.BlockEntityType<? extends EnergyStorageBlockEntity> type) {
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, type, (be, side) -> be.getEnergyStorageForSide(side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, type, (be, side) -> be.getItemHandler());
    }

    private void registerBatteryItemCapability(RegisterCapabilitiesEvent event, BatteryItem batteryItem) {
        event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, ctx) -> new IEnergyStorage() {
            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                return BatteryItem.receiveEnergy(stack, maxReceive, batteryItem.getCapacity(), batteryItem.getMaxTransfer(), simulate);
            }

            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
                return BatteryItem.extractEnergy(stack, maxExtract, batteryItem.getMaxTransfer(), simulate);
            }

            @Override
            public int getEnergyStored() {
                return BatteryItem.getEnergy(stack);
            }

            @Override
            public int getMaxEnergyStored() {
                return batteryItem.getCapacity();
            }

            @Override
            public boolean canExtract() {
                return true;
            }

            @Override
            public boolean canReceive() {
                return true;
            }
        }, batteryItem);
    }
}
