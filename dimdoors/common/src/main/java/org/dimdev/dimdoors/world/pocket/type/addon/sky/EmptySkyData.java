package org.dimdev.dimdoors.world.pocket.type.addon.sky;

public enum EmptySkyData implements SkyData {
    INSTANCE;

    @Override
    public SkyDataType<?> type() {
        return SkyDataType.EMPTY;
    }
}
