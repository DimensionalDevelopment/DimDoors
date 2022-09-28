package org.dimdev.dimdoors.mixin.accessor;

import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Stats.class)
public interface StatsAccessor {
	@Invoker
	static Identifier invokeRegister(String string, StatFormatter statFormatter) {
		throw new UnsupportedOperationException();
	}
}
