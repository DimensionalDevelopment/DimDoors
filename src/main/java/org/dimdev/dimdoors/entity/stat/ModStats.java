package org.dimdev.dimdoors.entity.stat;

import org.dimdev.dimdoors.mixin.accessor.StatsAccessor;

import net.minecraft.stat.StatFormatter;
import net.minecraft.util.Identifier;

public class ModStats {
	public static final Identifier DEATHS_IN_POCKETS = StatsAccessor.invokeRegister("dimdoors:deaths_in_pocket", StatFormatter.DEFAULT);

	public static void init() {
		// just loads the class
	}
}
