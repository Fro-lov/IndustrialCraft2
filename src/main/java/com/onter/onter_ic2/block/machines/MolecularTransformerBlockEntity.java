package com.onter.onter_ic2.block.machines;

import com.onter.onter_ic2.energy.IC2EnergyStorage;
import com.onter.onter_ic2.init.ModBlockEntities;
import com.onter.onter_ic2.init.ModItems;
import com.onter.onter_ic2.menu.MolecularTransformerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MolecularTransformerBlockEntity extends BlockEntity implements MenuProvider {
    public static final int CAPACITY = 100_000_000; // 25,000,000 EU

    public record MTRecipe(Item input, int inputCount, ItemStack output, int totalEU) {}

    public static final List<MTRecipe> RECIPES = new ArrayList<>();

    static {
        // Default classic recipes from ASP
        RECIPES.add(new MTRecipe(Items.WITHER_SKELETON_SKULL, 1, new ItemStack(Items.NETHER_STAR), 250_000_000));
        RECIPES.add(new MTRecipe(Items.IRON_INGOT, 1, new ItemStack(ModItems.IRIDIUM_INGOT.get()), 9_000_000));
        RECIPES.add(new MTRecipe(Items.COAL, 1, new ItemStack(Items.DIAMOND), 9_000_000));
        RECIPES.add(new MTRecipe(Items.GLOWSTONE, 1, new ItemStack(ModItems.SUNNARIUM.get()), 9_000_000));
        RECIPES.add(new MTRecipe(Items.GLOWSTONE_DUST, 1, new ItemStack(ModItems.SUNNARIUM_PART.get()), 1_000_000));
        RECIPES.add(new MTRecipe(Items.NETHERRACK, 1, new ItemStack(Items.GUNPOWDER, 2), 70_000));
        RECIPES.add(new MTRecipe(Items.SAND, 1, new ItemStack(Items.GRAVEL), 50_000));
        RECIPES.add(new MTRecipe(Items.DIRT, 1, new ItemStack(Items.CLAY), 50_000));
        RECIPES.add(new MTRecipe(Items.CHARCOAL, 1, new ItemStack(Items.COAL), 60_000));
        RECIPES.add(new MTRecipe(Items.YELLOW_WOOL, 1, new ItemStack(Items.GLOWSTONE_DUST, 2), 500_000));
        RECIPES.add(new MTRecipe(Items.BLUE_WOOL, 1, new ItemStack(Items.LAPIS_LAZULI, 4), 500_000));
        RECIPES.add(new MTRecipe(Items.RED_WOOL, 1, new ItemStack(Items.REDSTONE, 4), 500_000));
    }

    private final ItemStackHandler itemHandler = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private final IC2EnergyStorage energyStorage = new IC2EnergyStorage(CAPACITY, Integer.MAX_VALUE, 0, this::setChanged);

    private double energyUsed = 0;
    private double lastEnergyGiven = 0;
    private int currentRecipeIndex = -1;
    private int inactiveTimer = 0;

    protected final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> (int) (energyUsed % 10000);
                case 1 -> (int) ((energyUsed / 10000) % 10000);
                case 2 -> (int) (energyUsed / 100_000_000);
                case 3 -> (int) lastEnergyGiven;
                case 4 -> currentRecipeIndex;
                case 5 -> energyStorage.getEnergyStored();
                case 6 -> energyStorage.getMaxEnergyStored();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 3 -> lastEnergyGiven = value;
                case 4 -> currentRecipeIndex = value;
                case 5 -> energyStorage.setEnergy(value);
            }
        }

        @Override
        public int getCount() {
            return 7;
        }
    };

    public MolecularTransformerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MOLECULAR_TRANSFORMER.get(), pos, state);
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    public IC2EnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    public ContainerData getDataAccess() {
        return dataAccess;
    }

    public double getEnergyUsed() {
        return energyUsed;
    }

    public double getLastEnergyGiven() {
        return lastEnergyGiven;
    }

    public int getCurrentRecipeIndex() {
        return currentRecipeIndex;
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;

        boolean active = false;
        double energyReceivedThisTick = 0;

        // Try start new recipe if idle
        if (currentRecipeIndex == -1) {
            ItemStack input = itemHandler.getStackInSlot(0);
            if (!input.isEmpty()) {
                for (int i = 0; i < RECIPES.size(); i++) {
                    MTRecipe r = RECIPES.get(i);
                    if (input.getItem() == r.input && input.getCount() >= r.inputCount) {
                        ItemStack output = itemHandler.getStackInSlot(1);
                        if (output.isEmpty() || (ItemStack.isSameItemSameComponents(output, r.output) && output.getCount() + r.output.getCount() <= output.getMaxStackSize())) {
                            currentRecipeIndex = i;
                            energyUsed = 0;
                            input.shrink(r.inputCount);
                            setChanged();
                            break;
                        }
                    }
                }
            }
        }

        if (currentRecipeIndex >= 0 && currentRecipeIndex < RECIPES.size()) {
            MTRecipe recipe = RECIPES.get(currentRecipeIndex);
            double totalEU = recipe.totalEU;
            double neededEU = totalEU - energyUsed;
            int neededFE = (int) Math.min(Integer.MAX_VALUE, neededEU * 4);

            if (energyStorage.getEnergyStored() > 0) {
                int toConsumeFE = Math.min(energyStorage.getEnergyStored(), neededFE);
                energyStorage.consumeEnergy(toConsumeFE);
                double consumedEU = toConsumeFE / 4.0;
                energyUsed += consumedEU;
                energyReceivedThisTick = consumedEU;
                active = true;
                inactiveTimer = 0;

                if (energyUsed >= totalEU) {
                    ItemStack outStack = itemHandler.getStackInSlot(1);
                    if (outStack.isEmpty()) {
                        itemHandler.setStackInSlot(1, recipe.output.copy());
                    } else {
                        outStack.grow(recipe.output.getCount());
                    }
                    currentRecipeIndex = -1;
                    energyUsed = 0;
                }
                setChanged();
            } else {
                inactiveTimer++;
                if (inactiveTimer < 40) {
                    active = true;
                }
            }
        }

        lastEnergyGiven = energyReceivedThisTick;

        if (state.getValue(MolecularTransformerBlock.ACTIVE) != active) {
            level.setBlock(pos, state.setValue(MolecularTransformerBlock.ACTIVE, active), 3);
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", itemHandler.serializeNBT(registries));
        tag.put("Energy", energyStorage.serializeNBT(registries));
        tag.putDouble("EnergyUsed", energyUsed);
        tag.putInt("RecipeIndex", currentRecipeIndex);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Inventory")) itemHandler.deserializeNBT(registries, tag.getCompound("Inventory"));
        if (tag.contains("Energy")) energyStorage.deserializeNBT(registries, tag.get("Energy"));
        energyUsed = tag.getDouble("EnergyUsed");
        currentRecipeIndex = tag.getInt("RecipeIndex");
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.onter_ic2.molecular_transformer");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory, @NotNull Player player) {
        return new MolecularTransformerMenu(containerId, playerInventory, this, dataAccess);
    }
}
