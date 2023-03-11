package org.dimdev.dimdoors.mixin.accessor;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Stats.class)
public interface StatsAccessor {
	@Invoker
	static ResourceLocation invokeRegister(String string, StatFormatter statFormatter) {
		throw new UnsupportedOperationException();
	}
}
