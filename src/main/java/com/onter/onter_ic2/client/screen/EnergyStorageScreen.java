package com.onter.onter_ic2.client.screen;

import com.onter.onter_ic2.OnterIC2;
import com.onter.onter_ic2.energy.EnergyPriority;
import com.onter.onter_ic2.menu.EnergyStorageMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class EnergyStorageScreen extends AbstractContainerScreen<EnergyStorageMenu> {
    private final ResourceLocation texture;

    public EnergyStorageScreen(EnergyStorageMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.texture = OnterIC2.loc("textures/gui/container/energy_storage.png");
        this.imageWidth = 176;
        this.imageHeight = 196;
        this.titleLabelX = 8;
        this.titleLabelY = 6;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        if (mouseX >= x + 150 && mouseX <= x + 168 && mouseY >= y + 4 && mouseY <= y + 16) {
            if (minecraft != null && minecraft.gameMode != null) {
                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 100);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(texture, x, y, 0, 0, imageWidth, imageHeight);

        int energyWidth = menu.getScaledEnergy(24);
        if (energyWidth > 0) {
            guiGraphics.blit(texture, x + 79, y + 38, 176, 0, energyWidth + 1, 14);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        EnergyPriority priority = menu.getPriority();

        guiGraphics.drawString(font, priority.getIcon(), x + 154, y + 6, 0xFFFFFF, false);

        if (mouseX >= x + 150 && mouseX <= x + 168 && mouseY >= y + 4 && mouseY <= y + 16) {
            guiGraphics.renderComponentTooltip(font, List.of(
                    Component.literal("§6Приоритет сети: §f" + priority.getDisplayName().getString()),
                    Component.literal("§8(Клик: переключить)")
            ), mouseX, mouseY);
        }

        if (mouseX >= x + 79 && mouseX <= x + 103 && mouseY >= y + 38 && mouseY <= y + 52) {
            int energy = menu.getEnergy();
            int max = menu.getMaxEnergy();
            guiGraphics.renderComponentTooltip(font, List.of(
                    Component.literal("§eЗаряд: §f" + String.format("%,d", energy / 4) + " / " + String.format("%,d", max / 4) + " EU"),
                    Component.literal("§7(" + String.format("%,d", energy) + " / " + String.format("%,d", max) + " FE)")
            ), mouseX, mouseY);
        }
    }
}
