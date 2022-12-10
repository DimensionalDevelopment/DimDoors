package org.dimdev.dimdoors.mixin;

import net.minecraft.block.DoorBlock;
import net.minecraft.sound.SoundEvent;
import org.dimdev.dimdoors.block.DoorSoundProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DoorBlock.class)
public abstract class DoorBlockMixin implements DoorSoundProvider {
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
