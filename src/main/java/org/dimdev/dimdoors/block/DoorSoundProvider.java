package org.dimdev.dimdoors.block;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public interface DoorSoundProvider {
	public static final DoorSoundProvider DUMMY = new DoorSoundProvider() {};

	public default SoundEvent getOpenSound() {
		return SoundEvents.WOODEN_DOOR_OPEN;
	}
	public default SoundEvent getCloseSound() {
		return SoundEvents.WOODEN_DOOR_CLOSE;
	}
}
