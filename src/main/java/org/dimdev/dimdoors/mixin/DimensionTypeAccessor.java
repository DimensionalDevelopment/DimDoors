package org.dimdev.dimdoors.mixin;

import java.util.OptionalLong;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.util.Identifier;
import net.minecraft.world.dimension.DimensionType;

@Mixin(DimensionType.class)
public interface DimensionTypeAccessor {
	@Invoker("<init>")
	static DimensionType invokeInit(OptionalLong fixedTime, boolean hasSkylight, boolean hasCeiling, boolean ultrawarm, boolean natural, double coordinateScale, boolean piglinSafe, boolean bedWorks, boolean respawnAnchorWorks, boolean hasRaids, int logicalHeight, Identifier infiniburn, Identifier skyProperties, float ambientLight) {
		throw new AssertionError();
	}
}
