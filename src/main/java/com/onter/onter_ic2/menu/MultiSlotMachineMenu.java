package com.onter.onter_ic2.menu;

import com.onter.onter_ic2.block.machines.MultiSlotMachineBlockEntity;
import com.onter.onter_ic2.init.ModMenuTypes;
import com.onter.onter_ic2.recipe.MetalFormerRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class MultiSlotMachineMenu extends AbstractContainerMenu {
    protected final MultiSlotMachineBlockEntity blockEntity;
    protected final ContainerData data;
    protected final int numChannels;

    public MultiSlotMachineMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv,
                (MultiSlotMachineBlockEntity) inv.player.level().getBlockEntity(extraData.readBlockPos()),
                new SimpleContainerData(5));
    }

    public MultiSlotMachineMenu(int containerId, Inventory inv, MultiSlotMachineBlockEntity entity, ContainerData data) {
        super(ModMenuTypes.MULTI_SLOT_MACHINE_MENU.get(), containerId);
        this.blockEntity = entity;
        this.data = data;
        this.numChannels = entity != null ? entity.getNumChannels() : 6;

        checkContainerDataCount(data, 5);
        addDataSlots(data);

        if (entity != null) {
            ItemStackHandler handler = entity.getItemHandler();

            // Machine Inputs and Outputs
            if (numChannels == 6) {
                // X6: 2 rows of 3 columns
                // Inputs: x=26,44,62; y=46,64
                for (int r = 0; r < 2; r++) {
                    for (int c = 0; c < 3; c++) {
                        int slotIndex = r * 3 + c;
                        this.addSlot(new SlotItemHandler(handler, slotIndex, 26 + c * 18, 46 + r * 18) {
                            @Override
                            public boolean mayPlace(ItemStack stack) {
                                return entity.isValidInput(stack);
                            }
                        });
                    }
                }
                // Outputs: x=106,124,142; y=46,64
                for (int r = 0; r < 2; r++) {
                    for (int c = 0; c < 3; c++) {
                        int slotIndex = 6 + r * 3 + c;
                        this.addSlot(new SlotItemHandler(handler, slotIndex, 106 + c * 18, 46 + r * 18) {
                            @Override
                            public boolean mayPlace(ItemStack stack) {
                                return false;
                            }
                        });
                    }
                }
            } else {
                // X12: 4 rows of 3 columns
                // Inputs: x=26,44,62; y=28,46,64,82
                for (int r = 0; r < 4; r++) {
                    for (int c = 0; c < 3; c++) {
                        int slotIndex = r * 3 + c;
                        this.addSlot(new SlotItemHandler(handler, slotIndex, 26 + c * 18, 28 + r * 18) {
                            @Override
                            public boolean mayPlace(ItemStack stack) {
                                return entity.isValidInput(stack);
                            }
                        });
                    }
                }
                // Outputs: x=106,124,142; y=28,46,64,82
                for (int r = 0; r < 4; r++) {
                    for (int c = 0; c < 3; c++) {
                        int slotIndex = 12 + r * 3 + c;
                        this.addSlot(new SlotItemHandler(handler, slotIndex, 106 + c * 18, 28 + r * 18) {
                            @Override
                            public boolean mayPlace(ItemStack stack) {
                                return false;
                            }
                        });
                    }
                }
            }

            // Battery slot (x=202, y=28)
            this.addSlot(new SlotItemHandler(handler, entity.getBatterySlot(), 202, 28));

            // Upgrades slots (x=170, y=28, 46, 64, 82)
            int upStart = entity.getUpgradeSlotStart();
            for (int i = 0; i < 4; i++) {
                this.addSlot(new SlotItemHandler(handler, upStart + i, 170, 28 + i * 18));
            }
        }

        // Player Inventory (x=26, y=132)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inv, col + row * 9 + 9, 26 + col * 18, 132 + row * 18));
            }
        }

        // Player Hotbar (x=26, y=190)
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inv, col, 26 + col * 18, 190));
        }
    }

    public int getNumChannels() {
        return numChannels;
    }

    public MultiSlotMachineBlockEntity getBlockEntity() {
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
        return max != 0 && e != 0 ? (int) ((long) e * pixels / max) : 0;
    }

    public MetalFormerRecipe.Mode getMetalFormerMode() {
        int m = this.data.get(4);
        MetalFormerRecipe.Mode[] modes = MetalFormerRecipe.Mode.values();
        if (m >= 0 && m < modes.length) {
            return modes[m];
        }
        return MetalFormerRecipe.Mode.ROLLING;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (this.blockEntity != null && id == 3) {
            this.blockEntity.cycleMode();
            return true;
        }
        return super.clickMenuButton(player, id);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemstack = stackInSlot.copy();

            int machineSlotsCount = (numChannels * 2) + 1 + 4;

            if (index < machineSlotsCount) {
                // Move from machine to player inventory
                if (!this.moveItemStackTo(stackInSlot, machineSlotsCount, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Move from player inventory to machine
                if (blockEntity != null && blockEntity.isValidInput(stackInSlot)) {
                    if (!this.moveItemStackTo(stackInSlot, 0, numChannels, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (stackInSlot.getItem() instanceof com.onter.onter_ic2.item.UpgradeItem) {
                    int upStart = numChannels * 2 + 1;
                    if (!this.moveItemStackTo(stackInSlot, upStart, upStart + 4, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (stackInSlot.getItem() instanceof com.onter.onter_ic2.item.BatteryItem) {
                    int batSlot = numChannels * 2;
                    if (!this.moveItemStackTo(stackInSlot, batSlot, batSlot + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    return ItemStack.EMPTY;
                }
            }

            if (stackInSlot.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.blockEntity != null && this.blockEntity.getLevel() != null &&
                this.blockEntity.getLevel().getBlockEntity(this.blockEntity.getBlockPos()) == this.blockEntity &&
                player.distanceToSqr(this.blockEntity.getBlockPos().getX() + 0.5,
                        this.blockEntity.getBlockPos().getY() + 0.5,
                        this.blockEntity.getBlockPos().getZ() + 0.5) <= 64.0;
    }
}
