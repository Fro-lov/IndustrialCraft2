package com.onter.onter_ic2.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.onter.onter_ic2.OnterIC2;
import com.onter.onter_ic2.menu.MeterMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class MeterScreen extends AbstractContainerScreen<MeterMenu> {
    private static final ResourceLocation TEXTURE = OnterIC2.loc("textures/gui/guitooleumeter.png");
    private static final int CRT_GREEN = 0x33FF33; // Classic IC2 bright CRT green

    public MeterScreen(MeterMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 217;
        this.inventoryLabelY = -100;
        this.titleLabelY = 7;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int xMin = (this.width - this.imageWidth) / 2;
        int yMin = (this.height - this.imageHeight) / 2;
        int relX = (int) (mouseX - xMin);
        int relY = (int) (mouseY - yMin);

        // Click quadrants on the round mode dial (x: 112..152, y: 55..95)
        if (relX >= 112 && relX <= 152 && relY >= 55 && relY <= 95) {
            if (relX < 132 && relY < 75) {
                sendButton(0); // Top-Left: Energy In
                return true;
            } else if (relX >= 132 && relY < 75) {
                sendButton(1); // Top-Right: Energy Out
                return true;
            } else if (relX < 132 && relY >= 75) {
                sendButton(2); // Bottom-Left: Energy Gain
                return true;
            } else {
                sendButton(3); // Bottom-Right: Voltage
                return true;
            }
        }

        // Click Reset button area (x: 26..83, y: 111..123)
        if (relX >= 26 && relX <= 83 && relY >= 111 && relY <= 123) {
            sendButton(4); // Reset
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
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

        // Render circular dial overlay with pointing needle from UV (176, 0..120) at x=112, y=55
        MeterMenu.Mode mode = menu.getMode();
        int v = switch (mode) {
            case EnergyIn -> 0;    // needle pointing top-left (\)
            case EnergyOut -> 40;   // needle pointing top-right (/)
            case Voltage -> 80;     // needle pointing bottom-right (\ down)
            case EnergyGain -> 120; // needle pointing bottom-left (/ down)
        };
        guiGraphics.blit(TEXTURE, x + 112, y + 55, 176, v, 40, 40);
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
        int limit = menu.getMaxVoltageLimit();
        int tier = menu.getTier();

        // Mode label in header
        guiGraphics.drawString(font, Component.translatable("ic2.meter.mode"), x + 114, y + 28, CRT_GREEN, false);

        // Active mode description inside top-right black rectangle (x: 94..158, y: 38..49)
        String modeName = switch (mode) {
            case EnergyIn -> Component.translatable("ic2.meter.mode.EnergyIn").getString();
            case EnergyOut -> Component.translatable("ic2.meter.mode.EnergyOut").getString();
            case EnergyGain -> Component.translatable("ic2.meter.mode.EnergyGain").getString();
            case Voltage -> Component.translatable("ic2.meter.mode.Voltage").getString();
        };
        int modeWidth = font.width(modeName);
        guiGraphics.drawString(font, modeName, x + 96 + Math.max(0, (62 - modeWidth) / 2), y + 40, CRT_GREEN, false);

        // Left CRT Display:
        // Average
        guiGraphics.drawString(font, Component.translatable("ic2.meter.avg"), x + 15, y + 27, CRT_GREEN, false);
        guiGraphics.drawString(font, String.format("%,d%s", avg, unit), x + 15, y + 36, CRT_GREEN, false);

        // Max / Min
        guiGraphics.drawString(font, Component.translatable("ic2.meter.max_min"), x + 15, y + 48, CRT_GREEN, false);
        guiGraphics.drawString(font, String.format("%,d%s", max, unit), x + 15, y + 57, CRT_GREEN, false);
        guiGraphics.drawString(font, String.format("%,d%s", min, unit), x + 15, y + 66, CRT_GREEN, false);

        // Cycle time
        guiGraphics.drawString(font, Component.translatable("ic2.meter.cycle", cycleSeconds), x + 15, y + 77, CRT_GREEN, false);

        // Limit / Tier info line
        guiGraphics.drawString(font, String.format("Лимит: %,d EU/t (T%d)", limit, tier), x + 15, y + 88, 0x55FFFF, false);

        // Reset text
        Component resetComp = Component.translatable("ic2.meter.mode.reset");
        int resetWidth = font.width(resetComp);
        guiGraphics.drawString(font, resetComp, x + 26 + (58 - resetWidth) / 2, y + 114, CRT_GREEN, false);
    }
}
