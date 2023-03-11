package org.dimdev.dimdoors.mixin.accessor;

import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Direction.class)
public interface DirectionAccessor {
	@Accessor("BY_2D_DATA")
	static Direction[] getHorizontal() {
		throw new AssertionError();
	}
}
