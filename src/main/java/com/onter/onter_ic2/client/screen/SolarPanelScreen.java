package com.onter.onter_ic2.client.screen;

import com.onter.onter_ic2.OnterIC2;
import com.onter.onter_ic2.menu.SolarPanelMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class SolarPanelScreen extends AbstractContainerScreen<SolarPanelMenu> {
    private static final ResourceLocation TEXTURE = OnterIC2.loc("textures/gui/container/solar_panel.png");

    public SolarPanelScreen(SolarPanelMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 212;
        this.imageHeight = 210;
        this.inventoryLabelY = 117;
        this.titleLabelY = -100; // Hide automatic title because GUI texture already has built-in frame title
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        int dayGen = menu.getDayGen();
        int nightGen = menu.getNightGen();
        int genRate = menu.getGeneratingRate();
        int energy = menu.getEnergy();
        int maxEnergy = menu.getMaxEnergy();
        boolean sunVisible = menu.isSunVisible();

        int textX = x + 34;

        // Line 1: Compact Generation (y = 32)
        if (!sunVisible) {
            guiGraphics.drawString(font, "§c⚠ Небо закрыто", textX, y + 32, 0xFFFFFF, false);
        } else if (genRate > 0) {
            String genStr = (genRate == dayGen) ? "§6☀ §aВыработка: §f" + (genRate / 4) + " EU/t"
                                                : "§9☾ §aВыработка: §f" + (genRate / 4) + " EU/t";
            guiGraphics.drawString(font, genStr, textX, y + 32, 0xFFFFFF, false);
        } else {
            guiGraphics.drawString(font, "§7Выработка: 0 EU/t", textX, y + 32, 0xFFFFFF, false);
        }

        // Line 2: Specs (y = 44)
        String specsStr = "§eДень: §f" + (dayGen / 4) + " §7| §9Ночь: §f" + (nightGen / 4) + " EU/t";
        guiGraphics.drawString(font, specsStr, textX, y + 44, 0xFFFFFF, false);

        // Line 3: Compact Storage (y = 56)
        String storageStr = "§bБуфер: §f" + formatValue(energy / 4) + " / " + formatValue(maxEnergy / 4) + " EU";
        guiGraphics.drawString(font, storageStr, textX, y + 56, 0xFFFFFF, false);
    }

    private String formatValue(long value) {
        if (value >= 1_000_000_000) return String.format("%.1fB", value / 1_000_000_000.0);
        if (value >= 1_000_000) return String.format("%.2fM", value / 1_000_000.0);
        if (value >= 1_000) return String.format("%.1fk", value / 1_000.0);
        return String.valueOf(value);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // Tooltip over battery slots
        if (mouseX >= x + 71 && mouseX <= x + 143 && mouseY >= y + 92 && mouseY <= y + 110) {
            guiGraphics.renderComponentTooltip(font, List.of(
                    Component.literal("§eСлоты зарядки аккумуляторов"),
                    Component.literal("§7Поместите аккумулятор или кристалл для зарядки")
            ), mouseX, mouseY);
        }
    }
}
