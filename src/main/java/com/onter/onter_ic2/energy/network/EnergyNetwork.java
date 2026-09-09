package com.onter.onter_ic2.energy.network;

import com.onter.onter_ic2.block.cables.CableBlockEntity;
import com.onter.onter_ic2.energy.EnergyPriority;
import com.onter.onter_ic2.energy.IEnergyPrioritized;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.*;

public class EnergyNetwork {
    private final UUID id = UUID.randomUUID();
    private final Set<BlockPos> cables = new HashSet<>();
    private final Set<ConsumerNode> consumers = new HashSet<>();
    private int networkBuffer = 0;
    private boolean dirty = true;
    private boolean valid = true;

    public record ConsumerNode(BlockPos pos, Direction side) {}

    public record ActiveConsumer(ConsumerNode node, IEnergyStorage storage, int demand, EnergyPriority priority) {}

    public EnergyNetwork() {}

    public UUID getId() {
        return id;
    }

    public boolean isValid() {
        return valid;
    }

    public void invalidate() {
        this.valid = false;
        this.cables.clear();
        this.consumers.clear();
    }

    public void markDirty() {
        this.dirty = true;
    }

    public boolean isDirty() {
        return dirty;
    }

    public Set<BlockPos> getCables() {
        return cables;
    }

    public int getNetworkBuffer() {
        return networkBuffer;
    }

    public void addCable(BlockPos pos) {
        cables.add(pos);
    }

    public void removeCable(BlockPos pos) {
        cables.remove(pos);
        markDirty();
    }

    /**
     * Поступление энергии от любого подключенного источника в общий сетевой буфер.
     * Лимиты на кабелях отсутствуют - сеть принимает любой объем энергии.
     */
    public int receiveEnergy(int toReceive, boolean simulate) {
        if (toReceive <= 0 || !valid) return 0;
        if (!simulate) {
            long newTotal = (long) networkBuffer + toReceive;
            networkBuffer = (int) Math.min(Integer.MAX_VALUE, newTotal);
        }
        return toReceive;
    }

    /**
     * Перестроение топологии сети (BFS) только при изменении структуры (установка/слом проводов).
     */
    public void rebuild(Level level, BlockPos startPos) {
        cables.clear();
        consumers.clear();

        if (level == null || startPos == null || !level.isLoaded(startPos)) {
            dirty = false;
            return;
        }

        Queue<BlockPos> queue = new ArrayDeque<>();
        queue.add(startPos);
        cables.add(startPos);

        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();
            BlockEntity currentBe = level.getBlockEntity(current);
            if (currentBe instanceof CableBlockEntity cable) {
                cable.setNetwork(this);
            }

            for (Direction dir : Direction.values()) {
                BlockPos neighborPos = current.relative(dir);
                if (!level.isLoaded(neighborPos)) {
                    continue;
                }

                BlockEntity neighborBe = level.getBlockEntity(neighborPos);
                if (neighborBe instanceof CableBlockEntity) {
                    if (cables.add(neighborPos)) {
                        queue.add(neighborPos);
                    }
                } else if (neighborBe != null) {
                    IEnergyStorage storage = level.getCapability(Capabilities.EnergyStorage.BLOCK, neighborPos, dir.getOpposite());
                    if (storage != null && storage.canReceive()) {
                        consumers.add(new ConsumerNode(neighborPos, dir.getOpposite()));
                    }
                }
            }
        }

        dirty = false;
    }

    /**
     * Серверный тик энергосети:
     * 1. Фильтрует активных прогруженных потребителей.
     * 2. Группирует по приоритетам (HIGH -> NORMAL -> LOW).
     * 3. Равномерно (Fair Share) распределяет буфер энергии.
     */
    public void tick(Level level) {
        if (!valid || level == null || level.isClientSide) return;

        if (dirty) {
            Optional<BlockPos> firstLoaded = cables.stream().filter(level::isLoaded).findFirst();
            if (firstLoaded.isPresent()) {
                rebuild(level, firstLoaded.get());
            } else {
                return;
            }
        }

        if (networkBuffer <= 0 || consumers.isEmpty()) return;

        // Собираем всех доступных потребителей в прогруженных чанках
        List<ActiveConsumer> active = new ArrayList<>();
        for (ConsumerNode node : consumers) {
            if (!level.isLoaded(node.pos())) continue;

            IEnergyStorage target = level.getCapability(Capabilities.EnergyStorage.BLOCK, node.pos(), node.side());
            if (target == null || !target.canReceive()) continue;

            int demand = target.receiveEnergy(Integer.MAX_VALUE, true);
            if (demand <= 0) continue;

            BlockEntity be = level.getBlockEntity(node.pos());
            EnergyPriority priority = EnergyPriority.NORMAL;
            if (be instanceof IEnergyPrioritized prioritized) {
                priority = prioritized.getEnergyPriority();
            }

            active.add(new ActiveConsumer(node, target, demand, priority));
        }

        if (active.isEmpty()) return;

        // Распределяем энергию по приоритетам: HIGH (2) -> NORMAL (1) -> LOW (0)
        for (int pLevel = 2; pLevel >= 0; pLevel--) {
            if (networkBuffer <= 0) break;

            final int curLevel = pLevel;
            List<ActiveConsumer> tierConsumers = active.stream()
                    .filter(c -> c.priority().getLevel() == curLevel)
                    .toList();

            if (tierConsumers.isEmpty()) continue;

            long tierTotalDemand = 0;
            for (ActiveConsumer c : tierConsumers) {
                tierTotalDemand += c.demand();
            }

            if (tierTotalDemand <= networkBuffer) {
                // Энергии хватает на 100% потребностей этой группы
                for (ActiveConsumer c : tierConsumers) {
                    int accepted = c.storage().receiveEnergy(c.demand(), false);
                    networkBuffer -= accepted;
                    if (networkBuffer <= 0) break;
                }
            } else {
                // Энергии меньше, чем запросили: делим поровну (Fair Share)
                int remainingConsumers = tierConsumers.size();
                for (ActiveConsumer c : tierConsumers) {
                    if (networkBuffer <= 0) break;
                    int fairShare = networkBuffer / remainingConsumers;
                    int toSend = Math.min(c.demand(), Math.max(1, fairShare));
                    int accepted = c.storage().receiveEnergy(toSend, false);
                    networkBuffer -= accepted;
                    remainingConsumers--;
                }
            }
        }
    }
}
