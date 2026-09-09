package com.onter.onter_ic2.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.onter.onter_ic2.OnterIC2;
import com.onter.onter_ic2.menu.MeterMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class MeterScreen extends AbstractContainerScreen<MeterMenu> {
    private static final ResourceLocation TEXTURE = OnterIC2.loc("textures/gui/guitooleumeter.png");
    private static final int CRT_COLOR = 0x20EBBE; // Classic IC2 meter CRT green/cyan color (2157374)

    public MeterScreen(MeterMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 217;
        this.inventoryLabelY = -100;
        this.titleLabelY = -100;
    }

    @Override
    protected void init() {
        super.init();
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // Mode switch buttons (20x20 each)
        // EnergyIn (0): 112, 55
        addRenderableWidget(Button.builder(Component.empty(), btn -> sendButton(0))
                .bounds(x + 112, y + 55, 20, 20).build());
        // EnergyOut (1): 132, 55
        addRenderableWidget(Button.builder(Component.empty(), btn -> sendButton(1))
                .bounds(x + 132, y + 55, 20, 20).build());
        // EnergyGain (2): 112, 75
        addRenderableWidget(Button.builder(Component.empty(), btn -> sendButton(2))
                .bounds(x + 112, y + 75, 20, 20).build());
        // Voltage (3): 132, 75
        addRenderableWidget(Button.builder(Component.empty(), btn -> sendButton(3))
                .bounds(x + 132, y + 75, 20, 20).build());

        // Reset button: 26, 111, width 58, height 14
        addRenderableWidget(Button.builder(Component.translatable("ic2.meter.mode.reset"), btn -> sendButton(4))
                .bounds(x + 26, y + 111, 58, 14).build());
    }

    private void sendButton(int buttonId) {
        if (this.minecraft != null && this.minecraft.gameMode != null) {
            this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, buttonId);
        }
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // Active mode dial texture overlay (40x40 at x=112, y=55)
        MeterMenu.Mode mode = menu.getMode();
        int u = 176;
        int v = switch (mode) {
            case EnergyIn -> 0;
            case EnergyOut -> 40;
            case Voltage -> 80;
            case EnergyGain -> 120;
        };
        guiGraphics.blit(TEXTURE, x + 112, y + 55, u, v, 40, 40);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        MeterMenu.Mode mode = menu.getMode();
        String unit = mode == MeterMenu.Mode.Voltage ? " V" : " EU/t";

        int avg = menu.getResultAvg();
        int min = menu.getResultMin();
        int max = menu.getResultMax();
        int cycleSeconds = menu.getResultCount() / 20;

        // Draw meter readouts in classic CRT font
        guiGraphics.drawString(font, Component.translatable("ic2.meter.mode"), x + 115, y + 43, CRT_COLOR, false);

        guiGraphics.drawString(font, Component.translatable("ic2.meter.avg"), x + 15, y + 41, CRT_COLOR, false);
        guiGraphics.drawString(font, String.format("%,d%s", avg, unit), x + 15, y + 51, CRT_COLOR, false);

        guiGraphics.drawString(font, Component.translatable("ic2.meter.max_min"), x + 15, y + 64, CRT_COLOR, false);
        guiGraphics.drawString(font, String.format("%,d%s", max, unit), x + 15, y + 74, CRT_COLOR, false);
        guiGraphics.drawString(font, String.format("%,d%s", min, unit), x + 15, y + 84, CRT_COLOR, false);

        guiGraphics.drawString(font, Component.translatable("ic2.meter.cycle", cycleSeconds), x + 15, y + 100, CRT_COLOR, false);

        String modeName = switch (mode) {
            case EnergyIn -> Component.translatable("ic2.meter.mode.EnergyIn").getString();
            case EnergyOut -> Component.translatable("ic2.meter.mode.EnergyOut").getString();
            case EnergyGain -> Component.translatable("ic2.meter.mode.EnergyGain").getString();
            case Voltage -> Component.translatable("ic2.meter.mode.Voltage").getString();
        };
        guiGraphics.drawString(font, modeName, x + 105, y + 100, CRT_COLOR, false);
    }
}
