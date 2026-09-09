package com.onter.onter_ic2.menu;

import com.onter.onter_ic2.block.base.BaseMachineBlockEntity;
import com.onter.onter_ic2.block.cables.CableBlockEntity;
import com.onter.onter_ic2.block.generators.GeneratorBlockEntity;
import com.onter.onter_ic2.block.generators.QuantumGeneratorBlockEntity;
import com.onter.onter_ic2.block.generators.SolarPanelBlockEntity;
import com.onter.onter_ic2.block.machines.MolecularTransformerBlockEntity;
import com.onter.onter_ic2.block.machines.MultiSlotMachineBlockEntity;
import com.onter.onter_ic2.block.storage.EnergyStorageBlockEntity;
import com.onter.onter_ic2.init.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

public class MeterMenu extends AbstractContainerMenu {
    public enum Mode {
        EnergyIn,
        EnergyOut,
        EnergyGain,
        Voltage
    }

    private final BlockPos targetPos;
    private final Direction targetSide;
    private final ContainerData data;

    private int resultAvg = 0;
    private int resultMin = 0;
    private int resultMax = 0;
    private int resultCount = 0;
    private int mode = 0; // 0: EnergyIn, 1: EnergyOut, 2: EnergyGain, 3: Voltage
    private int maxVoltageLimit = 32;
    private int tier = 1;

    public MeterMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, extraData.readBlockPos(), extraData.readEnum(Direction.class), new SimpleContainerData(12));
    }

    public MeterMenu(int containerId, Inventory inv, BlockPos pos, Direction side, ContainerData data) {
        super(ModMenuTypes.METER_MENU.get(), containerId);
        this.targetPos = pos;
        this.targetSide = side;
        this.data = data;

        addDataSlots(data);

        // Player Inventory: x = 8, y = 135
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, 135 + row * 18));
            }
        }

        // Player Hotbar: x = 8, y = 193
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(inv, col, 8 + col * 18, 193));
        }
    }

    public static ContainerData createServerContainerData() {
        return new SimpleContainerData(12);
    }

    public BlockPos getTargetPos() {
        return targetPos;
    }

    public int getResultAvg() {
        return this.data.get(0);
    }

    public int getResultMin() {
        return this.data.get(1);
    }

    public int getResultMax() {
        return this.data.get(2);
    }

    public int getResultCount() {
        return this.data.get(3);
    }

    public Mode getMode() {
        int m = this.data.get(4);
        if (m < 0 || m >= Mode.values().length) return Mode.EnergyIn;
        return Mode.values()[m];
    }

    public int getStoredEnergy() {
        return (this.data.get(6) << 16) | (this.data.get(5) & 0xFFFF);
    }

    public int getMaxEnergy() {
        return (this.data.get(8) << 16) | (this.data.get(7) & 0xFFFF);
    }

    public int getMaxVoltageLimit() {
        return (this.data.get(10) << 16) | (this.data.get(9) & 0xFFFF);
    }

    public int getTier() {
        return this.data.get(11);
    }

    public void reset() {
        this.resultCount = 0;
        this.resultAvg = 0;
        this.resultMin = 0;
        this.resultMax = 0;
        updateDataSlots(0, 0);
    }

    public void setMode(int modeIndex) {
        this.mode = Math.max(0, Math.min(3, modeIndex));
        this.reset();
    }

    @Override
    public boolean clickMenuButton(@NotNull Player player, int id) {
        if (id >= 0 && id <= 3) {
            setMode(id);
            return true;
        } else if (id == 4) {
            reset();
            return true;
        }
        return false;
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();

        if (targetPos != null) {
            Level level = null;
            if (!this.slots.isEmpty() && this.slots.get(0).container instanceof Inventory inv) {
                level = inv.player.level();
            }

            if (level != null && !level.isClientSide()) {
                BlockEntity be = level.getBlockEntity(targetPos);
                IEnergyStorage storage = level.getCapability(Capabilities.EnergyStorage.BLOCK, targetPos, targetSide);

                int stored = storage != null ? storage.getEnergyStored() : 0;
                int max = storage != null ? storage.getMaxEnergyStored() : 0;

                double currentEU = 0;
                maxVoltageLimit = 32;
                tier = 1;

                if (be instanceof GeneratorBlockEntity gen) {
                    boolean isBurning = gen.getDataAccess().get(0) > 0;
                    if (mode == 1 || mode == 2) currentEU = isBurning ? 10 : 0;
                    maxVoltageLimit = 32;
                    tier = 1;
                } else if (be instanceof SolarPanelBlockEntity solar) {
                    if (mode == 1 || mode == 2) currentEU = solar.getDayGen() / 4.0;
                    maxVoltageLimit = solar.getDayGen() / 4;
                    tier = solar.getDayGen() <= 32 ? 1 : (solar.getDayGen() <= 128 ? 2 : (solar.getDayGen() <= 512 ? 3 : 4));
                } else if (be instanceof QuantumGeneratorBlockEntity qg) {
                    if (mode == 1 || mode == 2) currentEU = qg.isActive() ? qg.getProduction() : 0;
                    maxVoltageLimit = qg.getProduction();
                    tier = qg.getTier();
                } else if (be instanceof MolecularTransformerBlockEntity mt) {
                    if (mode == 0) currentEU = mt.getLastEnergyGiven();
                    else if (mode == 2) currentEU = -mt.getLastEnergyGiven();
                    maxVoltageLimit = 8192;
                    tier = 5;
                } else if (be instanceof BaseMachineBlockEntity machine) {
                    boolean isRunning = machine.getProgress() > 0;
                    if (mode == 0) currentEU = isRunning ? (machine.getBaseEnergyPerTick() / 4.0) : 0;
                    else if (mode == 2) currentEU = isRunning ? -(machine.getBaseEnergyPerTick() / 4.0) : 0;
                    maxVoltageLimit = 32;
                    tier = 1;
                } else if (be instanceof MultiSlotMachineBlockEntity multiMachine) {
                    boolean isRunning = multiMachine.getProgress() > 0;
                    if (mode == 0) currentEU = isRunning ? (multiMachine.getBaseEnergyPerTick() / 4.0) : 0;
                    else if (mode == 2) currentEU = isRunning ? -(multiMachine.getBaseEnergyPerTick() / 4.0) : 0;
                    maxVoltageLimit = multiMachine.getNumChannels() > 6 ? 512 : 128;
                    tier = multiMachine.getNumChannels() > 6 ? 3 : 2;
                } else if (be instanceof EnergyStorageBlockEntity esbe) {
                    int transferEU = esbe.getMaxTransfer() / 4;
                    if (mode == 0) currentEU = Math.min(transferEU, (max - stored) / 4.0);
                    else if (mode == 1) currentEU = Math.min(transferEU, stored / 4.0);
                    else if (mode == 2) currentEU = 0;
                    maxVoltageLimit = transferEU;
                    tier = transferEU <= 32 ? 1 : (transferEU <= 128 ? 2 : (transferEU <= 512 ? 3 : 4));
                } else if (be instanceof CableBlockEntity cable) {
                    maxVoltageLimit = cable.getMaxTransfer() / 4;
                    tier = maxVoltageLimit <= 32 ? 1 : (maxVoltageLimit <= 128 ? 2 : (maxVoltageLimit <= 512 ? 3 : (maxVoltageLimit <= 2048 ? 4 : 5)));
                    currentEU = mode == 3 ? maxVoltageLimit : 0;
                } else if (storage != null) {
                    maxVoltageLimit = storage.getMaxEnergyStored() > 100000 ? 512 : 32;
                    tier = maxVoltageLimit <= 32 ? 1 : 3;
                    if (mode == 0 && storage.canReceive()) currentEU = storage.receiveEnergy(10000, true) / 4.0;
                    else if (mode == 1 && storage.canExtract()) currentEU = storage.extractEnergy(10000, true) / 4.0;
                }

                double measuredValue = (mode == 3) ? maxVoltageLimit : currentEU;

                if (resultCount == 0) {
                    resultAvg = (int) measuredValue;
                    resultMin = (int) measuredValue;
                    resultMax = (int) measuredValue;
                } else {
                    if (measuredValue < resultMin) resultMin = (int) measuredValue;
                    if (measuredValue > resultMax) resultMax = (int) measuredValue;
                    resultAvg = (int) ((resultAvg * (long) resultCount + (long) measuredValue) / (resultCount + 1));
                }

                resultCount++;
                updateDataSlots(stored, max);
            }
        }
    }

    private void updateDataSlots(int stored, int max) {
        this.data.set(0, resultAvg);
        this.data.set(1, resultMin);
        this.data.set(2, resultMax);
        this.data.set(3, resultCount);
        this.data.set(4, mode);
        this.data.set(5, stored & 0xFFFF);
        this.data.set(6, (stored >> 16) & 0xFFFF);
        this.data.set(7, max & 0xFFFF);
        this.data.set(8, (max >> 16) & 0xFFFF);
        this.data.set(9, maxVoltageLimit & 0xFFFF);
        this.data.set(10, (maxVoltageLimit >> 16) & 0xFFFF);
        this.data.set(11, tier);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return targetPos == null || player.distanceToSqr(targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5) <= 64.0;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        return ItemStack.EMPTY;
    }
}
