package com.onter.onter_ic2.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.onter.onter_ic2.OnterIC2;
import com.onter.onter_ic2.block.machines.UnifiedReplicatorBlockEntity;
import com.onter.onter_ic2.inventory.UnifiedReplicatorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class UnifiedReplicatorScreen extends AbstractContainerScreen<UnifiedReplicatorMenu> {
    private static final ResourceLocation TEXTURE = OnterIC2.loc("textures/gui/guireplicator.png");

    public UnifiedReplicatorScreen(UnifiedReplicatorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // Button Previous Pattern
        this.addRenderableWidget(Button.builder(Component.literal("<"), btn -> {
            if (this.minecraft != null && this.minecraft.gameMode != null) {
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 0);
            }
        }).bounds(x + 25, y + 45, 16, 16).build());

        // Button Next Pattern
        this.addRenderableWidget(Button.builder(Component.literal(">"), btn -> {
            if (this.minecraft != null && this.minecraft.gameMode != null) {
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 1);
            }
        }).bounds(x + 75, y + 45, 16, 16).build());
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // Render replication progress bar
        int progress = menu.getProgress();
        int maxProgress = menu.getMaxProgress();
        if (maxProgress > 0 && progress > 0) {
            int progressWidth = (int) ((float) progress / maxProgress * 24);
            guiGraphics.blit(TEXTURE, x + 102, y + 22, 176, 14, progressWidth, 16);
        }
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        int patternIdx = menu.getPatternIndex();
        if (patternIdx >= 0 && patternIdx < UnifiedReplicatorBlockEntity.PATTERNS.length) {
            ItemStack patternItem = new ItemStack(UnifiedReplicatorBlockEntity.PATTERNS[patternIdx]);
            guiGraphics.renderItem(patternItem, x + 49, y + 45);
        }

        guiGraphics.drawString(font, Component.literal(String.format("EU: %,d", menu.getEnergy() / 4)), x + 105, y + 50, 0xFFFF55, false);
    }
}
