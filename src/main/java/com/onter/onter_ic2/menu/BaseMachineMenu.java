package com.onter.onter_ic2.menu;

import com.onter.onter_ic2.block.base.BaseMachineBlockEntity;
import com.onter.onter_ic2.init.ModMenuTypes;
import com.onter.onter_ic2.item.BatteryItem;
import com.onter.onter_ic2.item.UpgradeItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;

public class BaseMachineMenu extends AbstractContainerMenu {
    protected final BaseMachineBlockEntity blockEntity;
    protected final ContainerData data;

    public BaseMachineMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(ModMenuTypes.BASE_MACHINE_MENU.get(), containerId, inv,
                (BaseMachineBlockEntity) inv.player.level().getBlockEntity(extraData.readBlockPos()),
                new SimpleContainerData(4));
    }

    public BaseMachineMenu(int containerId, Inventory inv, BaseMachineBlockEntity entity, ContainerData data) {
        this(ModMenuTypes.BASE_MACHINE_MENU.get(), containerId, inv, entity, data);
    }

    public BaseMachineMenu(MenuType<?> menuType, int containerId, Inventory inv, BaseMachineBlockEntity entity, ContainerData data) {
        super(menuType, containerId);
        this.blockEntity = entity;
        this.data = data;

        checkContainerSize(inv, 36);
        addDataSlots(data);

        addMachineSlots(entity);

        // Player Inventory
        addPlayerInventory(inv);
        addPlayerHotbar(inv);
    }

    protected void addMachineSlots(BaseMachineBlockEntity entity) {
        // 0: Input (top left)
        this.addSlot(new SlotItemHandler(entity.getItemHandler(), BaseMachineBlockEntity.SLOT_INPUT, 56, 17));
        // 1: Output (right)
        this.addSlot(new SlotItemHandler(entity.getItemHandler(), BaseMachineBlockEntity.SLOT_OUTPUT, 116, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        // 2: Battery (bottom left)
        this.addSlot(new SlotItemHandler(entity.getItemHandler(), BaseMachineBlockEntity.SLOT_BATTERY, 56, 53));
        // 3..6: Upgrades (far right)
        for (int i = 0; i < 4; i++) {
            this.addSlot(new SlotItemHandler(entity.getItemHandler(), BaseMachineBlockEntity.SLOT_UPGRADE_1 + i, 152, 8 + i * 18));
        }
    }

    public BaseMachineBlockEntity getBlockEntity() {
        return blockEntity;
    }

    public int getProgress() {
        return this.data.get(0);
    }

    public int getMaxProgress() {
        return this.data.get(1);
    }

    public int getEnergy() {
        return this.data.get(2);
    }

    public int getMaxEnergy() {
        return this.data.get(3);
    }

    public int getScaledProgress(int pixels) {
        int p = getProgress();
        int max = getMaxProgress();
        return max != 0 && p != 0 ? p * pixels / max : 0;
    }

    public int getScaledEnergy(int pixels) {
        int e = getEnergy();
        int max = getMaxEnergy();
        return max != 0 && e != 0 ? e * pixels / max : 0;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            int machineSlotsCount = BaseMachineBlockEntity.TOTAL_SLOTS;

            if (index < machineSlotsCount) {
                if (!this.moveItemStackTo(itemstack1, machineSlotsCount, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (itemstack1.getItem() instanceof BatteryItem) {
                    if (!this.moveItemStackTo(itemstack1, BaseMachineBlockEntity.SLOT_BATTERY, BaseMachineBlockEntity.SLOT_BATTERY + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (itemstack1.getItem() instanceof UpgradeItem) {
                    if (!this.moveItemStackTo(itemstack1, BaseMachineBlockEntity.SLOT_UPGRADE_1, BaseMachineBlockEntity.SLOT_UPGRADE_4 + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (!this.moveItemStackTo(itemstack1, BaseMachineBlockEntity.SLOT_INPUT, BaseMachineBlockEntity.SLOT_INPUT + 1, false)) {
                        return ItemStack.EMPTY;
                    }
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

    protected void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    protected void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
}
