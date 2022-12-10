package org.dimdev.dimdoors.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.block.TrapdoorBlock;
import net.minecraft.sound.SoundEvent;

import org.dimdev.dimdoors.block.DoorSoundProvider;

@Mixin(TrapdoorBlock.class)
public class TrapDoorMixin implements DoorSoundProvider {
	@Shadow
	@Final
	private SoundEvent openSound;

	@Shadow
	@Final
	private SoundEvent closeSound;

	@Override
	public SoundEvent getOpenSound() {
		return openSound;
	}

	@Override
	public SoundEvent getCloseSound() {
		return closeSound;
	}
}
