package com.onter.onter_ic2.inventory;

import com.onter.onter_ic2.init.ModMenuTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import com.onter.onter_ic2.energy.EnergyPriority;
import com.onter.onter_ic2.block.machines.UnifiedReplicatorBlockEntity;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class UnifiedReplicatorMenu extends AbstractContainerMenu {
    private final IItemHandler itemHandler;
    private final ContainerData data;

    public UnifiedReplicatorMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new ItemStackHandler(3), new SimpleContainerData(7));
    }

    public UnifiedReplicatorMenu(int containerId, Inventory playerInventory, net.minecraft.network.FriendlyByteBuf extraData) {
        this(containerId, playerInventory);
    }

    public UnifiedReplicatorMenu(int containerId, Inventory playerInventory, IItemHandler itemHandler, ContainerData data) {
        super(ModMenuTypes.UNIFIED_REPLICATOR.get(), containerId);
        this.itemHandler = itemHandler;
        this.data = data;

        // Slot 0: Scanner / Pattern slot (x: 30, y: 20)
        this.addSlot(new SlotItemHandler(itemHandler, 0, 30, 20));

        // Slot 1: UU-Matter Input slot (x: 80, y: 20)
        this.addSlot(new SlotItemHandler(itemHandler, 1, 80, 20));

        // Slot 2: Output Replicated Item (x: 134, y: 20)
        this.addSlot(new SlotItemHandler(itemHandler, 2, 134, 20) {
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

    public int getScaledProgress(int width) {
        int max = getMaxProgress();
        return max > 0 ? getProgress() * width / max : 0;
    }

    public int getSelectedPatternIndex() {
        return getPatternIndex();
    }

    public net.minecraft.world.item.Item[] getPatterns() {
        return UnifiedReplicatorBlockEntity.PATTERNS;
    }

    public int getProgress() {
        return data.get(0);
    }

    public int getMaxProgress() {
        return data.get(1);
    }

    public int getScanProgress() {
        return data.get(2);
    }

    public int getPatternIndex() {
        return data.get(3);
    }

    public EnergyPriority getPriority() {
        return EnergyPriority.fromLevel(data.get(6));
    }

    public int getEnergy() {
        return data.get(4);
    }

    public int getMaxEnergy() {
        return data.get(5);
    }

    @Override
    public boolean clickMenuButton(@NotNull Player player, int id) {
                if (id == 100) {
            int current = data.get(6);
            data.set(6, EnergyPriority.fromLevel(current).next().getLevel());
            return true;
        }
        if (id == 0) {
            // Previous pattern
            int current = getPatternIndex();
            int next = (current - 1 + com.onter.onter_ic2.block.machines.UnifiedReplicatorBlockEntity.PATTERNS.length) % com.onter.onter_ic2.block.machines.UnifiedReplicatorBlockEntity.PATTERNS.length;
            this.setData(3, next);
            return true;
        } else if (id == 1) {
            // Next pattern
            int current = getPatternIndex();
            int next = (current + 1) % com.onter.onter_ic2.block.machines.UnifiedReplicatorBlockEntity.PATTERNS.length;
            this.setData(3, next);
            return true;
        }
        return super.clickMenuButton(player, id);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index < 3) {
                if (!this.moveItemStackTo(itemstack1, 3, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(itemstack1, 0, 2, false)) {
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
