package com.onter.onter_ic2.client.screen;

import com.onter.onter_ic2.OnterIC2;
import com.onter.onter_ic2.menu.EnergyStorageMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class EnergyStorageScreen extends AbstractContainerScreen<EnergyStorageMenu> {
    private static final ResourceLocation TEXTURE = OnterIC2.loc("textures/gui/container/storage.png");

    public EnergyStorageScreen(EnergyStorageMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 196;
        this.inventoryLabelY = 104;
        this.titleLabelY = 6;
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Authentically render title, Power Level, EU values, and Out transfer rate
        guiGraphics.drawString(this.font, this.title, 79, 8, 0x404040, false);
        guiGraphics.drawString(this.font, "Power Level:", 79, 22, 0x404040, false);

        int energyEU = menu.getEnergy() / 4;
        int maxEU = menu.getMaxEnergy() / 4;
        guiGraphics.drawString(this.font, String.valueOf(energyEU), 108, 33, 0x404040, false);
        guiGraphics.drawString(this.font, "/" + maxEU, 104, 44, 0x404040, false);

        int outEU = menu.getBlockEntity() != null ? menu.getBlockEntity().getMaxTransfer() / 4 : 32;
        guiGraphics.drawString(this.font, "Out: " + outEU + ".0 EU/t", 79, 58, 0x404040, false);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // Energy Bar (24px wide, 17px high, sprite at u=176, v=14)
        int energyWidth = menu.getScaledEnergy(24);
        if (energyWidth > 0) {
            guiGraphics.blit(TEXTURE, x + 79, y + 35, 176, 14, energyWidth, 17);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        if (mouseX >= x + 79 && mouseX <= x + 103 && mouseY >= y + 35 && mouseY <= y + 52) {
            int energy = menu.getEnergy();
            int max = menu.getMaxEnergy();
            guiGraphics.renderComponentTooltip(font, List.of(
                    Component.literal("§eЭнергия: §f" + (energy / 4) + " / " + (max / 4) + " EU"),
                    Component.literal("§7(" + energy + " / " + max + " FE)")
            ), mouseX, mouseY);
        }
    }
}
