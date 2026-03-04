package org.dimdev.dimdoors.entity.mask;

import net.minecraft.util.StringRepresentable;

public enum MaskMode implements StringRepresentable {
    GUARD("guard"),
    PATROL("patrol"),
    WANDER("wander"),
    CHASE("chase"),
    SPOTTING("spotting");

    private final String name;

    MaskMode(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
