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
        registerMachineCapabilities(event, ModBlockEntities.ADVANCED_MACERATOR.get());
        registerMachineCapabilities(event, ModBlockEntities.ADVANCED_ELECTRIC_FURNACE.get());
        registerMachineCapabilities(event, ModBlockEntities.ADVANCED_COMPRESSOR.get());
        registerMachineCapabilities(event, ModBlockEntities.ADVANCED_EXTRACTOR.get());
        registerMachineCapabilities(event, ModBlockEntities.ADVANCED_METAL_FORMER.get());

        // Maximum Machines (x12)
        registerMachineCapabilities(event, ModBlockEntities.MAX_MACERATOR.get());
        registerMachineCapabilities(event, ModBlockEntities.MAX_ELECTRIC_FURNACE.get());
        registerMachineCapabilities(event, ModBlockEntities.MAX_COMPRESSOR.get());
        registerMachineCapabilities(event, ModBlockEntities.MAX_EXTRACTOR.get());
        registerMachineCapabilities(event, ModBlockEntities.MAX_METAL_FORMER.get());

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
    }

    private void registerMachineCapabilities(RegisterCapabilitiesEvent event, net.minecraft.world.level.block.entity.BlockEntityType<? extends BaseMachineBlockEntity> type) {
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
