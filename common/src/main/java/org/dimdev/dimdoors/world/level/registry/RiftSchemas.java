package org.dimdev.dimdoors.world.level.registry;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import org.dimdev.dimdoors.world.level.registry.schema.Schema1;

import java.util.function.BiFunction;

public class RiftSchemas {
	public static final DSL.TypeReference RIFT_DATA_TYPE_REF = () -> "rift_data";
	public static final int RIFT_DATA_VERSION = DimensionalRegistry.RIFT_DATA_VERSION;
	public static final BiFunction<Integer, Schema, Schema> EMPTY = Schema::new;
	public static final DataFixer DATA_FIXER = Util.make(new DataFixerBuilder(RIFT_DATA_VERSION), builder -> {
		builder.addSchema(1, Schema1::new);
		// TODO: add schemas if schema changes
	}).buildUnoptimized();

	public static CompoundTag update(int oldVersion, CompoundTag original) {
		return (CompoundTag) DATA_FIXER.update(RIFT_DATA_TYPE_REF, new Dynamic<>( NbtOps.INSTANCE, original), oldVersion, RIFT_DATA_VERSION).getValue();
	}
}
