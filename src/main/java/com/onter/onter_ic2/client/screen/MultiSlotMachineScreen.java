package com.onter.onter_ic2.client.screen;

import com.onter.onter_ic2.OnterIC2;
import com.onter.onter_ic2.block.machines.MultiSlotMachineBlockEntity;
import com.onter.onter_ic2.menu.MultiSlotMachineMenu;
import com.onter.onter_ic2.recipe.MetalFormerRecipe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class MultiSlotMachineScreen extends AbstractContainerScreen<MultiSlotMachineMenu> {
    private static final ResourceLocation GUI_ELEMENTS = OnterIC2.loc("textures/gui/guielements.png");
    private static final ResourceLocation ICON_EXTRUDING = OnterIC2.loc("textures/gui/mode_icons/extruding.png");
    private static final ResourceLocation ICON_ROLLING = OnterIC2.loc("textures/gui/mode_icons/rolling.png");
    private static final ResourceLocation ICON_CUTTING = OnterIC2.loc("textures/gui/mode_icons/cutting.png");

    // =========================================================================
    // НАСТРОЙКИ КООРДИНАТ, РАЗМЕРОВ И UV-РАЗВЕРТКИ (МЕНЯТЬ ЗДЕСЬ)
    // =========================================================================

    // 1. Размеры окна GUI и заголовки текста
    public static final int GUI_WIDTH = 212;
    public static final int GUI_HEIGHT = 214;
    public static final int TITLE_X = 14;
    public static final int TITLE_Y = 8;
    public static final int INVENTORY_TITLE_X = 26;
    public static final int INVENTORY_TITLE_Y = 120;

    // 2. Полоса энергии (Energy Bar)
    public static final int ENERGY_BAR_X = 46;       // Позиция X в окне GUI
    public static final int ENERGY_BAR_Y = 105;      // Позиция Y в окне GUI
    public static final int ENERGY_BAR_U = 0;        // Смещение U (X) в guielements.png
    public static final int ENERGY_BAR_V = 16;       // Смещение V (Y) в guielements.png
    public static final int ENERGY_BAR_WIDTH = 129;  // Полная длина полоски (при 100% энергии)
    public static final int ENERGY_BAR_HEIGHT = 5;   // Высота полоски

    // 3. Иконка молнии (Lightning Icon)
    public static final int LIGHTNING_X = 36;        // Позиция X в окне GUI
    public static final int LIGHTNING_Y = 105;       // Позиция Y в окне GUI
    public static final int LIGHTNING_U = 134;       // Смещение U (X) в guielements.png
    public static final int LIGHTNING_V = 15;        // Смещение V (Y) в guielements.png
    public static final int LIGHTNING_WIDTH = 5;     // Ширина молнии
    public static final int LIGHTNING_HEIGHT = 7;    // Высота молнии

    // 4. Кнопка режима металлоформовщика (Metal Former Mode Button)
    public static final int MODE_BUTTON_X = 84;     // Позиция X в окне GUI
    public static final int MODE_BUTTON_Y = 80;      // Позиция Y в окне GUI
    public static final int MODE_BUTTON_SIZE = 16;   // Размер кнопки (16x16)

    // 5. Полоса/стрелка прогресса между слотами (Progress Bar)
    public static final int PROGRESS_X = 84;         // Позиция X между входами и выходами
    public static final int PROGRESS_Y = 55;         // Позиция Y между входами и выходами
    public static final int PROGRESS_U = 212;        // Смещение U (X) в правом верхнем углу PNG
    public static final int PROGRESS_V = 0;          // Смещение V (Y) в правом верхнем углу PNG
    public static final int PROGRESS_WIDTH = 16;     // Полная ширина стрелочки
    public static final int PROGRESS_HEIGHT = 17;    // Высота стрелочки

    // =========================================================================

    private final ResourceLocation backgroundTexture;
    private final boolean isMetalFormer;
    private final int numChannels;

    public MultiSlotMachineScreen(MultiSlotMachineMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = GUI_WIDTH;
        this.imageHeight = GUI_HEIGHT;
        this.titleLabelX = TITLE_X;
        this.titleLabelY = TITLE_Y;
        this.inventoryLabelX = INVENTORY_TITLE_X;
        this.inventoryLabelY = INVENTORY_TITLE_Y;

        MultiSlotMachineBlockEntity entity = menu.getBlockEntity();
        MultiSlotMachineBlockEntity.MachineType type = entity != null ? entity.getMachineType() : MultiSlotMachineBlockEntity.MachineType.ELECTRIC_FURNACE;
        this.numChannels = menu.getNumChannels();
        this.isMetalFormer = (type == MultiSlotMachineBlockEntity.MachineType.METAL_FORMER);

        String typeStr = switch (type) {
            case MACERATOR -> "guimacerator";
            case ELECTRIC_FURNACE -> "guielectricfurnace";
            case COMPRESSOR -> "guicompressor";
            case EXTRACTOR -> "guiextractor";
            case METAL_FORMER -> "guimetalformer";
        };
        String tierStr = numChannels == 6 ? "x6.png" : "x12.png";
        this.backgroundTexture = OnterIC2.loc("textures/gui/" + typeStr + tierStr);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // 1. Главный фон механизма
        guiGraphics.blit(backgroundTexture, x, y, 0, 0, imageWidth, imageHeight, 256, 256);

        // 2. Отрисовка полосы энергии
        int scaledEnergy = menu.getScaledEnergy(ENERGY_BAR_WIDTH);
        if (scaledEnergy > 0) {
            guiGraphics.blit(GUI_ELEMENTS, x + ENERGY_BAR_X, y + ENERGY_BAR_Y, ENERGY_BAR_U, ENERGY_BAR_V, scaledEnergy, ENERGY_BAR_HEIGHT, 256, 256);
        }

        // 3. Отрисовка иконки молнии
        guiGraphics.blit(GUI_ELEMENTS, x + LIGHTNING_X, y + LIGHTNING_Y, LIGHTNING_U, LIGHTNING_V, LIGHTNING_WIDTH, LIGHTNING_HEIGHT, 256, 256);

        // 4. Отрисовка стрелки/анимации прогресса работы
        int progress = menu.getScaledProgress(PROGRESS_WIDTH);
        if (progress > 0) {
            guiGraphics.blit(backgroundTexture, x + PROGRESS_X, y + PROGRESS_Y, PROGRESS_U, PROGRESS_V, progress, PROGRESS_HEIGHT, 256, 256);
        }

        // 5. Кнопка режима металлоформовщика (если применимо)
        if (isMetalFormer) {
            MetalFormerRecipe.Mode mode = menu.getMetalFormerMode();
            ResourceLocation modeIcon = switch (mode) {
                case EXTRUDING -> ICON_EXTRUDING;
                case ROLLING -> ICON_ROLLING;
                case CUTTING -> ICON_CUTTING;
            };
            guiGraphics.blit(modeIcon, x + MODE_BUTTON_X, y + MODE_BUTTON_Y, 0, 0, MODE_BUTTON_SIZE, MODE_BUTTON_SIZE, MODE_BUTTON_SIZE, MODE_BUTTON_SIZE);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && isMetalFormer && minecraft != null && minecraft.gameMode != null) {
            int x = (width - imageWidth) / 2;
            int y = (height - imageHeight) / 2;

            // Клик по кнопке режима металлоформовщика
            if (mouseX >= x + MODE_BUTTON_X && mouseX <= x + MODE_BUTTON_X + MODE_BUTTON_SIZE &&
                mouseY >= y + MODE_BUTTON_Y && mouseY <= y + MODE_BUTTON_Y + MODE_BUTTON_SIZE) {
                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 3);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // Всплывающая подсказка энергии (над молнией и всей полоской энергии)
        int minEnergyX = Math.min(LIGHTNING_X, ENERGY_BAR_X);
        int maxEnergyX = Math.max(LIGHTNING_X + LIGHTNING_WIDTH, ENERGY_BAR_X + ENERGY_BAR_WIDTH);
        int minEnergyY = Math.min(LIGHTNING_Y, ENERGY_BAR_Y) - 1;
        int maxEnergyY = Math.max(LIGHTNING_Y + LIGHTNING_HEIGHT, ENERGY_BAR_Y + ENERGY_BAR_HEIGHT) + 1;

        if (mouseX >= x + minEnergyX && mouseX <= x + maxEnergyX && mouseY >= y + minEnergyY && mouseY <= y + maxEnergyY) {
            int energy = menu.getEnergy();
            int maxEnergy = menu.getMaxEnergy();
            guiGraphics.renderComponentTooltip(font, List.of(
                    Component.literal("§eЭнергия: §f" + (energy / 4) + " / " + (maxEnergy / 4) + " EU"),
                    Component.literal("§7(" + energy + " / " + maxEnergy + " FE)")
            ), mouseX, mouseY);
        }

        // Всплывающая подсказка режима металлоформовщика
        if (isMetalFormer && mouseX >= x + MODE_BUTTON_X && mouseX <= x + MODE_BUTTON_X + MODE_BUTTON_SIZE &&
            mouseY >= y + MODE_BUTTON_Y && mouseY <= y + MODE_BUTTON_Y + MODE_BUTTON_SIZE) {
            MetalFormerRecipe.Mode mode = menu.getMetalFormerMode();
            String modeName = switch (mode) {
                case EXTRUDING -> "Выдавливание (Провода)";
                case ROLLING -> "Прокатка (Пластины)";
                case CUTTING -> "Резка (Кусачки / Оболочки)";
            };
            guiGraphics.renderComponentTooltip(font, List.of(
                    Component.literal("§eРежим: §a" + modeName),
                    Component.literal("§7[Нажмите для смены]")
            ), mouseX, mouseY);
        }
    }
}
