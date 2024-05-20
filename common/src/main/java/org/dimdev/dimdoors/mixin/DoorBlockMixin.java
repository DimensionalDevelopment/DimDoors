package org.dimdev.dimdoors.mixin;

import net.minecraft.world.level.block.DoorBlock;
import org.dimdev.dimdoors.block.DoorSoundProvider;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(DoorBlock.class)
public abstract class DoorBlockMixin implements DoorSoundProvider {

//	@Shadow
//	@Final
//	private BlockSetType type;

//	@Override
//	public SoundEvent getOpenSound() {
//		return .type.doorOpen();
//	}
//
//	@Override
//	public SoundEvent getCloseSound() {
//		return this.type.doorClose();
//	}
//
//	@Override
//	public BlockSetType getSetType() {
//		return this.type;
//	}
}
