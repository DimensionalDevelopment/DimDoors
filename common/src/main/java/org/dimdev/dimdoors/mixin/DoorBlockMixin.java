package org.dimdev.dimdoors.mixin;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import org.dimdev.dimdoors.block.DoorSoundProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DoorSoundProvider.class)
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
