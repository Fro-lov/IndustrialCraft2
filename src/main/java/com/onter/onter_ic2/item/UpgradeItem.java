package com.onter.onter_ic2.item;

import net.minecraft.world.item.Item;

public class UpgradeItem extends Item {
    public enum UpgradeType {
        OVERCLOCKER,
        ENERGY_STORAGE,
        TRANSFORMER
    }

    private final UpgradeType type;

    public UpgradeItem(UpgradeType type, Properties properties) {
        super(properties.stacksTo(16));
        this.type = type;
    }

    public UpgradeType getType() {
        return type;
    }
}
