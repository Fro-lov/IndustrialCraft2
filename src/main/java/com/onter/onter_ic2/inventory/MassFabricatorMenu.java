package com.onter.onter_ic2.inventory;

import com.onter.onter_ic2.init.ModMenuTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class MassFabricatorMenu extends AbstractContainerMenu {
    private final IItemHandler itemHandler;
    private final ContainerData data;

    public MassFabricatorMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new ItemStackHandler(2), new SimpleContainerData(5));
    }

    public MassFabricatorMenu(int containerId, Inventory playerInventory, net.minecraft.network.FriendlyByteBuf extraData) {
        this(containerId, playerInventory);
    }

    public MassFabricatorMenu(int containerId, Inventory playerInventory, IItemHandler itemHandler, ContainerData data) {
        super(ModMenuTypes.MASS_FABRICATOR.get(), containerId);
        this.itemHandler = itemHandler;
        this.data = data;

        // Slot 0: Scrap / Amplifier input (x: 116, y: 17)
        this.addSlot(new SlotItemHandler(itemHandler, 0, 116, 17));

        // Slot 1: UU-Matter output (x: 116, y: 53)
        this.addSlot(new SlotItemHandler(itemHandler, 1, 116, 53) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return false;
            }
        });

        // Player Inventory
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        // Player Hotbar
        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
        }

        this.addDataSlots(data);
    }

    public int getProgress() {
        return data.get(0);
    }

    public int getMaxProgress() {
        return data.get(1);
    }

    public int getAmplifier() {
        return data.get(2);
    }

    public int getEnergy() {
        return data.get(3);
    }

    public int getMaxEnergy() {
        return data.get(4);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index < 2) {
                if (!this.moveItemStackTo(itemstack1, 2, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
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
        return true;
    }
}
