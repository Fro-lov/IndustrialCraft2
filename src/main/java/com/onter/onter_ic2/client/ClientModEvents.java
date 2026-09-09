package com.onter.onter_ic2.client;

import com.onter.onter_ic2.OnterIC2;
import com.onter.onter_ic2.client.screen.BaseMachineScreen;
import com.onter.onter_ic2.client.screen.EnergyStorageScreen;
import com.onter.onter_ic2.client.screen.GeneratorScreen;
import com.onter.onter_ic2.client.screen.MetalFormerScreen;
import com.onter.onter_ic2.init.ModMenuTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = OnterIC2.MODID, value = Dist.CLIENT)
public class ClientModEvents {
    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.BASE_MACHINE_MENU.get(), BaseMachineScreen.MachineScreen::new);
        event.register(ModMenuTypes.GENERATOR_MENU.get(), GeneratorScreen::new);
        event.register(ModMenuTypes.ENERGY_STORAGE_MENU.get(), EnergyStorageScreen::new);
        event.register(ModMenuTypes.METAL_FORMER_MENU.get(), MetalFormerScreen::new);
        event.register(ModMenuTypes.SOLAR_PANEL_MENU.get(), com.onter.onter_ic2.client.screen.SolarPanelScreen::new);
        event.register(ModMenuTypes.MULTI_SLOT_MACHINE_MENU.get(), com.onter.onter_ic2.client.screen.MultiSlotMachineScreen::new);
        event.register(ModMenuTypes.NUCLEAR_REACTOR.get(), com.onter.onter_ic2.client.screen.NuclearReactorScreen::new);
        event.register(ModMenuTypes.MASS_FABRICATOR.get(), com.onter.onter_ic2.client.screen.MassFabricatorScreen::new);
        event.register(ModMenuTypes.UNIFIED_REPLICATOR.get(), com.onter.onter_ic2.client.screen.UnifiedReplicatorScreen::new);
    }
}
