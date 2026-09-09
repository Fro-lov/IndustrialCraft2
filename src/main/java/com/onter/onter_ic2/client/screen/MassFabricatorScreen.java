package com.onter.onter_ic2.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.onter.onter_ic2.OnterIC2;
import com.onter.onter_ic2.inventory.MassFabricatorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class MassFabricatorScreen extends AbstractContainerScreen<MassFabricatorMenu> {
    private static final ResourceLocation TEXTURE = OnterIC2.loc("textures/gui/guimatter.png");

    public MassFabricatorScreen(MassFabricatorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // Render progress bar (middle sphere or bar)
        int progress = menu.getProgress();
        int maxProgress = menu.getMaxProgress();
        if (maxProgress > 0 && progress > 0) {
            int progressWidth = (int) ((float) progress / maxProgress * 24);
            guiGraphics.blit(TEXTURE, x + 63, y + 36, 176, 0, progressWidth, 16);
        }
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        int progress = menu.getProgress();
        int maxProgress = menu.getMaxProgress();
        float percent = maxProgress > 0 ? (100.0F * progress / maxProgress) : 0.0F;

        guiGraphics.drawString(font, Component.literal(String.format("Progress: %.1f%%", percent)), x + 10, y + 20, 0x55FFFF, false);
        guiGraphics.drawString(font, Component.literal(String.format("Scrap: %,d", menu.getAmplifier())), x + 10, y + 35, 0xFFAA00, false);
        guiGraphics.drawString(font, Component.literal(String.format("EU: %,d", menu.getEnergy() / 4)), x + 10, y + 50, 0xFFFF55, false);
    }
}
