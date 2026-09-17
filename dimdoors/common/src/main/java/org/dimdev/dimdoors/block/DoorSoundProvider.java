package org.dimdev.dimdoors.block;

import net.minecraft.world.level.block.state.properties.BlockSetType;

public interface DoorSoundProvider {
    public static final DoorSoundProvider DUMMY = new DoorSoundProvider() {};

    default BlockSetType getSetType() {
    return BlockSetType.IRON;
    }
}
