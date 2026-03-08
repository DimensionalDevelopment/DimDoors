package org.dimdev.dimdoors.rift;

import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.jetbrains.annotations.NotNull;

public class RiftUtils {
    public static <T extends RiftBlockEntity> T registerFunction(@NotNull T riftBlockEntity) {
        riftBlockEntity.register();
        return riftBlockEntity;
    }
}
