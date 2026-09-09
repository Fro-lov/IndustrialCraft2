package com.onter.onter_ic2.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.onter.onter_ic2.OnterIC2;
import com.onter.onter_ic2.inventory.NuclearReactorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class NuclearReactorScreen extends AbstractContainerScreen<NuclearReactorMenu> {
    private static final ResourceLocation TEXTURE = OnterIC2.loc("textures/gui/guinuclearreactor.png");

    public NuclearReactorScreen(NuclearReactorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 219;
        this.inventoryLabelY = 127;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // Gray-out inactive columns if chambers < 6
        int activeCols = Math.min(NuclearReactorMenu.GRID_COLS, 3 + menu.getChambers());
        if (activeCols < NuclearReactorMenu.GRID_COLS) {
            int inactiveX = x + 8 + activeCols * 18;
            int inactiveW = (NuclearReactorMenu.GRID_COLS - activeCols) * 18;
            int inactiveH = NuclearReactorMenu.GRID_ROWS * 18;
            guiGraphics.fill(inactiveX, y + 17, inactiveX + inactiveW, y + 17 + inactiveH, 0x88000000);
        }
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);

        // Render Heat and Output text in GUI
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        int heat = menu.getHeat();
        int maxHeat = menu.getMaxHeat();
        int outputEU = menu.getOutputEU();

        Component heatText = Component.literal(String.format("Heat: %,d / %,d", heat, maxHeat));
        Component outputText = Component.literal(String.format("Output: %d EU/t", outputEU));

        int heatColor = heat > (maxHeat * 0.7) ? 0xFFFF3333 : (heat > (maxHeat * 0.4) ? 0xFFFFAA00 : 0xFF55FF55);
        guiGraphics.drawString(font, heatText, x + 8, y + 5, heatColor, false);
        guiGraphics.drawString(font, outputText, x + 105, y + 5, 0xFFFFFF55, false);
    }
}
