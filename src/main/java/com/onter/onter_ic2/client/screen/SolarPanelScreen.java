package com.onter.onter_ic2.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.onter.onter_ic2.OnterIC2;
import com.onter.onter_ic2.menu.SolarPanelMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class SolarPanelScreen extends AbstractContainerScreen<SolarPanelMenu> {
    private static final ResourceLocation TEXTURE = OnterIC2.loc("textures/gui/advanced_solar_panel.png");

    public SolarPanelScreen(SolarPanelMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 194;
        this.imageHeight = 168;
        this.inventoryLabelY = -100;
        this.titleLabelY = 7;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        int energy = menu.getEnergy();
        int maxEnergy = menu.getMaxEnergy();

        // Progress gauge at x=19, y=24, UV u=195, v=0, w=24, h=14
        if (maxEnergy > 0 && energy > 0) {
            int gaugeWidth = (int) Math.min(24, Math.round(24.0 * energy / maxEnergy));
            if (gaugeWidth > 0) {
                guiGraphics.blit(TEXTURE, x + 19, y + 24, 195, 0, gaugeWidth, 14);
            }
        }

        // Sun / Moon icon at x=24, y=41, w=14, h=14
        int genRate = menu.getGeneratingRate();
        int dayGen = menu.getDayGen();
        if (menu.isSunVisible()) {
            if (genRate == dayGen && genRate > 0) {
                // Sun: u=195, v=15
                guiGraphics.blit(TEXTURE, x + 24, y + 41, 195, 15, 14, 14);
            } else if (genRate > 0) {
                // Moon: u=210, v=15
                guiGraphics.blit(TEXTURE, x + 24, y + 41, 210, 15, 14, 14);
            }
        }
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        int energy = menu.getEnergy();
        int maxEnergy = menu.getMaxEnergy();
        int genRate = menu.getGeneratingRate();
        int dayGen = menu.getDayGen();

        // Exact text positions from XML: x=50, y=22, 32, 42
        String storageStr = String.format("Storage: %,d / %,d", energy / 4, maxEnergy / 4);
        String maxOutputStr = String.format("Max Output: %d EU/t", dayGen / 4);
        String genStr = String.format("Generating: %d EU/t", genRate / 4);

        int textColor = 0xCDCDCD;
        guiGraphics.drawString(font, storageStr, x + 50, y + 22, textColor, false);
        guiGraphics.drawString(font, maxOutputStr, x + 50, y + 32, textColor, false);
        guiGraphics.drawString(font, genStr, x + 50, y + 42, textColor, false);
    }
}
