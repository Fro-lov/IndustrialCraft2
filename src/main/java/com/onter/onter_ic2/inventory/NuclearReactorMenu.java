package com.onter.onter_ic2.inventory;

import com.onter.onter_ic2.init.ModBlocks;
import com.onter.onter_ic2.init.ModMenuTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class NuclearReactorMenu extends AbstractContainerMenu {
    public static final int GRID_ROWS = 6;
    public static final int GRID_COLS = 9;
    public static final int TOTAL_SLOTS = 54;

    private final IItemHandler itemHandler;
    private final ContainerData data;

    public NuclearReactorMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new ItemStackHandler(TOTAL_SLOTS), new SimpleContainerData(6));
    }

    public NuclearReactorMenu(int containerId, Inventory playerInventory, net.minecraft.network.FriendlyByteBuf extraData) {
        this(containerId, playerInventory);
    }

    public NuclearReactorMenu(int containerId, Inventory playerInventory, IItemHandler itemHandler, ContainerData data) {
        super(ModMenuTypes.NUCLEAR_REACTOR.get(), containerId);
        this.itemHandler = itemHandler;
        this.data = data;

        // 6x9 Reactor grid slots (x: 8 + col * 18, y: 17 + row * 18)
        for (int row = 0; row < GRID_ROWS; row++) {
            for (int col = 0; col < GRID_COLS; col++) {
                int slotIndex = row * GRID_COLS + col;
                this.addSlot(new SlotItemHandler(itemHandler, slotIndex, 8 + col * 18, 17 + row * 18) {
                    @Override
                    public boolean isActive() {
                        int activeCols = Math.min(GRID_COLS, 3 + data.get(2));
                        int c = slotIndex % GRID_COLS;
                        return c < activeCols;
                    }
                });
            }
        }

        // Player Inventory
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 137 + i * 18));
            }
        }

        // Player Hotbar
        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 195));
        }

        this.addDataSlots(data);
    }

    public int getHeat() {
        return data.get(0);
    }

    public int getMaxHeat() {
        return data.get(1);
    }

    public int getChambers() {
        return data.get(2);
    }

    public int getOutputEU() {
        return data.get(3);
    }

    public int getStoredEnergy() {
        return data.get(4);
    }

    public int getMaxEnergy() {
        return data.get(5);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index < TOTAL_SLOTS) {
                // Moving from Reactor to Player Inventory
                if (!this.moveItemStackTo(itemstack1, TOTAL_SLOTS, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Moving from Player to Reactor
                int activeCols = Math.min(GRID_COLS, 3 + data.get(2));
                boolean moved = false;
                for (int row = 0; row < GRID_ROWS; row++) {
                    for (int col = 0; col < activeCols; col++) {
                        int targetSlotIndex = row * GRID_COLS + col;
                        Slot targetSlot = this.slots.get(targetSlotIndex);
                        if (targetSlot != null && !targetSlot.hasItem() && targetSlot.mayPlace(itemstack1)) {
                            targetSlot.set(itemstack1.split(1));
                            moved = true;
                            break;
                        }
                    }
                    if (moved) break;
                }

                if (!moved) {
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
