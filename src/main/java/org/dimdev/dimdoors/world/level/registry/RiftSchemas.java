package org.dimdev.dimdoors.world.level.registry;

import java.util.function.BiFunction;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import org.dimdev.dimdoors.world.level.registry.schema.Schema1;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.Util;

public class RiftSchemas {
	public static final DSL.TypeReference RIFT_DATA_TYPE_REF = () -> "rift_data";
	public static final int RIFT_DATA_VERSION = DimensionalRegistry.RIFT_DATA_VERSION;
	public static final BiFunction<Integer, Schema, Schema> EMPTY = Schema::new;
	public static final DataFixer DATA_FIXER = Util.make(new DataFixerBuilder(RIFT_DATA_VERSION), builder -> {
		builder.addSchema(1, Schema1::new);
		// TODO: add schemas if schema changes
	}).build(Runnable::run);

	public static NbtCompound update(int oldVersion, NbtCompound original) {
		return (NbtCompound) DATA_FIXER.update(RIFT_DATA_TYPE_REF, new Dynamic<>( NbtOps.INSTANCE, original), oldVersion, RIFT_DATA_VERSION).getValue();
	}
}
