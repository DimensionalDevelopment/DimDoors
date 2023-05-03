package org.dimdev.dimdoors.block;

import net.minecraft.block.BlockSetType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public interface DoorSoundProvider {
	public static final DoorSoundProvider DUMMY = new DoorSoundProvider() {};

	// TODO: remove these two
	public default SoundEvent getOpenSound() {
		return SoundEvents.BLOCK_WOODEN_DOOR_OPEN;
	}
	public default SoundEvent getCloseSound() {
		return SoundEvents.BLOCK_WOODEN_DOOR_CLOSE;
	}

	default BlockSetType getSetType() {
		return BlockSetType.IRON;
	}
}
