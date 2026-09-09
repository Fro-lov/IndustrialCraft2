package com.onter.onter_ic2.client.screen;

import com.onter.onter_ic2.OnterIC2;
import com.onter.onter_ic2.energy.EnergyPriority;
import com.onter.onter_ic2.menu.MolecularTransformerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MolecularTransformerScreen extends AbstractContainerScreen<MolecularTransformerMenu> {
    private static final ResourceLocation TEXTURE = OnterIC2.loc("textures/gui/molecular_transformer.png");

    public MolecularTransformerScreen(MolecularTransformerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
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
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        int progressWidth = menu.getProgressPercent() * 24 / 100;
        if (progressWidth > 0) {
            guiGraphics.blit(TEXTURE, x + 79, y + 34, 176, 0, progressWidth + 1, 16);
        }
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
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

        guiGraphics.drawString(font, Component.literal("Прогресс: " + menu.getProgressPercent() + "%"), x + 8, y + 20, 0x404040, false);
        guiGraphics.drawString(font, Component.literal("Потребление: " + String.format("%,d", menu.getLastEnergyGiven() / 4) + " EU/t"), x + 8, y + 32, 0x404040, false);
    }
}
