package com.onter.onter_ic2.client.screen;

import com.onter.onter_ic2.OnterIC2;
import com.onter.onter_ic2.energy.EnergyPriority;
import com.onter.onter_ic2.inventory.UnifiedReplicatorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UnifiedReplicatorScreen extends AbstractContainerScreen<UnifiedReplicatorMenu> {
    private static final ResourceLocation TEXTURE = OnterIC2.loc("textures/gui/guireplicator.png");

    public UnifiedReplicatorScreen(UnifiedReplicatorMenu menu, Inventory playerInventory, Component title) {
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
        int relX = (int) (mouseX - x);
        int relY = (int) (mouseY - y);

        if (mouseX >= x + 150 && mouseX <= x + 168 && mouseY >= y + 4 && mouseY <= y + 16) {
            if (minecraft != null && minecraft.gameMode != null) {
                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 100);
                return true;
            }
        }

        boolean handled = handlePatternClicks(relX, relY);
        if (handled) return true;

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean handlePatternClicks(int relX, int relY) {
        int startX = 62;
        int startY = 18;
        int cols = 3;
        int rows = 3;
        int slotSize = 18;

        for (int i = 0; i < 9; i++) {
            int col = i % cols;
            int row = i / cols;
            int bx = startX + col * slotSize;
            int by = startY + row * slotSize;

            if (relX >= bx && relX < bx + slotSize && relY >= by && relY < by + slotSize) {
                if (minecraft != null && minecraft.gameMode != null) {
                    minecraft.gameMode.handleInventoryButtonClick(menu.containerId, i);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        int progressWidth = menu.getScaledProgress(20);
        if (progressWidth > 0) {
            guiGraphics.blit(TEXTURE, x + 120, y + 36, 176, 0, progressWidth, 16);
        }

        renderPatternSelectionGrid(guiGraphics, x, y);
    }

    private void renderPatternSelectionGrid(GuiGraphics guiGraphics, int x, int y) {
        int startX = x + 62;
        int startY = y + 18;
        int cols = 3;
        int slotSize = 18;

        int selected = menu.getSelectedPatternIndex();

        for (int i = 0; i < menu.getPatterns().length; i++) {
            int col = i % cols;
            int row = i / cols;
            int bx = startX + col * slotSize;
            int by = startY + row * slotSize;

            if (i == selected) {
                guiGraphics.fill(bx, by, bx + 16, by + 16, 0x8033FF33);
            }

            ItemStack icon = new ItemStack(menu.getPatterns()[i]);
            guiGraphics.renderItem(icon, bx, by);
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
    }
}
