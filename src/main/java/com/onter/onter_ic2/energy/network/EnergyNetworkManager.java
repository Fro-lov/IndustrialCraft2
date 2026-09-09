package com.onter.onter_ic2.energy.network;

import com.onter.onter_ic2.OnterIC2;
import com.onter.onter_ic2.block.cables.CableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@EventBusSubscriber(modid = OnterIC2.MODID)
public class EnergyNetworkManager {
    private static final Map<ResourceKey<Level>, List<EnergyNetwork>> DIMENSION_NETWORKS = new ConcurrentHashMap<>();

    private static List<EnergyNetwork> getNetworksForLevel(Level level) {
        return DIMENSION_NETWORKS.computeIfAbsent(level.dimension(), k -> new CopyOnWriteArrayList<>());
    }

    public static EnergyNetwork getOrCreateNetwork(Level level, BlockPos cablePos) {
        if (level.isClientSide) return null;

        List<EnergyNetwork> networks = getNetworksForLevel(level);

        for (EnergyNetwork net : networks) {
            if (net.isValid() && net.getCables().contains(cablePos)) {
                return net;
            }
        }

        // Check if adjacent to existing valid networks
        List<EnergyNetwork> adjacentNetworks = new ArrayList<>();
        for (Direction dir : Direction.values()) {
            BlockPos neighbor = cablePos.relative(dir);
            for (EnergyNetwork net : networks) {
                if (net.isValid() && net.getCables().contains(neighbor) && !adjacentNetworks.contains(net)) {
                    adjacentNetworks.add(net);
                }
            }
        }

        if (adjacentNetworks.isEmpty()) {
            // Create a fresh network
            EnergyNetwork newNet = new EnergyNetwork();
            newNet.addCable(cablePos);
            newNet.markDirty();
            networks.add(newNet);
            return newNet;
        } else {
            // Join first adjacent network and mark dirty
            EnergyNetwork primary = adjacentNetworks.get(0);
            primary.addCable(cablePos);
            primary.markDirty();

            // If connected multiple distinct networks together, merge them
            for (int i = 1; i < adjacentNetworks.size(); i++) {
                EnergyNetwork secondary = adjacentNetworks.get(i);
                primary.receiveEnergy(secondary.getNetworkBuffer(), false);
                secondary.invalidate();
                networks.remove(secondary);
            }
            return primary;
        }
    }

    public static void onCableAdded(Level level, BlockPos pos) {
        if (level == null || level.isClientSide) return;
        EnergyNetwork net = getOrCreateNetwork(level, pos);
        if (net != null) {
            net.markDirty();
        }
    }

    public static void onCableRemoved(Level level, BlockPos pos) {
        if (level == null || level.isClientSide) return;
        List<EnergyNetwork> networks = getNetworksForLevel(level);

        for (EnergyNetwork net : networks) {
            if (net.isValid() && net.getCables().contains(pos)) {
                net.removeCable(pos);
                net.markDirty();
                break;
            }
        }
    }

    public static void invalidateAt(Level level, BlockPos pos) {
        if (level == null || level.isClientSide) return;
        List<EnergyNetwork> networks = getNetworksForLevel(level);

        for (EnergyNetwork net : networks) {
            if (net.isValid() && net.getCables().contains(pos)) {
                net.markDirty();
            }
        }
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        for (Map.Entry<ResourceKey<Level>, List<EnergyNetwork>> entry : DIMENSION_NETWORKS.entrySet()) {
            ServerLevel level = event.getServer().getLevel(entry.getKey());
            if (level == null) continue;

            List<EnergyNetwork> list = entry.getValue();
            list.removeIf(net -> !net.isValid() || (net.getCables().isEmpty() && !net.isDirty()));

            for (EnergyNetwork net : list) {
                if (net.isValid()) {
                    net.tick(level);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof Level lvl && !lvl.isClientSide()) {
            DIMENSION_NETWORKS.remove(lvl.dimension());
        }
    }
}
