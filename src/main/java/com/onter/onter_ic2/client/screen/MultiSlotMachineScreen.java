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

    private final ResourceLocation backgroundTexture;
    private final boolean isMetalFormer;
    private final int numChannels;

    public MultiSlotMachineScreen(MultiSlotMachineMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 228;
        this.imageHeight = 214;
        this.titleLabelX = 10;
        this.titleLabelY = 8;
        this.inventoryLabelX = 26;
        this.inventoryLabelY = 120;

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

        // Main Background (228x214)
        guiGraphics.blit(backgroundTexture, x, y, 0, 0, imageWidth, imageHeight, 256, 256);

        // Energy Bar from GuiElements.png (2nd row: u=0, v=10, width=144, height=8)
        // Located in GUI at (x + 24, y + 118)
        int scaledEnergy = menu.getScaledEnergy(144);
        if (scaledEnergy > 0) {
            guiGraphics.blit(GUI_ELEMENTS, x + 24, y + 118, 0, 10, scaledEnergy, 8, 256, 256);
        }

        // Lightning Icon at (x + 8, y + 115) from GuiElements.png (u=120, v=10, 14x14)
        guiGraphics.blit(GUI_ELEMENTS, x + 8, y + 115, 120, 10, 14, 14, 256, 256);

        // If Metal Former: Mode Button at (x + 202, y + 50, 16x16)
        if (isMetalFormer) {
            MetalFormerRecipe.Mode mode = menu.getMetalFormerMode();
            ResourceLocation modeIcon = switch (mode) {
                case EXTRUDING -> ICON_EXTRUDING;
                case ROLLING -> ICON_ROLLING;
                case CUTTING -> ICON_CUTTING;
            };
            guiGraphics.blit(modeIcon, x + 202, y + 50, 0, 0, 16, 16, 16, 16);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && isMetalFormer && minecraft != null && minecraft.gameMode != null) {
            int x = (width - imageWidth) / 2;
            int y = (height - imageHeight) / 2;

            // Click on Metal Former Mode Button (202..218, 50..66)
            if (mouseX >= x + 202 && mouseX <= x + 218 && mouseY >= y + 50 && mouseY <= y + 66) {
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

        // Energy Tooltip over Energy Bar & Lightning (8..170, 115..126)
        if (mouseX >= x + 8 && mouseX <= x + 170 && mouseY >= y + 115 && mouseY <= y + 126) {
            int energy = menu.getEnergy();
            int maxEnergy = menu.getMaxEnergy();
            guiGraphics.renderComponentTooltip(font, List.of(
                    Component.literal("§eЭнергия: §f" + (energy / 4) + " / " + (maxEnergy / 4) + " EU"),
                    Component.literal("§7(" + energy + " / " + maxEnergy + " FE)")
            ), mouseX, mouseY);
        }

        // Metal Former Mode Tooltip (202..218, 50..66)
        if (isMetalFormer && mouseX >= x + 202 && mouseX <= x + 218 && mouseY >= y + 50 && mouseY <= y + 66) {
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
