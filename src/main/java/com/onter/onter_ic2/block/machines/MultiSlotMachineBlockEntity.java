package com.onter.onter_ic2.block.machines;

import com.onter.onter_ic2.block.base.BaseMachineBlock;
import com.onter.onter_ic2.energy.IC2EnergyStorage;
import com.onter.onter_ic2.init.ModItems;
import com.onter.onter_ic2.init.ModRecipeTypes;
import com.onter.onter_ic2.item.BatteryItem;
import com.onter.onter_ic2.item.EjectorUpgradeItem;
import com.onter.onter_ic2.item.PullingUpgradeItem;
import com.onter.onter_ic2.item.UpgradeItem;
import com.onter.onter_ic2.menu.MultiSlotMachineMenu;
import com.onter.onter_ic2.recipe.CompressorRecipe;
import com.onter.onter_ic2.recipe.ExtractorRecipe;
import com.onter.onter_ic2.recipe.MaceratorRecipe;
import com.onter.onter_ic2.recipe.MetalFormerRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MultiSlotMachineBlockEntity extends BlockEntity implements MenuProvider {
    public enum MachineType {
        MACERATOR,
        ELECTRIC_FURNACE,
        COMPRESSOR,
        EXTRACTOR,
        METAL_FORMER
    }

    private final MachineType machineType;
    private final int numChannels; // 6 or 12
    private final int baseCapacity;
    private final int baseEnergyPerTick;
    private final int baseMaxProgress;

    private int progress = 0;
    private int maxProgress = 100;
    private MetalFormerRecipe.Mode metalFormerMode = MetalFormerRecipe.Mode.ROLLING;

    protected final IC2EnergyStorage energyStorage;
    protected final ItemStackHandler itemHandler;

    public final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> maxProgress;
                case 2 -> energyStorage.getEnergyStored();
                case 3 -> energyStorage.getMaxEnergyStored();
                case 4 -> metalFormerMode.ordinal();
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
                    if (value >= 0 && value < MetalFormerRecipe.Mode.values().length) {
                        metalFormerMode = MetalFormerRecipe.Mode.values()[value];
                    }
                }
            }
        }

        @Override
        public int getCount() {
            return 5;
        }
    };

    public MultiSlotMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState,
                                       MachineType machineType, int numChannels,
                                       int baseCapacity, int baseEnergyPerTick, int baseMaxProgress) {
        super(type, pos, blockState);
        this.machineType = machineType;
        this.numChannels = numChannels;
        this.baseCapacity = baseCapacity;
        this.baseEnergyPerTick = baseEnergyPerTick;
        this.baseMaxProgress = baseMaxProgress;
        this.maxProgress = baseMaxProgress;

        this.energyStorage = new IC2EnergyStorage(baseCapacity, 20000, 0, this::setChanged);

        int totalSlots = (numChannels * 2) + 1 + 4;
        this.itemHandler = new ItemStackHandler(totalSlots) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                if (slot >= numChannels && slot < numChannels * 2) return false; // output
                if (slot == getBatterySlot()) return stack.getItem() instanceof BatteryItem || stack.getCapability(Capabilities.EnergyStorage.ITEM, null) != null;
                if (slot >= getUpgradeSlotStart() && slot < getUpgradeSlotStart() + 4) return stack.getItem() instanceof UpgradeItem;
                if (slot < numChannels) return isValidInput(stack);
                return true;
            }
        };
    }

    public int getNumChannels() {
        return numChannels;
    }

    public MachineType getMachineType() {
        return machineType;
    }

    public int getBatterySlot() {
        return numChannels * 2;
    }

    public int getUpgradeSlotStart() {
        return (numChannels * 2) + 1;
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    public IItemHandler getItemHandler(@Nullable Direction side) {
        return itemHandler;
    }

    public IC2EnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    public MetalFormerRecipe.Mode getMetalFormerMode() {
        return metalFormerMode;
    }

    public void setMetalFormerMode(MetalFormerRecipe.Mode mode) {
        this.metalFormerMode = mode;
        this.progress = 0;
        setChanged();
        if (level != null && !level.isClientSide) {
            BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, 3);
        }
    }

    public void cycleMode() {
        MetalFormerRecipe.Mode[] modes = MetalFormerRecipe.Mode.values();
        setMetalFormerMode(modes[(metalFormerMode.ordinal() + 1) % modes.length]);
    }

    public boolean isValidInput(ItemStack stack) {
        if (stack.isEmpty() || level == null) return false;
        return switch (machineType) {
            case MACERATOR -> level.getRecipeManager().getRecipeFor(ModRecipeTypes.MACERATOR_RECIPE_TYPE.get(), new SingleRecipeInput(stack), level).isPresent();
            case ELECTRIC_FURNACE -> level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(stack), level).isPresent();
            case COMPRESSOR -> level.getRecipeManager().getRecipeFor(ModRecipeTypes.COMPRESSOR_RECIPE_TYPE.get(), new SingleRecipeInput(stack), level).isPresent();
            case EXTRACTOR -> level.getRecipeManager().getRecipeFor(ModRecipeTypes.EXTRACTOR_RECIPE_TYPE.get(), new SingleRecipeInput(stack), level).isPresent();
            case METAL_FORMER -> getMetalFormerRecipe(stack, metalFormerMode) != null;
        };
    }

    @Nullable
    private ItemStack getRecipeOutput(ItemStack input) {
        if (input.isEmpty() || level == null) return null;
        return switch (machineType) {
            case MACERATOR -> level.getRecipeManager().getRecipeFor(ModRecipeTypes.MACERATOR_RECIPE_TYPE.get(), new SingleRecipeInput(input), level)
                    .map(h -> h.value().getResultItem(level.registryAccess())).orElse(null);
            case ELECTRIC_FURNACE -> level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(input), level)
                    .map(h -> h.value().getResultItem(level.registryAccess())).orElse(null);
            case COMPRESSOR -> level.getRecipeManager().getRecipeFor(ModRecipeTypes.COMPRESSOR_RECIPE_TYPE.get(), new SingleRecipeInput(input), level)
                    .map(h -> h.value().getResultItem(level.registryAccess())).orElse(null);
            case EXTRACTOR -> level.getRecipeManager().getRecipeFor(ModRecipeTypes.EXTRACTOR_RECIPE_TYPE.get(), new SingleRecipeInput(input), level)
                    .map(h -> h.value().getResultItem(level.registryAccess())).orElse(null);
            case METAL_FORMER -> {
                MetalFormerRecipe r = getMetalFormerRecipe(input, metalFormerMode);
                yield r != null ? r.getResultItem(level.registryAccess()) : null;
            }
        };
    }

    @Nullable
    private MetalFormerRecipe getMetalFormerRecipe(ItemStack input, MetalFormerRecipe.Mode mode) {
        if (level == null) return null;
        List<RecipeHolder<MetalFormerRecipe>> recipes = level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.METAL_FORMER_RECIPE_TYPE.get());
        for (RecipeHolder<MetalFormerRecipe> holder : recipes) {
            MetalFormerRecipe recipe = holder.value();
            if (recipe.getMode() == mode && recipe.matches(new SingleRecipeInput(input), level)) {
                return recipe;
            }
        }
        return null;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, MultiSlotMachineBlockEntity entity) {
        if (level.isClientSide) return;

        // 1. Discharge battery in battery slot
        ItemStack batteryStack = entity.itemHandler.getStackInSlot(entity.getBatterySlot());
        if (!batteryStack.isEmpty()) {
            IEnergyStorage itemEnergy = batteryStack.getCapability(Capabilities.EnergyStorage.ITEM, null);
            if (itemEnergy != null && itemEnergy.canExtract()) {
                int needed = entity.energyStorage.getMaxEnergyStored() - entity.energyStorage.getEnergyStored();
                if (needed > 0) {
                    int extracted = itemEnergy.extractEnergy(needed, false);
                    entity.energyStorage.receiveEnergy(extracted, false);
                }
            }
        }

        // 2. Process Upgrades (Overclocker, Energy Storage, Transformer, Ejector, Pulling)
        int overclockerCount = 0;
        int energyStorageCount = 0;
        int transformerCount = 0;
        ItemStack ejectorStack = ItemStack.EMPTY;
        ItemStack pullingStack = ItemStack.EMPTY;

        int upStart = entity.getUpgradeSlotStart();
        for (int i = 0; i < 4; i++) {
            ItemStack up = entity.itemHandler.getStackInSlot(upStart + i);
            if (up.getItem() instanceof UpgradeItem upgrade) {
                switch (upgrade.getType()) {
                    case OVERCLOCKER -> overclockerCount += up.getCount();
                    case ENERGY_STORAGE -> energyStorageCount += up.getCount();
                    case TRANSFORMER -> transformerCount += up.getCount();
                    case EJECTOR -> ejectorStack = up;
                    case PULLING -> pullingStack = up;
                    default -> {}
                }
            }
        }

        // Calculate upgrades modifiers
        int capacity = (int) (entity.baseCapacity * Math.pow(1.6, Math.min(energyStorageCount, 16)));
        entity.energyStorage.setCapacity(capacity);

        double speedMultiplier = Math.pow(1.3, Math.min(overclockerCount, 16));
        double energyMultiplier = Math.pow(1.6, Math.min(overclockerCount, 16));
        int effectiveMaxProgress = Math.max(1, (int) (entity.baseMaxProgress / speedMultiplier));
        int effectiveEnergyPerTick = (int) (entity.baseEnergyPerTick * energyMultiplier);
        entity.maxProgress = effectiveMaxProgress;

        // Handle Automation Upgrades
        entity.handleAutomation(ejectorStack, pullingStack);

        // 3. Parallel Processing across all channels
        boolean anyWorking = false;
        int activeChannels = 0;

        for (int ch = 0; ch < entity.numChannels; ch++) {
            ItemStack input = entity.itemHandler.getStackInSlot(ch);
            if (!input.isEmpty()) {
                ItemStack result = entity.getRecipeOutput(input);
                if (result != null && entity.canOutput(ch, result)) {
                    activeChannels++;
                }
            }
        }

        if (activeChannels > 0 && entity.energyStorage.getEnergyStored() >= effectiveEnergyPerTick) {
            entity.energyStorage.extractEnergy(effectiveEnergyPerTick, false);
            entity.progress++;
            anyWorking = true;

            if (entity.progress >= entity.maxProgress) {
                // Complete cycle for all ready channels
                for (int ch = 0; ch < entity.numChannels; ch++) {
                    ItemStack input = entity.itemHandler.getStackInSlot(ch);
                    if (!input.isEmpty()) {
                        ItemStack result = entity.getRecipeOutput(input);
                        if (result != null && entity.canOutput(ch, result)) {
                            input.shrink(1);
                            entity.produceChannelOutput(ch, result);
                        }
                    }
                }
                entity.progress = 0;
            }
        } else {
            if (entity.progress > 0) {
                entity.progress = Math.max(0, entity.progress - 2);
            }
        }

        // Update blockstate active property
        boolean isBlockActive = state.hasProperty(BaseMachineBlock.LIT) && state.getValue(BaseMachineBlock.LIT);
        if (isBlockActive != anyWorking && state.hasProperty(BaseMachineBlock.LIT)) {
            level.setBlock(pos, state.setValue(BaseMachineBlock.LIT, anyWorking), 3);
        }
    }

    private boolean canOutput(int channel, ItemStack result) {
        int outSlot = numChannels + channel;
        ItemStack currentOut = itemHandler.getStackInSlot(outSlot);
        if (currentOut.isEmpty()) return true;
        if (!ItemStack.isSameItemSameComponents(currentOut, result)) return false;
        return currentOut.getCount() + result.getCount() <= currentOut.getMaxStackSize();
    }

    private void produceChannelOutput(int channel, ItemStack result) {
        int outSlot = numChannels + channel;
        ItemStack currentOut = itemHandler.getStackInSlot(outSlot);
        if (currentOut.isEmpty()) {
            itemHandler.setStackInSlot(outSlot, result.copy());
        } else if (ItemStack.isSameItemSameComponents(currentOut, result)) {
            currentOut.grow(result.getCount());
        }
    }

    private void handleAutomation(ItemStack ejectorStack, ItemStack pullingStack) {
        if (level == null || level.getGameTime() % 8 != 0) return;

        // Ejector upgrade: push output slots to neighboring inventory
        if (!ejectorStack.isEmpty()) {
            Direction targetDir = EjectorUpgradeItem.getDirection(ejectorStack);
            for (int ch = 0; ch < numChannels; ch++) {
                int outSlot = numChannels + ch;
                ItemStack out = itemHandler.getStackInSlot(outSlot);
                if (!out.isEmpty()) {
                    if (targetDir != null) {
                        ejectToSide(outSlot, targetDir);
                    } else {
                        for (Direction side : Direction.values()) {
                            if (ejectToSide(outSlot, side)) break;
                        }
                    }
                }
            }
        }

        // Pulling upgrade: pull valid inputs from neighboring inventory
        if (!pullingStack.isEmpty()) {
            Direction pullDir = PullingUpgradeItem.getDirection(pullingStack);
            for (int ch = 0; ch < numChannels; ch++) {
                if (itemHandler.getStackInSlot(ch).getCount() < 64) {
                    if (pullDir != null) {
                        pullFromSide(ch, pullDir);
                    } else {
                        for (Direction side : Direction.values()) {
                            if (pullFromSide(ch, side)) break;
                        }
                    }
                }
            }
        }
    }

    private boolean ejectToSide(int slot, Direction side) {
        if (level == null) return false;
        BlockEntity neighbor = level.getBlockEntity(worldPosition.relative(side));
        if (neighbor == null) return false;

        IItemHandler targetHandler = level.getCapability(Capabilities.ItemHandler.BLOCK, neighbor.getBlockPos(), side.getOpposite());
        if (targetHandler == null) return false;

        ItemStack toEject = itemHandler.getStackInSlot(slot);
        if (toEject.isEmpty()) return false;

        ItemStack remaining = ItemHandlerHelper.insertItem(targetHandler, toEject, false);
        itemHandler.setStackInSlot(slot, remaining);
        return remaining.getCount() < toEject.getCount();
    }

    private boolean pullFromSide(int slot, Direction side) {
        if (level == null) return false;
        BlockEntity neighbor = level.getBlockEntity(worldPosition.relative(side));
        if (neighbor == null) return false;

        IItemHandler srcHandler = level.getCapability(Capabilities.ItemHandler.BLOCK, neighbor.getBlockPos(), side.getOpposite());
        if (srcHandler == null) return false;

        for (int i = 0; i < srcHandler.getSlots(); i++) {
            ItemStack inSrc = srcHandler.extractItem(i, 1, true);
            if (!inSrc.isEmpty() && isValidInput(inSrc)) {
                ItemStack currentIn = itemHandler.getStackInSlot(slot);
                if (currentIn.isEmpty() || (ItemStack.isSameItemSameComponents(currentIn, inSrc) && currentIn.getCount() < currentIn.getMaxStackSize())) {
                    ItemStack extracted = srcHandler.extractItem(i, 1, false);
                    if (!extracted.isEmpty()) {
                        if (currentIn.isEmpty()) {
                            itemHandler.setStackInSlot(slot, extracted);
                        } else {
                            currentIn.grow(extracted.getCount());
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public NonNullList<ItemStack> getDrops() {
        NonNullList<ItemStack> drops = NonNullList.create();
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                drops.add(stack);
            }
        }
        return drops;
    }

    public void drops() {
        if (level != null) {
            Containers.dropContents(level, worldPosition, getDrops());
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(getBlockState().getBlock().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new MultiSlotMachineMenu(containerId, playerInventory, this, this.dataAccess);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", itemHandler.serializeNBT(registries));
        tag.putInt("Progress", progress);
        tag.putInt("MaxProgress", maxProgress);
        tag.putInt("Energy", energyStorage.getEnergyStored());
        tag.putString("MetalFormerMode", metalFormerMode.name());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Inventory")) {
            itemHandler.deserializeNBT(registries, tag.getCompound("Inventory"));
        }
        progress = tag.getInt("Progress");
        maxProgress = tag.getInt("MaxProgress");
        energyStorage.setEnergy(tag.getInt("Energy"));
        if (tag.contains("MetalFormerMode")) {
            try {
                metalFormerMode = MetalFormerRecipe.Mode.valueOf(tag.getString("MetalFormerMode"));
            } catch (Exception ignored) {}
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
