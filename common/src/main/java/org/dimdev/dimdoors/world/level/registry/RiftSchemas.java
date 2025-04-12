package org.dimdev.dimdoors.world.level.registry;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.Util;
import org.dimdev.dimdoors.world.level.registry.schema.Schema1;

import java.util.function.BiFunction;

public class RiftSchemas {
	public static final DSL.TypeReference RIFT_DATA_TYPE_REF = () -> "rift_data";
	public static final int RIFT_DATA_VERSION = DimensionalRegistry.RIFT_DATA_VERSION;
	public static final BiFunction<Integer, Schema, Schema> EMPTY = Schema::new;
	public static final DataFixer DATA_FIXER = Util.make(new DataFixerBuilder(RIFT_DATA_VERSION), builder -> {
		builder.addSchema(1, Schema1::new);
//		builder.addSchema(2, Schema2::new); TODO: Determine what my changes in 1.21 causes.
		// TODO: add schemas if schema changes
	}).buildUnoptimized();

	public static <T> Dynamic<T> update(int oldVersion, Dynamic<T> original) {
		return DATA_FIXER.update(RIFT_DATA_TYPE_REF, original, oldVersion, RIFT_DATA_VERSION);
	}
}
