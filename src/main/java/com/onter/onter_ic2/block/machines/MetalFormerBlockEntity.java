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
        this(ModBlockEntities.METAL_FORMER.get(), pos, blockState, 40000, 10, 200, 1); // 10 FE/t, 200 ticks
    }

    public MetalFormerBlockEntity(net.minecraft.world.level.block.entity.BlockEntityType<?> type, BlockPos pos, BlockState blockState,
                                  int capacity, int energyPerTick, int maxProgress, int batchMultiplier) {
        super(type, pos, blockState, capacity, energyPerTick, maxProgress, batchMultiplier);
    }

    public MetalFormerRecipe.Mode getMode() {
        return mode;
    }

    public void setMode(MetalFormerRecipe.Mode newMode) {
        if (this.mode != newMode) {
            this.mode = newMode;
            this.progress = 0;
            setChanged();
        }
    }

    public void cycleMode() {
        int next = (mode.ordinal() + 1) % MetalFormerRecipe.Mode.values().length;
        this.mode = MetalFormerRecipe.Mode.values()[next];
        this.progress = 0;
        setChanged();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(getBlockState().getBlock().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new MetalFormerMenu(containerId, playerInventory, this, this.metalFormerDataAccess);
    }

    @Override
    public boolean isValidInput(ItemStack stack) {
        if (stack.isEmpty() || level == null) return false;
        return level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.METAL_FORMER_RECIPE_TYPE.get()).stream()
                .anyMatch(r -> r.value().matches(new SingleRecipeInput(stack), this.mode));
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
        if (!surplusOutputBuffer.isEmpty()) return false;
        Optional<RecipeHolder<MetalFormerRecipe>> recipe = getCurrentRecipe();
        if (recipe.isEmpty()) return false;

        ItemStack result = recipe.get().value().getResultItem(level.registryAccess());
        ItemStack outputSlot = itemHandler.getStackInSlot(SLOT_OUTPUT);

        if (outputSlot.isEmpty()) return true;
        return ItemStack.isSameItemSameComponents(outputSlot, result) && outputSlot.getCount() < outputSlot.getMaxStackSize();
    }

    @Override
    protected void processItem() {
        Optional<RecipeHolder<MetalFormerRecipe>> recipe = getCurrentRecipe();
        if (recipe.isPresent() && canProcess()) {
            ItemStack input = itemHandler.getStackInSlot(SLOT_INPUT);
            int toExtract = Math.min(batchMultiplier, input.getCount());
            ItemStack result = recipe.get().value().assemble(new SingleRecipeInput(input), level.registryAccess());
            itemHandler.extractItem(SLOT_INPUT, toExtract, false);
            produceOutput(result, toExtract);
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
