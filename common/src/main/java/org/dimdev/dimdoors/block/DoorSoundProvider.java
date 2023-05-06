package org.dimdev.dimdoors.block;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.state.properties.BlockSetType;

public interface DoorSoundProvider {
	public static final DoorSoundProvider DUMMY = new DoorSoundProvider() {};

	// TODO: remove these two
	public default SoundEvent getOpenSound() {
		return SoundEvents.WOODEN_DOOR_OPEN;
	}
	public default SoundEvent getCloseSound() {
		return SoundEvents.WOODEN_DOOR_CLOSE;
	}

	default BlockSetType getSetType() {
		return BlockSetType.IRON;
	}
}
