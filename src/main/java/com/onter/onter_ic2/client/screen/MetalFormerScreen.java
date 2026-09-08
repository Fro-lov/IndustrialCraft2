package com.onter.onter_ic2.client.screen;

import com.onter.onter_ic2.OnterIC2;
import com.onter.onter_ic2.menu.MetalFormerMenu;
import com.onter.onter_ic2.recipe.MetalFormerRecipe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class MetalFormerScreen extends BaseMachineScreen<MetalFormerMenu> {
    private static final ResourceLocation TEXTURE = OnterIC2.loc("textures/gui/container/metal_former.png");
    private Button modeButton;

    public MetalFormerScreen(MetalFormerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, TEXTURE);
        this.titleLabelX = 8;
        this.titleLabelY = 6;
    }

    @Override
    protected void init() {
        super.init();
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // Mode switch button placed at x=50, y=16 (above the 3 mode icons, perfectly clear of input slot at x=17)
        modeButton = addRenderableWidget(Button.builder(Component.literal("Mode"), btn -> {
            if (minecraft != null && minecraft.gameMode != null) {
                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 0);
            }
        }).bounds(x + 54, y + 16, 54, 16).build());
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

        // Active Mode Highlight Icon & Progress
        MetalFormerRecipe.Mode mode = menu.getMode();
        int modeU = switch (mode) {
            case ROLLING -> 176;
            case EXTRUDING -> 192;
            case CUTTING -> 208;
        };
        int modeX = switch (mode) {
            case ROLLING -> 54;
            case EXTRUDING -> 72;
            case CUTTING -> 90;
        };

        // Draw active mode icon
        guiGraphics.blit(TEXTURE, x + modeX, y + 39, modeU, 14, 16, 15);

        // Progress bar inside active mode
        int progress = menu.getScaledProgress(16);
        if (progress > 0) {
            guiGraphics.blit(TEXTURE, x + modeX, y + 39, modeU, 14, progress, 15);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // Click directly on mode icons area to switch mode
        if (mouseX >= x + 50 && mouseX <= x + 110 && mouseY >= y + 36 && mouseY <= y + 56) {
            if (minecraft != null && minecraft.gameMode != null) {
                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 0);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (modeButton != null) {
            MetalFormerRecipe.Mode mode = menu.getMode();
            String modeRu = switch (mode) {
                case ROLLING -> "Прокат";
                case EXTRUDING -> "Выдавл.";
                case CUTTING -> "Резка";
            };
            modeButton.setMessage(Component.literal(modeRu));
        }
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // Tooltip over mode area
        if (mouseX >= x + 50 && mouseX <= x + 110 && mouseY >= y + 36 && mouseY <= y + 56) {
            MetalFormerRecipe.Mode mode = menu.getMode();
            String desc = switch (mode) {
                case ROLLING -> "§eПрокат: §fСлитки -> Пластины";
                case EXTRUDING -> "§eВыдавливание: §fСлитки -> Провода";
                case CUTTING -> "§eРезка: §fПластины -> Оболочки / Провода";
            };
            guiGraphics.renderComponentTooltip(font, List.of(
                    Component.literal("§6Режим: " + mode.name()),
                    Component.literal(desc),
                    Component.literal("§7[Нажмите для переключения]")
            ), mouseX, mouseY);
        }
    }
}
