package org.dimdev.dimdoors.entity.mask;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum MaskMode implements StringRepresentable {
    GUARD("guard"),
    PATROL("patrol"),
    WANDER("wander"),
    CHASE("chase"),
    STUNNED("stunned");

    private final String name;

    MaskMode(String name) {
        this.name = name;
    }

    public boolean isPassive() {
        return this == GUARD || this == PATROL || this == WANDER;
    }

    @Override
    public @NotNull String getSerializedName() {
        return name;
    }
}
