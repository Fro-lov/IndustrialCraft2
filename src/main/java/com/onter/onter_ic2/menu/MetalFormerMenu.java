package com.onter.onter_ic2.menu;

import com.onter.onter_ic2.block.base.BaseMachineBlockEntity;
import com.onter.onter_ic2.block.machines.MetalFormerBlockEntity;
import com.onter.onter_ic2.init.ModMenuTypes;
import com.onter.onter_ic2.recipe.MetalFormerRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;

public class MetalFormerMenu extends BaseMachineMenu {
    public MetalFormerMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv,
                (MetalFormerBlockEntity) inv.player.level().getBlockEntity(extraData.readBlockPos()),
                new SimpleContainerData(5));
    }

    public MetalFormerMenu(int containerId, Inventory inv, MetalFormerBlockEntity entity, ContainerData data) {
        super(ModMenuTypes.METAL_FORMER_MENU.get(), containerId, inv, entity, data);
    }

    @Override
    protected void addMachineSlots(BaseMachineBlockEntity entity) {
        // 0: Input (top left at x=17, y=17)
        this.addSlot(new SlotItemHandler(entity.getItemHandler(), BaseMachineBlockEntity.SLOT_INPUT, 17, 17) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return entity.isValidInput(stack);
            }
        });
        // 1: Output (right at x=116, y=35)
        this.addSlot(new SlotItemHandler(entity.getItemHandler(), BaseMachineBlockEntity.SLOT_OUTPUT, 116, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        // 2: Battery (bottom left at x=17, y=53)
        this.addSlot(new SlotItemHandler(entity.getItemHandler(), BaseMachineBlockEntity.SLOT_BATTERY, 17, 53) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return entity.getItemHandler().isItemValid(BaseMachineBlockEntity.SLOT_BATTERY, stack);
            }
        });
        // 3..6: Upgrades (far right at x=152, y=8..62)
        for (int i = 0; i < 4; i++) {
            final int slotIdx = BaseMachineBlockEntity.SLOT_UPGRADE_1 + i;
            this.addSlot(new SlotItemHandler(entity.getItemHandler(), slotIdx, 152, 8 + i * 18) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return stack.getItem() instanceof com.onter.onter_ic2.item.UpgradeItem;
                }
            });
        }
    }

    public MetalFormerRecipe.Mode getMode() {
        int m = this.data.get(4);
        MetalFormerRecipe.Mode[] modes = MetalFormerRecipe.Mode.values();
        if (m >= 0 && m < modes.length) {
            return modes[m];
        }
        return MetalFormerRecipe.Mode.ROLLING;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (this.blockEntity instanceof MetalFormerBlockEntity metalFormer) {
            if (id == 0) {
                metalFormer.setMode(MetalFormerRecipe.Mode.EXTRUDING);
                return true;
            } else if (id == 1) {
                metalFormer.setMode(MetalFormerRecipe.Mode.ROLLING);
                return true;
            } else if (id == 2) {
                metalFormer.setMode(MetalFormerRecipe.Mode.CUTTING);
                return true;
            } else if (id == 3) {
                metalFormer.cycleMode();
                return true;
            }
        }
        return super.clickMenuButton(player, id);
    }
}
