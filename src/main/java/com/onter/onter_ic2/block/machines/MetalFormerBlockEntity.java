package com.onter.onter_ic2.block.machines;

import com.onter.onter_ic2.block.base.BaseMachineBlockEntity;
import com.onter.onter_ic2.init.ModBlockEntities;
import com.onter.onter_ic2.init.ModRecipeTypes;
import com.onter.onter_ic2.menu.MetalFormerMenu;
import com.onter.onter_ic2.recipe.MetalFormerRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class MetalFormerBlockEntity extends BaseMachineBlockEntity {
    private MetalFormerRecipe.Mode mode = MetalFormerRecipe.Mode.ROLLING;

    protected final ContainerData metalFormerDataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> maxProgress;
                case 2 -> energyStorage.getEnergyStored();
                case 3 -> energyStorage.getMaxEnergyStored();
                case 4 -> mode.ordinal();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> progress = value;
                case 1 -> maxProgress = value;
                case 2 -> energyStorage.setEnergy(value);
                case 3 -> energyStorage.setCapacity(value);
                case 4 -> {
                    MetalFormerRecipe.Mode[] modes = MetalFormerRecipe.Mode.values();
                    if (value >= 0 && value < modes.length) {
                        mode = modes[value];
                    }
                }
            }
        }

        @Override
        public int getCount() {
            return 5;
        }
    };

    public MetalFormerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.METAL_FORMER.get(), pos, blockState, 40000, 10, 200); // 10 FE/t, 200 ticks
    }

    public MetalFormerRecipe.Mode getMode() {
        return mode;
    }

    public void cycleMode() {
        int next = (mode.ordinal() + 1) % MetalFormerRecipe.Mode.values().length;
        this.mode = MetalFormerRecipe.Mode.values()[next];
        this.progress = 0;
        setChanged();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.onter_ic2.metal_former");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new MetalFormerMenu(containerId, playerInventory, this, this.metalFormerDataAccess);
    }

    private Optional<RecipeHolder<MetalFormerRecipe>> getCurrentRecipe() {
        if (level == null) return Optional.empty();
        ItemStack input = itemHandler.getStackInSlot(SLOT_INPUT);
        if (input.isEmpty()) return Optional.empty();

        return level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.METAL_FORMER_RECIPE_TYPE.get()).stream()
                .filter(r -> r.value().matches(new SingleRecipeInput(input), this.mode))
                .findFirst();
    }

    @Override
    protected boolean canProcess() {
        Optional<RecipeHolder<MetalFormerRecipe>> recipe = getCurrentRecipe();
        if (recipe.isEmpty()) return false;

        ItemStack result = recipe.get().value().getResultItem(level.registryAccess());
        ItemStack outputSlot = itemHandler.getStackInSlot(SLOT_OUTPUT);

        if (outputSlot.isEmpty()) return true;
        if (!ItemStack.isSameItemSameComponents(outputSlot, result)) return false;
        return outputSlot.getCount() + result.getCount() <= outputSlot.getMaxStackSize();
    }

    @Override
    protected void processItem() {
        Optional<RecipeHolder<MetalFormerRecipe>> recipe = getCurrentRecipe();
        if (recipe.isPresent() && canProcess()) {
            ItemStack result = recipe.get().value().assemble(new SingleRecipeInput(itemHandler.getStackInSlot(SLOT_INPUT)), level.registryAccess());
            itemHandler.extractItem(SLOT_INPUT, 1, false);

            ItemStack outputSlot = itemHandler.getStackInSlot(SLOT_OUTPUT);
            if (outputSlot.isEmpty()) {
                itemHandler.setStackInSlot(SLOT_OUTPUT, result.copy());
            } else {
                outputSlot.grow(result.getCount());
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("Mode", mode.getSerializedName());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Mode")) {
            for (MetalFormerRecipe.Mode m : MetalFormerRecipe.Mode.values()) {
                if (m.getSerializedName().equalsIgnoreCase(tag.getString("Mode"))) {
                    this.mode = m;
                    break;
                }
            }
        }
    }
}
