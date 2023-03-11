package org.dimdev.dimdoors.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.DoorBlock;
import org.dimdev.dimdoors.block.DoorSoundProvider;

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
