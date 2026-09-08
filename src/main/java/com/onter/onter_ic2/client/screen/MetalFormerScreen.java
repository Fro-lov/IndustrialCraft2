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

    public MetalFormerScreen(MetalFormerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, TEXTURE);
        this.titleLabelX = 8;
        this.titleLabelY = 6;
    }

    @Override
    protected void init() {
        super.init();
        // No text buttons - authentic IC2 interactive GUI layout
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // Lightning Energy Bar (14x14 at x=17, y=37)
        int energyHeight = menu.getScaledEnergy(14);
        if (energyHeight > 0) {
            guiGraphics.blit(TEXTURE, x + 17, y + 37 + (14 - energyHeight), 176, 14 - energyHeight, 14, energyHeight);
        }

        // Active Mode Capsule Highlight (17x13)
        MetalFormerRecipe.Mode mode = menu.getMode();
        int modeU = switch (mode) {
            case EXTRUDING -> 176;
            case ROLLING -> 193;
            case CUTTING -> 210;
        };
        int modeX = switch (mode) {
            case EXTRUDING -> 54;
            case ROLLING -> 72;
            case CUTTING -> 90;
        };

        // Draw active mode icon
        guiGraphics.blit(TEXTURE, x + modeX, y + 39, modeU, 14, 17, 13);

        // Progress Bar inside the box at (x=67, y=53)
        int progress = menu.getScaledProgress(16);
        if (progress > 0) {
            // Authentic orange progress line filling horizontally inside the box
            guiGraphics.fill(x + 69, y + 62, x + 69 + progress, y + 64, 0xFFE58200);
            guiGraphics.fill(x + 69, y + 64, x + 69 + progress, y + 65, 0xFFA05500);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && minecraft != null && minecraft.gameMode != null) {
            int x = (width - imageWidth) / 2;
            int y = (height - imageHeight) / 2;

            // Click on Extruding capsule (54..70, 39..51)
            if (mouseX >= x + 54 && mouseX <= x + 70 && mouseY >= y + 39 && mouseY <= y + 51) {
                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 0);
                return true;
            }
            // Click on Rolling capsule (72..88, 39..51)
            if (mouseX >= x + 72 && mouseX <= x + 88 && mouseY >= y + 39 && mouseY <= y + 51) {
                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 1);
                return true;
            }
            // Click on Cutting capsule (90..106, 39..51)
            if (mouseX >= x + 90 && mouseX <= x + 106 && mouseY >= y + 39 && mouseY <= y + 51) {
                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 2);
                return true;
            }
            // Click on Mode Button / Progress Box (67..86, 53..72)
            if (mouseX >= x + 67 && mouseX <= x + 86 && mouseY >= y + 53 && mouseY <= y + 72) {
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

        // Mode Tooltip over Mode Capsules (54..107, 39..52)
        if (mouseX >= x + 54 && mouseX <= x + 107 && mouseY >= y + 39 && mouseY <= y + 52) {
            MetalFormerRecipe.Mode mode = menu.getMode();
            String activeName = switch (mode) {
                case EXTRUDING -> "§6Выдавливание (Провода)";
                case ROLLING -> "§6Прокатка (Пластины)";
                case CUTTING -> "§6Резка (Кусачки / Оболочки)";
            };
            guiGraphics.renderComponentTooltip(font, List.of(
                    Component.literal("§eТекущий режим: " + activeName),
                    Component.literal("§7- §fЛевая иконка: §eВыдавливание (Слитки -> Провода)"),
                    Component.literal("§7- §fЦентр: §eПрокатка (Слитки -> Пластины)"),
                    Component.literal("§7- §fПравая: §eРезка (Пластины -> Оболочки)"),
                    Component.literal("§a[Нажмите на иконку для выбора]")
            ), mouseX, mouseY);
        }

        // Tooltip over Progress Box (67..86, 53..72)
        if (mouseX >= x + 67 && mouseX <= x + 86 && mouseY >= y + 53 && mouseY <= y + 72) {
            guiGraphics.renderComponentTooltip(font, List.of(
                    Component.literal("§eПереключатель режима"),
                    Component.literal("§7[Нажмите для смены]")
            ), mouseX, mouseY);
        }
    }
}

