package com.onter.onter_ic2.block.cables;

import com.onter.onter_ic2.energy.IC2EnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.*;

/**
 * ============================================================================
 * ДВИЖОК ЭНЕРГОСЕТИ (CABLE NETWORK & ENERGY DISTRIBUTION)
 * ============================================================================
 * 
 * Как ток ходит по проводам:
 * 1. Источник (генератор, панель, батарея) передает энергию в кабель через метод receiveEnergy().
 * 2. Кабель не ждет следующего тика — он МГНОВЕННО запускает волновой алгоритм поиска потребителей (BFS)
 *    через метод `distributeToNetwork()`.
 * 3. Волна обходит соединенные провода и находит все активные энергоприемники (механизмы, хранилища).
 * 4. Энергия пропорционально и без задержек передается потребителям с учетом лимита провода (maxTransfer).
 * 5. Если потребители заполнены, остаток временно буферизуется во внутреннем хранилище провода
 *    и выталкивается в следующем тике в методе `tick()`.
 */
public class CableBlockEntity extends BlockEntity {
    // Максимальная пропускная способность кабеля за 1 тик (в FE, где 1 EU = 4 FE)
    private final int maxTransfer;
    
    // Внутренний буфер кабеля для сглаживания сетевых пиков
    private final IC2EnergyStorage energyStorage;

    public CableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState, int maxTransfer) {
        super(type, pos, blockState);
        this.maxTransfer = maxTransfer;
        this.energyStorage = new IC2EnergyStorage(maxTransfer, maxTransfer, maxTransfer, this::setChanged) {
            @Override
            public int receiveEnergy(int toReceive, boolean simulate) {
                if (level != null && !level.isClientSide && toReceive > 0) {
                    // [МГНОВЕННОЕ РАСПРЕДЕЛЕНИЕ] При поступлении тока сразу отправляем его по сети
                    int distributed = distributeToNetwork(toReceive, simulate);
                    if (distributed >= toReceive) {
                        return distributed;
                    }
                    // Если сеть потребила не весь объем, сохраняем остаток в буфер провода
                    int leftover = toReceive - distributed;
                    int stored = super.receiveEnergy(leftover, simulate);
                    return distributed + stored;
                }
                return super.receiveEnergy(toReceive, simulate);
            }
        };
    }

    public IEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    public int getMaxTransfer() {
        return maxTransfer;
    }

    /**
     * Основной алгоритм распределения энергии по соединенной кабельной сети.
     * 
     * @param amount   Количество энергии (в FE), которое нужно передать
     * @param simulate Режим симуляции (true - только проверка, false - реальная передача)
     * @return Фактически принятое сетью количество энергии
     */
    public int distributeToNetwork(int amount, boolean simulate) {
        if (level == null || level.isClientSide || amount <= 0) return 0;

        // 1. Находим все точки выхода энергии (конечные потребители)
        List<Endpoint> endpoints = findEndpoints();
        if (endpoints.isEmpty()) return 0;

        int totalAccepted = 0;
        int remaining = Math.min(amount, maxTransfer);

        // 2. Передаем ток в найденные потребители
        for (Endpoint ep : endpoints) {
            if (remaining <= 0) break;
            IEnergyStorage target = level.getCapability(Capabilities.EnergyStorage.BLOCK, ep.pos, ep.side);
            if (target != null && target.canReceive()) {
                int accepted = target.receiveEnergy(remaining, simulate);
                if (accepted > 0) {
                    totalAccepted += accepted;
                    remaining -= accepted;
                }
            }
        }
        return totalAccepted;
    }

    /**
     * Серверный тик кабеля: если в буфере провода осталась энергия, пытаемся протолкнуть её дальше.
     */
    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;

        if (energyStorage.getEnergyStored() > 0) {
            int toSend = Math.min(energyStorage.getEnergyStored(), maxTransfer);
            int sent = distributeToNetwork(toSend, false);
            if (sent > 0) {
                energyStorage.consumeEnergy(sent);
            }
        }
    }

    /**
     * Точка подключения потребителя: координаты блока и сторона, с которой подключен кабель.
     */
    public record Endpoint(BlockPos pos, Direction side) {}

    /**
     * Волновой алгоритм (BFS) для поиска всех потребителей, подключенных к этой ветке проводов.
     * Ограничение глубины поиска (maxSearch = 256) предотвращает лаги на огромных цепочках.
     */
    private List<Endpoint> findEndpoints() {
        List<Endpoint> endpoints = new ArrayList<>();
        Set<BlockPos> visitedCables = new HashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();

        queue.add(worldPosition);
        visitedCables.add(worldPosition);

        int maxSearch = 256; // Лимит просматриваемых блоков проводов за вызов

        while (!queue.isEmpty() && visitedCables.size() < maxSearch) {
            BlockPos current = queue.poll();

            // Проверяем всех 6 соседей текущего кабеля
            for (Direction dir : Direction.values()) {
                BlockPos neighborPos = current.relative(dir);
                BlockEntity neighborBe = level.getBlockEntity(neighborPos);

                if (neighborBe instanceof CableBlockEntity) {
                    // Если сосед - кабель, продолжаем распространение волны
                    if (visitedCables.add(neighborPos)) {
                        queue.add(neighborPos);
                    }
                } else if (neighborBe != null) {
                    // Если сосед - механизм, накопитель или трансформатор, проверяем возможность приема энергии
                    IEnergyStorage target = level.getCapability(Capabilities.EnergyStorage.BLOCK, neighborPos, dir.getOpposite());
                    if (target != null && target.canReceive()) {
                        endpoints.add(new Endpoint(neighborPos, dir.getOpposite()));
                    }
                }
            }
        }

        return endpoints;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Energy", energyStorage.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Energy")) energyStorage.deserializeNBT(registries, tag.getCompound("Energy"));
    }
}
