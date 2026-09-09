package com.onter.onter_ic2.menu;

import com.onter.onter_ic2.block.generators.GeneratorBlockEntity;
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
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.items.SlotItemHandler;

public class GeneratorMenu extends AbstractContainerMenu {
    private final GeneratorBlockEntity blockEntity;
    private final ContainerData data;

    public GeneratorMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv,
                (GeneratorBlockEntity) inv.player.level().getBlockEntity(extraData.readBlockPos()),
                new SimpleContainerData(4));
    }

    public GeneratorMenu(int containerId, Inventory inv, GeneratorBlockEntity entity, ContainerData data) {
        super(ModMenuTypes.GENERATOR_MENU.get(), containerId);
        this.blockEntity = entity;
        this.data = data;

        addDataSlots(data);

        // 0: Fuel slot at (65, 53)
        this.addSlot(new SlotItemHandler(entity.getItemHandler(), GeneratorBlockEntity.SLOT_FUEL, 65, 53));
        // 1: Charge battery slot at (65, 17)
        this.addSlot(new SlotItemHandler(entity.getItemHandler(), GeneratorBlockEntity.SLOT_CHARGE, 65, 17) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof BatteryItem || stack.getCapability(net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage.ITEM, null) != null;
            }
        });

        addPlayerInventory(inv);
        addPlayerHotbar(inv);
    }

    public int getBurnTime() {
        return this.data.get(0);
    }

    public int getMaxBurnTime() {
        return this.data.get(1);
    }

    public int getEnergy() {
        return this.data.get(2);
    }

    public int getMaxEnergy() {
        return this.data.get(3);
    }

    public int getScaledBurnTime(int pixels) {
        int bt = getBurnTime();
        int max = getMaxBurnTime();
        return max != 0 && bt != 0 ? bt * pixels / max : 0;
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

            if (index < 2) {
                if (!this.moveItemStackTo(itemstack1, 2, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (itemstack1.getItem() instanceof BatteryItem || itemstack1.getCapability(net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage.ITEM, null) != null) {
                    if (!this.moveItemStackTo(itemstack1, GeneratorBlockEntity.SLOT_CHARGE, GeneratorBlockEntity.SLOT_CHARGE + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (itemstack1.getBurnTime(RecipeType.SMELTING) > 0) {
                    if (!this.moveItemStackTo(itemstack1, GeneratorBlockEntity.SLOT_FUEL, GeneratorBlockEntity.SLOT_FUEL + 1, false)) {
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
