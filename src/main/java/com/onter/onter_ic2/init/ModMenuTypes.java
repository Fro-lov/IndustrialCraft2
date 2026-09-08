package com.onter.onter_ic2.init;

import com.onter.onter_ic2.OnterIC2;
import com.onter.onter_ic2.menu.BaseMachineMenu;
import com.onter.onter_ic2.menu.EnergyStorageMenu;
import com.onter.onter_ic2.menu.GeneratorMenu;
import com.onter.onter_ic2.menu.MetalFormerMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, OnterIC2.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<BaseMachineMenu>> BASE_MACHINE_MENU =
            MENUS.register("base_machine", () -> IMenuTypeExtension.create(BaseMachineMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<GeneratorMenu>> GENERATOR_MENU =
            MENUS.register("generator", () -> IMenuTypeExtension.create(GeneratorMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<EnergyStorageMenu>> ENERGY_STORAGE_MENU =
            MENUS.register("energy_storage", () -> IMenuTypeExtension.create(EnergyStorageMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<MetalFormerMenu>> METAL_FORMER_MENU =
            MENUS.register("metal_former", () -> IMenuTypeExtension.create(MetalFormerMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<com.onter.onter_ic2.menu.SolarPanelMenu>> SOLAR_PANEL_MENU =
            MENUS.register("solar_panel", () -> IMenuTypeExtension.create(com.onter.onter_ic2.menu.SolarPanelMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<com.onter.onter_ic2.menu.MultiSlotMachineMenu>> MULTI_SLOT_MACHINE_MENU =
            MENUS.register("multi_slot_machine", () -> IMenuTypeExtension.create(com.onter.onter_ic2.menu.MultiSlotMachineMenu::new));
}
