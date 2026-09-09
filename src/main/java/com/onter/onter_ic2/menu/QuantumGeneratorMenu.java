package com.onter.onter_ic2.menu;

import com.onter.onter_ic2.block.generators.QuantumGeneratorBlockEntity;
import com.onter.onter_ic2.init.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class QuantumGeneratorMenu extends AbstractContainerMenu {
    private final QuantumGeneratorBlockEntity blockEntity;
    private final ContainerData data;

    public QuantumGeneratorMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv,
                (QuantumGeneratorBlockEntity) inv.player.level().getBlockEntity(extraData.readBlockPos()),
                new SimpleContainerData(4));
    }

    public QuantumGeneratorMenu(int containerId, Inventory inv, QuantumGeneratorBlockEntity entity, ContainerData data) {
        super(ModMenuTypes.QUANTUM_GENERATOR_MENU.get(), containerId);
        this.blockEntity = entity;
        this.data = data;

        addDataSlots(data);

        // Player Inventory: x = 8, y = 110
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, 110 + row * 18));
            }
        }

        // Player Hotbar: x = 8, y = 168
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(inv, col, 8 + col * 18, 168));
        }
    }

    public int getProduction() {
        return (this.data.get(1) << 16) | (this.data.get(0) & 0xFFFF);
    }

    public int getTier() {
        return this.data.get(2);
    }

    public boolean isActive() {
        return this.data.get(3) != 0;
    }

    public QuantumGeneratorBlockEntity getBlockEntity() {
        return blockEntity;
    }

    @Override
    public boolean clickMenuButton(@NotNull Player player, int id) {
        if (blockEntity != null) {
            blockEntity.handleButtonClick(id);
            return true;
        }
        return false;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return blockEntity != null && !blockEntity.isRemoved() &&
                player.distanceToSqr(blockEntity.getBlockPos().getX() + 0.5,
                        blockEntity.getBlockPos().getY() + 0.5,
                        blockEntity.getBlockPos().getZ() + 0.5) <= 64.0;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        return ItemStack.EMPTY;
    }
}
