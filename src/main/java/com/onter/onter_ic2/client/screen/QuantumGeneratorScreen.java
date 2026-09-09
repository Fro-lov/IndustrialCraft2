package com.onter.onter_ic2.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.onter.onter_ic2.OnterIC2;
import com.onter.onter_ic2.menu.QuantumGeneratorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class QuantumGeneratorScreen extends AbstractContainerScreen<QuantumGeneratorMenu> {
    private static final ResourceLocation TEXTURE = OnterIC2.loc("textures/gui/quantum_generator.png");

    private final Button[] prodButtons = new Button[6];
    private static final String[] NORMAL_LABELS = {"-100", "-10", "-1", "+1", "+10", "+100"};
    private static final String[] SHIFT_LABELS = {"-500", "-50", "-5", "+5", "+50", "+500"};

    public QuantumGeneratorScreen(QuantumGeneratorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 193;
        this.inventoryLabelY = -100;
        this.titleLabelY = 7;
    }

    @Override
    protected void init() {
        super.init();
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        int[] xs = {6, 39, 66, 89, 110, 137};
        int[] ws = {32, 26, 20, 20, 26, 32};

        // Production buttons (y=40)
        for (int i = 0; i < 6; i++) {
            final int buttonIndex = i;
            prodButtons[i] = Button.builder(Component.literal(NORMAL_LABELS[i]), btn -> {
                int eventId = Screen.hasShiftDown() ? buttonIndex + 10 : buttonIndex;
                if (this.minecraft != null && this.minecraft.gameMode != null) {
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, eventId);
                }
            }).bounds(x + xs[i], y + 40, ws[i], 20).build();
            addRenderableWidget(prodButtons[i]);
        }

        // Tier buttons (y=84)
        int[] tierXs = {6, 32, 58, 84, 110, 138};
        int[] tierWs = {24, 24, 24, 24, 24, 32};
        String[] tierLabels = {"1", "2", "3", "4", "5", "MAX"};

        for (int i = 0; i < 6; i++) {
            final int eventId = 20 + i;
            Button tierBtn = Button.builder(Component.literal(tierLabels[i]), btn -> {
                if (this.minecraft != null && this.minecraft.gameMode != null) {
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, eventId);
                }
            }).bounds(x + tierXs[i], y + 84, tierWs[i], 20).build();
            addRenderableWidget(tierBtn);
        }
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // Active indicator: x=145, y=21, UV u=176, v=3, w=14, h=14
        if (menu.isActive()) {
            guiGraphics.blit(TEXTURE, x + 145, y + 21, 176, 3, 14, 14);
        }
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Update production button labels dynamically when shift is held
        boolean isShift = Screen.hasShiftDown();
        for (int i = 0; i < 6; i++) {
            if (prodButtons[i] != null) {
                prodButtons[i].setMessage(Component.literal(isShift ? SHIFT_LABELS[i] : NORMAL_LABELS[i]));
            }
        }

        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        int textColor = 0xFFFFFF;

        // Right aligned labels at x=88
        Component outputLabel = Component.translatable("advanced_solar_panels.gui.output");
        Component tierLabel = Component.translatable("advanced_solar_panels.gui.tier");
        guiGraphics.drawString(font, outputLabel, x + 88 - font.width(outputLabel), y + 25, textColor, false);
        guiGraphics.drawString(font, tierLabel, x + 88 - font.width(tierLabel), y + 69, textColor, false);

        // Left aligned values at x=95
        int prod = menu.getProduction();
        int tier = menu.getTier();
        String tierStr = tier > 5 ? "MAX" : Integer.toString(tier);

        guiGraphics.drawString(font, String.format("%,d EU/t", prod), x + 95, y + 25, textColor, false);
        guiGraphics.drawString(font, tierStr, x + 95, y + 69, textColor, false);
    }
}
