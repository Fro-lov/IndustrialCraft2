package com.onter.onter_ic2.energy;

import net.minecraft.network.chat.Component;

public enum EnergyPriority {
    HIGH(2, "ic2.priority.high", 0xFF5555, "🔴"),    // Critical / Machines
    NORMAL(1, "ic2.priority.normal", 0xFFFF55, "🟡"), // Storage / BatBox
    LOW(0, "ic2.priority.low", 0x55FF55, "🟢");       // Dump / Mass Fab

    private final int level;
    private final String translationKey;
    private final int color;
    private final String icon;

    EnergyPriority(int level, String translationKey, int color, String icon) {
        this.level = level;
        this.translationKey = translationKey;
        this.color = color;
        this.icon = icon;
    }

    public int getLevel() {
        return level;
    }

    public int getColor() {
        return color;
    }

    public String getIcon() {
        return icon;
    }

    public Component getDisplayName() {
        return Component.translatable(translationKey);
    }

    public EnergyPriority next() {
        return switch (this) {
            case HIGH -> NORMAL;
            case NORMAL -> LOW;
            case LOW -> HIGH;
        };
    }

    public static EnergyPriority fromLevel(int level) {
        for (EnergyPriority p : values()) {
            if (p.level == level) return p;
        }
        return NORMAL;
    }
}
