package com.onter.onter_ic2.menu;

import com.onter.onter_ic2.block.generators.SolarPanelBlockEntity;
import com.onter.onter_ic2.init.ModMenuTypes;
import com.onter.onter_ic2.item.BatteryItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class SolarPanelMenu extends AbstractContainerMenu {
    private final SolarPanelBlockEntity blockEntity;
    private final ContainerData data;

    public SolarPanelMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv,
                (SolarPanelBlockEntity) inv.player.level().getBlockEntity(extraData.readBlockPos()),
                new SimpleContainerData(8));
    }

    public SolarPanelMenu(int containerId, Inventory inv, SolarPanelBlockEntity entity, ContainerData data) {
        super(ModMenuTypes.SOLAR_PANEL_MENU.get(), containerId);
        this.blockEntity = entity;
        this.data = data;

        addDataSlots(data);

        // 4 Charging slots: x = 17, 35, 53, 71, y = 59 (spacing 2)
        for (int i = 0; i < 4; i++) {
            this.addSlot(new SlotItemHandler(entity.getItemHandler(), i, 17 + i * 18, 59) {
                @Override
                public boolean mayPlace(@NotNull ItemStack stack) {
                    return stack.getItem() instanceof BatteryItem || stack.getCapability(net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage.ITEM, null) != null;
                }
            });
        }

        // Player Inventory: x = 17, y = 86
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(inv, col + row * 9 + 9, 17 + col * 18, 86 + row * 18));
            }
        }

        // Player Hotbar: x = 17, y = 144
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(inv, col, 17 + col * 18, 144));
        }
    }

    public int getEnergy() {
        return (this.data.get(1) << 16) | (this.data.get(0) & 0xFFFF);
    }

    public int getMaxEnergy() {
        return (this.data.get(3) << 16) | (this.data.get(2) & 0xFFFF);
    }

    public int getDayGen() {
        return this.data.get(4);
    }

    public int getNightGen() {
        return this.data.get(5);
    }

    public boolean isSunVisible() {
        return this.data.get(6) == 1;
    }

    public int getGeneratingRate() {
        return this.data.get(7);
    }

    public SolarPanelBlockEntity getBlockEntity() {
        return blockEntity;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index < 4) {
                if (!this.moveItemStackTo(itemstack1, 4, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (itemstack1.getItem() instanceof BatteryItem || itemstack1.getCapability(net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage.ITEM, null) != null) {
                    if (!this.moveItemStackTo(itemstack1, 0, 4, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    return ItemStack.EMPTY;
                }
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
        }
        return itemstack;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(this.blockEntity.getBlockPos() != null ?
                net.minecraft.world.inventory.ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()) :
                net.minecraft.world.inventory.ContainerLevelAccess.NULL, player, blockEntity.getBlockState().getBlock());
    }
}
