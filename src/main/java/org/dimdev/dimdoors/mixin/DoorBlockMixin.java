package org.dimdev.dimdoors.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.block.BlockSetType;
import net.minecraft.block.DoorBlock;
import net.minecraft.sound.SoundEvent;

import org.dimdev.dimdoors.block.DoorSoundProvider;

@Mixin(DoorBlock.class)
public abstract class DoorBlockMixin implements DoorSoundProvider {

	@Shadow
	@Final
	private BlockSetType blockSetType;

	@Override
	public SoundEvent getOpenSound() {
		return this.blockSetType.doorOpen();
	}

	@Override
	public SoundEvent getCloseSound() {
		return this.blockSetType.doorClose();
	}

	@Override
	public BlockSetType getSetType() {
		return this.blockSetType;
	}
}
