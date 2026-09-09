package com.onter.onter_ic2.client.screen;

import com.onter.onter_ic2.OnterIC2;
import com.onter.onter_ic2.menu.MetalFormerMenu;
import com.onter.onter_ic2.recipe.MetalFormerRecipe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class MetalFormerScreen extends BaseMachineScreen<MetalFormerMenu> {
    private static final ResourceLocation TEXTURE = OnterIC2.loc("textures/gui/container/metal_former.png");
    private static final ResourceLocation ICON_EXTRUDING = OnterIC2.loc("textures/gui/mode_icons/extruding.png");
    private static final ResourceLocation ICON_ROLLING = OnterIC2.loc("textures/gui/mode_icons/rolling.png");
    private static final ResourceLocation ICON_CUTTING = OnterIC2.loc("textures/gui/mode_icons/cutting.png");

    private static final ResourceLocation SPRITE_BUTTON = ResourceLocation.withDefaultNamespace("widget/button");
    private static final ResourceLocation SPRITE_BUTTON_HIGHLIGHTED = ResourceLocation.withDefaultNamespace("widget/button_highlighted");

    public MetalFormerScreen(MetalFormerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, TEXTURE);
        this.titleLabelX = 8;
        this.titleLabelY = 6;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // Render main background
        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // Lightning Energy Bar (14x14 at x=17, y=37)
        int energyHeight = menu.getScaledEnergy(14);
        if (energyHeight > 0) {
            guiGraphics.blit(TEXTURE, x + 17, y + 37 + (14 - energyHeight), 176, 14 - energyHeight, 14, energyHeight);
        }

        // 3-Capsules Progress Bar (width 51, height 13 at x=50, y=37)
        // Orange capsules from u=176, v=14 overlay the gray capsules as progress increases
        int progress = menu.getScaledProgress(51);
        if (progress > 0) {
            guiGraphics.blit(TEXTURE, x + 50, y + 37, 176, 14, progress, 13);
        }

        // Mode Button (20x20 at x=65, y=53 under middle capsule, matching original IC2 VanillaButton)
        int bx = x + 65;
        int by = y + 53;
        int bw = 20;
        int bh = 20;
        boolean isHovered = mouseX >= bx && mouseX < bx + bw && mouseY >= by && mouseY < by + bh;

        // Render authentic 9-slice vanilla button widget
        guiGraphics.blitSprite(isHovered ? SPRITE_BUTTON_HIGHLIGHTED : SPRITE_BUTTON, bx, by, bw, bh);

        // Mode Button Icon (16x16 centered inside 20x20 button)
        MetalFormerRecipe.Mode mode = menu.getMode();
        ResourceLocation modeIcon = switch (mode) {
            case EXTRUDING -> ICON_EXTRUDING;
            case ROLLING -> ICON_ROLLING;
            case CUTTING -> ICON_CUTTING;
        };
        guiGraphics.blit(modeIcon, bx + 2, by + 2, 0, 0, 16, 16, 16, 16);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && minecraft != null && minecraft.gameMode != null) {
            int x = (width - imageWidth) / 2;
            int y = (height - imageHeight) / 2;

            // Click on Mode Button below progress bar (65..85, 53..73)
            if (mouseX >= x + 65 && mouseX <= x + 85 && mouseY >= y + 53 && mouseY <= y + 73) {
                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 3);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // Energy Tooltip over Lightning bolt (17..31, 37..51)
        if (mouseX >= x + 17 && mouseX <= x + 31 && mouseY >= y + 37 && mouseY <= y + 51) {
            int energy = menu.getEnergy();
            int maxEnergy = menu.getMaxEnergy();
            guiGraphics.renderComponentTooltip(font, List.of(
                    Component.literal("§eЭнергия: §f" + (energy / 4) + " / " + (maxEnergy / 4) + " EU"),
                    Component.literal("§7(" + energy + " / " + maxEnergy + " FE)")
            ), mouseX, mouseY);
        }

        // Tooltip over Mode Button (65..85, 53..73)
        if (mouseX >= x + 65 && mouseX <= x + 85 && mouseY >= y + 53 && mouseY <= y + 73) {
            MetalFormerRecipe.Mode mode = menu.getMode();
            String modeName = switch (mode) {
                case EXTRUDING -> "Выдавливание (Провода)";
                case ROLLING -> "Прокатка (Пластины)";
                case CUTTING -> "Резка (Кусачки / Оболочки)";
            };
            guiGraphics.renderComponentTooltip(font, List.of(
                    Component.literal("§eРежим: §a" + modeName),
                    Component.literal("§7[Нажмите для смены]")
            ), mouseX, mouseY);
        }
    }
}

