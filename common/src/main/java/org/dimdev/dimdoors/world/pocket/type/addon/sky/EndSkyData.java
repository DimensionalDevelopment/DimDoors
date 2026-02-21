package org.dimdev.dimdoors.world.pocket.type.addon.sky;

public enum EndSkyData implements SkyData {
    INSTANCE;

    @Override
    public SkyDataType<?> type() {
        return SkyDataType.END;
    }
}
