package com.onter.onter_ic2.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.onter.onter_ic2.OnterIC2;
import com.onter.onter_ic2.block.machines.MolecularTransformerBlockEntity;
import com.onter.onter_ic2.menu.MolecularTransformerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MolecularTransformerScreen extends AbstractContainerScreen<MolecularTransformerMenu> {
    private static final ResourceLocation TEXTURE = OnterIC2.loc("textures/gui/molecular_transformer.png");

    public MolecularTransformerScreen(MolecularTransformerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 220;
        this.imageHeight = 193;
        this.inventoryLabelY = -100;
        this.titleLabelY = 9;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        int recipeIndex = menu.getCurrentRecipeIndex();
        if (recipeIndex >= 0 && recipeIndex < MolecularTransformerBlockEntity.RECIPES.size()) {
            MolecularTransformerBlockEntity.MTRecipe recipe = MolecularTransformerBlockEntity.RECIPES.get(recipeIndex);
            double energyUsed = menu.getEnergyUsed();
            double totalEU = recipe.totalEU();
            if (totalEU > 0 && energyUsed > 0) {
                int gaugeHeight = (int) Math.min(15, Math.round(15.0 * energyUsed / totalEU));
                if (gaugeHeight > 0) {
                    guiGraphics.blit(TEXTURE, x + 23, y + 48, 221, 7, 10, gaugeHeight);
                }
            }
        }
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        int recipeIndex = menu.getCurrentRecipeIndex();
        String inputName = "-";
        String outputName = "-";
        String totalEUStr = "-";
        double progressPercent = 0.0;

        if (recipeIndex >= 0 && recipeIndex < MolecularTransformerBlockEntity.RECIPES.size()) {
            MolecularTransformerBlockEntity.MTRecipe recipe = MolecularTransformerBlockEntity.RECIPES.get(recipeIndex);
            inputName = new ItemStack(recipe.input()).getHoverName().getString();
            outputName = recipe.output().getHoverName().getString();
            totalEUStr = String.format("%,d EU", recipe.totalEU());
            progressPercent = Math.min(100.0, (menu.getEnergyUsed() / (double) recipe.totalEU()) * 100.0);
        } else {
            ItemStack inSlot = menu.getSlot(0).getItem();
            if (!inSlot.isEmpty()) {
                inputName = inSlot.getHoverName().getString();
            }
        }

        String lastEUStr = String.format("%,d EU/t", menu.getLastEnergyGiven());
        String progressStr = String.format("%.1f%%", progressPercent);

        int textColor = 0xFFFFFF;

        // Draw Right Aligned Labels at x=107
        drawRightAlignedText(guiGraphics, Component.translatable("advanced_solar_panels.gui.input"), x + 107, y + 26, textColor);
        drawRightAlignedText(guiGraphics, Component.translatable("advanced_solar_panels.gui.output"), x + 107, y + 38, textColor);
        drawRightAlignedText(guiGraphics, Component.translatable("advanced_solar_panels.gui.energyPerOperation"), x + 107, y + 50, textColor);
        drawRightAlignedText(guiGraphics, Component.translatable("advanced_solar_panels.gui.energyPerTick"), x + 107, y + 62, textColor);
        drawRightAlignedText(guiGraphics, Component.translatable("advanced_solar_panels.gui.progress"), x + 107, y + 74, textColor);

        // Draw Left Aligned Dynamic Values at x=112
        guiGraphics.drawString(font, inputName, x + 112, y + 26, textColor, false);
        guiGraphics.drawString(font, outputName, x + 112, y + 38, textColor, false);
        guiGraphics.drawString(font, totalEUStr, x + 112, y + 50, textColor, false);
        guiGraphics.drawString(font, lastEUStr, x + 112, y + 62, textColor, false);
        guiGraphics.drawString(font, progressStr, x + 112, y + 74, textColor, false);
    }

    private void drawRightAlignedText(GuiGraphics guiGraphics, Component comp, int x, int y, int color) {
        int textWidth = font.width(comp);
        guiGraphics.drawString(font, comp, x - textWidth, y, color, false);
    }
}
