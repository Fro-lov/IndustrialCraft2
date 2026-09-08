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
                    Component.literal("§eЭнергия: §f" + energy + " / " + max + " FE"),
                    Component.literal("§7(§a" + (energy / 4) + " §7/ §a" + (max / 4) + " EU§7)")
            ), mouseX, mouseY);
        }
    }
}
