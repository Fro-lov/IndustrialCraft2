package com.onter.onter_ic2.client.screen;

import com.onter.onter_ic2.OnterIC2;
import com.onter.onter_ic2.block.machines.*;
import com.onter.onter_ic2.energy.EnergyPriority;
import com.onter.onter_ic2.menu.BaseMachineMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class BaseMachineScreen<T extends BaseMachineMenu> extends AbstractContainerScreen<T> {
    private final ResourceLocation texture;

    public BaseMachineScreen(T menu, Inventory playerInventory, Component title) {
        this(menu, playerInventory, title, getTextureForMachine(menu));
    }

    public BaseMachineScreen(T menu, Inventory playerInventory, Component title, ResourceLocation texture) {
        super(menu, playerInventory, title);
        this.texture = texture;
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.titleLabelX = 8;
        this.titleLabelY = 6;
    }

    private static ResourceLocation getTextureForMachine(BaseMachineMenu menu) {
        if (menu.getBlockEntity() instanceof ElectricFurnaceBlockEntity) {
            return OnterIC2.loc("textures/gui/container/electric_furnace.png");
        } else if (menu.getBlockEntity() instanceof MaceratorBlockEntity) {
            return OnterIC2.loc("textures/gui/container/macerator.png");
        } else if (menu.getBlockEntity() instanceof CompressorBlockEntity) {
            return OnterIC2.loc("textures/gui/container/compressor.png");
        } else if (menu.getBlockEntity() instanceof ExtractorBlockEntity) {
            return OnterIC2.loc("textures/gui/container/extractor.png");
        } else if (menu.getBlockEntity() instanceof MetalFormerBlockEntity) {
            return OnterIC2.loc("textures/gui/container/metal_former.png");
        }
        return OnterIC2.loc("textures/gui/container/electric_furnace.png");
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(texture, x, y, 0, 0, imageWidth, imageHeight);

        // Progress Arrow (24x17 at x=79, y=34)
        int progressWidth = menu.getScaledProgress(24);
        if (progressWidth > 0) {
            guiGraphics.blit(texture, x + 79, y + 34, 176, 14, progressWidth + 1, 17);
        }

        // Lightning Energy Bar (14x14 at x=56, y=37)
        int energyHeight = menu.getScaledEnergy(14);
        if (energyHeight > 0) {
            guiGraphics.blit(texture, x + 56, y + 37 + (14 - energyHeight), 176, 14 - energyHeight, 14, energyHeight);
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

        // Render Priority Badge
        guiGraphics.drawString(font, priority.getIcon(), x + 154, y + 6, 0xFFFFFF, false);

        if (mouseX >= x + 150 && mouseX <= x + 168 && mouseY >= y + 4 && mouseY <= y + 16) {
            guiGraphics.renderComponentTooltip(font, List.of(
                    Component.literal("§6Приоритет сети: §f" + priority.getDisplayName().getString()),
                    Component.literal("§8(Клик: переключить)")
            ), mouseX, mouseY);
        }

        if (mouseX >= x + 56 && mouseX <= x + 70 && mouseY >= y + 36 && mouseY <= y + 52) {
            int energy = menu.getEnergy();
            int max = menu.getMaxEnergy();
            guiGraphics.renderComponentTooltip(font, List.of(
                    Component.literal("§eЭнергия: §f" + energy + " / " + max + " FE"),
                    Component.literal("§7(§a" + (energy / 4) + " §7/ §a" + (max / 4) + " EU§7)")
            ), mouseX, mouseY);
        }
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

    public static class MachineScreen extends BaseMachineScreen<BaseMachineMenu> {
        public MachineScreen(BaseMachineMenu menu, Inventory playerInventory, Component title) {
            super(menu, playerInventory, title);
        }
    }
}
