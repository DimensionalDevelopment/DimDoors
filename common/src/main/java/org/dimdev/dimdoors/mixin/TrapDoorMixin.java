package org.dimdev.dimdoors.mixin;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import org.dimdev.dimdoors.block.DoorSoundProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TrapDoorBlock.class)
public class TrapDoorMixin implements DoorSoundProvider {

	@Shadow
	@Final
	private BlockSetType type;

	@Override
	public SoundEvent getOpenSound() {
		return this.type.doorOpen();
	}

	@Override
	public SoundEvent getCloseSound() {
		return this.type.doorClose();
	}

	@Override
	public BlockSetType getSetType() {
		return this.type;
	}
}
