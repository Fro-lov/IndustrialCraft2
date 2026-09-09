package com.onter.onter_ic2.block.machines;

import com.onter.onter_ic2.energy.IC2EnergyStorage;
import com.onter.onter_ic2.init.ModBlockEntities;
import com.onter.onter_ic2.init.ModItems;
import com.onter.onter_ic2.inventory.MassFabricatorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MassFabricatorBlockEntity extends BlockEntity implements MenuProvider {
    public static final int REQUIRED_PROGRESS = 4_000_000; // 1,000,000 EU
    public static final int CAPACITY = 4_000_000;
    public static final int MAX_RECEIVE = 32_768;

    private final ItemStackHandler itemHandler = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private final IC2EnergyStorage energyStorage = new IC2EnergyStorage(CAPACITY, MAX_RECEIVE, 0, this::setChanged);

    private int progress = 0;
    private int amplifier = 0;

    protected final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> REQUIRED_PROGRESS;
                case 2 -> amplifier;
                case 3 -> energyStorage.getEnergyStored();
                case 4 -> energyStorage.getMaxEnergyStored();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> progress = value;
                case 2 -> amplifier = value;
                case 3 -> energyStorage.setEnergy(value);
            }
        }

        @Override
        public int getCount() {
            return 5;
        }
    };

    public MassFabricatorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MASS_FABRICATOR.get(), pos, state);
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

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;

        // Check Scrap in slot 0 to build up amplifier points
        if (amplifier < 100_000) {
            ItemStack scrapStack = itemHandler.getStackInSlot(0);
            if (!scrapStack.isEmpty()) {
                if (scrapStack.getItem() == ModItems.SCRAP.get()) {
                    scrapStack.shrink(1);
                    amplifier += 5_000;
                    setChanged();
                } else if (scrapStack.getItem() == ModItems.SCRAP_BOX.get()) {
                    scrapStack.shrink(1);
                    amplifier += 45_000;
                    setChanged();
                }
            }
        }

        boolean canOutput = canProduceUUMatter();
        boolean active = false;

        if (canOutput && energyStorage.getEnergyStored() > 0) {
            int consumeEnergy = Math.min(energyStorage.getEnergyStored(), 8192);
            energyStorage.extractEnergy(consumeEnergy, false);

            if (amplifier > 0) {
                int boosted = consumeEnergy * 6;
                progress += boosted;
                amplifier = Math.max(0, amplifier - consumeEnergy);
            } else {
                progress += consumeEnergy;
            }

            active = true;

            if (progress >= REQUIRED_PROGRESS) {
                progress = 0;
                produceUUMatter();
            }

            setChanged();
        }

        if (state.getValue(MassFabricatorBlock.ACTIVE) != active) {
            level.setBlock(pos, state.setValue(MassFabricatorBlock.ACTIVE, active), 3);
        }
    }

    private boolean canProduceUUMatter() {
        ItemStack output = itemHandler.getStackInSlot(1);
        if (output.isEmpty()) return true;
        return output.getItem() == ModItems.UU_MATTER.get() && output.getCount() < output.getMaxStackSize();
    }

    private void produceUUMatter() {
        ItemStack output = itemHandler.getStackInSlot(1);
        if (output.isEmpty()) {
            itemHandler.setStackInSlot(1, new ItemStack(ModItems.UU_MATTER.get(), 1));
        } else if (output.getItem() == ModItems.UU_MATTER.get()) {
            output.grow(1);
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", itemHandler.serializeNBT(registries));
        tag.put("Energy", energyStorage.serializeNBT(registries));
        tag.putInt("Progress", progress);
        tag.putInt("Amplifier", amplifier);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Inventory")) itemHandler.deserializeNBT(registries, tag.getCompound("Inventory"));
        if (tag.contains("Energy")) energyStorage.deserializeNBT(registries, tag.get("Energy"));
        progress = tag.getInt("Progress");
        amplifier = tag.getInt("Amplifier");
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.onter_ic2.mass_fabricator");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory, @NotNull Player player) {
        return new MassFabricatorMenu(containerId, playerInventory, itemHandler, dataAccess);
    }
}
