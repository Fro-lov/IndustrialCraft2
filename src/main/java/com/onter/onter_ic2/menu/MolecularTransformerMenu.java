package com.onter.onter_ic2.menu;

import com.onter.onter_ic2.block.machines.MolecularTransformerBlockEntity;
import com.onter.onter_ic2.init.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import com.onter.onter_ic2.energy.EnergyPriority;
import com.onter.onter_ic2.block.machines.MolecularTransformerBlockEntity;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class MolecularTransformerMenu extends AbstractContainerMenu {
    private final MolecularTransformerBlockEntity blockEntity;
    private final ContainerData data;

    public MolecularTransformerMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv,
                (MolecularTransformerBlockEntity) inv.player.level().getBlockEntity(extraData.readBlockPos()),
                new SimpleContainerData(8));
    }

    public MolecularTransformerMenu(int containerId, Inventory inv, MolecularTransformerBlockEntity entity, ContainerData data) {
        super(ModMenuTypes.MOLECULAR_TRANSFORMER_MENU.get(), containerId);
        this.blockEntity = entity;
        this.data = data;

        addDataSlots(data);

        // Input Slot: x = 20, y = 27
        this.addSlot(new SlotItemHandler(entity.getItemHandler(), 0, 20, 27));

        // Output Slot: x = 20, y = 68
        this.addSlot(new SlotItemHandler(entity.getItemHandler(), 1, 20, 68) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return false;
            }
        });

        // Player Inventory: x = 18, y = 98 (spacing 5 -> each row is 18 px)
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(inv, col + row * 9 + 9, 18 + col * 18, 98 + row * 18));
            }
        }

        // Player Hotbar: x = 18, y = 165 (hotbaroffset = 67)
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(inv, col, 18 + col * 18, 165));
        }
    }

    public int getProgressPercent() {
        int recipeIdx = getCurrentRecipeIndex();
        if (recipeIdx >= 0 && recipeIdx < MolecularTransformerBlockEntity.RECIPES.size()) {
            double totalEU = MolecularTransformerBlockEntity.RECIPES.get(recipeIdx).totalEU();
            if (totalEU > 0) {
                return (int) Math.min(100, (getEnergyUsed() * 100.0) / totalEU);
            }
        }
        return 0;
    }

    public double getEnergyUsed() {
        return (long) this.data.get(0) + (long) this.data.get(1) * 10000L + (long) this.data.get(2) * 100_000_000L;
    }

    public int getLastEnergyGiven() {
        return this.data.get(3);
    }

    public int getCurrentRecipeIndex() {
        return this.data.get(4);
    }

    public int getStoredEnergy() {
        return this.data.get(5);
    }

    public int getMaxEnergy() {
        return this.data.get(6);
    }

    public EnergyPriority getPriority() {
        return EnergyPriority.fromLevel(this.data.get(7));
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == 100) {
            int current = data.get(7);
            data.set(7, EnergyPriority.fromLevel(current).next().getLevel());
            return true;
        }
        return super.clickMenuButton(player, id);
    }

    public MolecularTransformerBlockEntity getBlockEntity() {
        return blockEntity;
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
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack current = slot.getItem();
            itemstack = current.copy();

            if (index == 1) { // Output slot
                if (!this.moveItemStackTo(current, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(current, itemstack);
            } else if (index == 0) { // Input slot
                if (!this.moveItemStackTo(current, 2, 38, false)) {
                    return ItemStack.EMPTY;
                }
            } else { // Player inventory
                if (!this.moveItemStackTo(current, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (current.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (current.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, current);
        }
        return itemstack;
    }
}
