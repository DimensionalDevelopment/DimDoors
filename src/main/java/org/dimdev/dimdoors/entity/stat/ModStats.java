package org.dimdev.dimdoors.entity.stat;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import org.dimdev.dimdoors.mixin.accessor.StatsAccessor;

public class ModStats {
	public static final ResourceLocation DEATHS_IN_POCKETS = StatsAccessor.invokeRegister("dimdoors:deaths_in_pocket", StatFormatter.DEFAULT);
	public static final ResourceLocation TIMES_SENT_TO_LIMBO = StatsAccessor.invokeRegister("dimdoors:times_sent_to_limbo", StatFormatter.DEFAULT);
	public static final ResourceLocation TIMES_TELEPORTED_BY_MONOLITH = StatsAccessor.invokeRegister("dimdoors:times_teleported_by_monolith", StatFormatter.DEFAULT);
	public static final ResourceLocation TIMES_BEEN_TO_DUNGEON = StatsAccessor.invokeRegister("dimdoors:times_been_to_dungeon", StatFormatter.DEFAULT);
	public static void init() {
		// just loads the class
	}
}
