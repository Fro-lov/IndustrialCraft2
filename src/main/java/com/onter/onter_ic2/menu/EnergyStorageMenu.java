package com.onter.onter_ic2.menu;

import com.onter.onter_ic2.block.storage.EnergyStorageBlockEntity;
import com.onter.onter_ic2.init.ModMenuTypes;
import com.onter.onter_ic2.item.BatteryItem;
import com.onter.onter_ic2.item.UpgradeItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import com.onter.onter_ic2.energy.EnergyPriority;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;

public class EnergyStorageMenu extends AbstractContainerMenu {
    private final EnergyStorageBlockEntity blockEntity;
    private final ContainerData data;

    public EnergyStorageMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv,
                (EnergyStorageBlockEntity) inv.player.level().getBlockEntity(extraData.readBlockPos()),
                new SimpleContainerData(5));
    }

    public EnergyStorageMenu(int containerId, Inventory inv, EnergyStorageBlockEntity entity, ContainerData data) {
        super(ModMenuTypes.ENERGY_STORAGE_MENU.get(), containerId);
        this.blockEntity = entity;
        this.data = data;

        addDataSlots(data);

        // 0: Charge slot (top)
        this.addSlot(new SlotItemHandler(entity.getItemHandler(), EnergyStorageBlockEntity.SLOT_CHARGE, 56, 17) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof BatteryItem || stack.getCapability(net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage.ITEM, null) != null;
            }
        });
        // 1: Discharge slot (bottom)
        this.addSlot(new SlotItemHandler(entity.getItemHandler(), EnergyStorageBlockEntity.SLOT_DISCHARGE, 56, 53) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof BatteryItem || stack.getCapability(net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage.ITEM, null) != null;
            }
        });
        // 2..5: 4 Upgrade slots
        for (int i = 0; i < 4; i++) {
            this.addSlot(new SlotItemHandler(entity.getItemHandler(), EnergyStorageBlockEntity.SLOT_UPGRADE_1 + i, 8 + i * 18, 84));
        }

        // Player Inventory (3 rows at y=114)
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, 114 + row * 18));
            }
        }

        // Player Hotbar (1 row at y=172)
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(inv, col, 8 + col * 18, 172));
        }
    }

    public EnergyStorageBlockEntity getBlockEntity() {
        return blockEntity;
    }

    public EnergyPriority getPriority() {
        return EnergyPriority.fromLevel(data.get(4));
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == 100 && blockEntity != null) {
            blockEntity.setEnergyPriority(blockEntity.getEnergyPriority().next());
            return true;
        }
        return super.clickMenuButton(player, id);
    }

    public int getEnergy() {
        return (this.data.get(1) << 16) | (this.data.get(0) & 0xFFFF);
    }

    public int getMaxEnergy() {
        return (this.data.get(3) << 16) | (this.data.get(2) & 0xFFFF);
    }

    public int getScaledEnergy(int pixels) {
        int e = getEnergy();
        int max = getMaxEnergy();
        return max != 0 && e != 0 ? (int) (((long) e * pixels) / max) : 0;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            int totalStorageSlots = EnergyStorageBlockEntity.TOTAL_SLOTS;

            if (index < totalStorageSlots) {
                if (!this.moveItemStackTo(itemstack1, totalStorageSlots, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (itemstack1.getItem() instanceof BatteryItem || itemstack1.getCapability(net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage.ITEM, null) != null) {
                    if (!this.moveItemStackTo(itemstack1, 0, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (itemstack1.getItem() instanceof UpgradeItem) {
                    if (!this.moveItemStackTo(itemstack1, 2, 6, false)) {
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
        }
        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.blockEntity.getBlockPos() != null ?
                net.minecraft.world.inventory.ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()) :
                net.minecraft.world.inventory.ContainerLevelAccess.NULL, player, blockEntity.getBlockState().getBlock());
    }
}
