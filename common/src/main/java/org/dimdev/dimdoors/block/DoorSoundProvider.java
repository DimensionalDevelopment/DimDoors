package org.dimdev.dimdoors.block;

public interface DoorSoundProvider {
	public static final DoorSoundProvider DUMMY = new DoorSoundProvider() {};

	// TODO: remove these two
//	public default SoundEvent getOpenSound() {
//		return SoundEvents.WOODEN_DOOR_OPEN;
//	}
//	public default SoundEvent getCloseSound() {
//		return SoundEvents.WOODEN_DOOR_CLOSE;
//	}
}
