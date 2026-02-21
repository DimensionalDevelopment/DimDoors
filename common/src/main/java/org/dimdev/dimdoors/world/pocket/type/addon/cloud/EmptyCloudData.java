package org.dimdev.dimdoors.world.pocket.type.addon.cloud;

public enum EmptyCloudData implements CloudData {
    INSTANCE;

    @Override
    public CloudDataType<?> type() {
        return CloudDataType.EMPTY;
    }
}
