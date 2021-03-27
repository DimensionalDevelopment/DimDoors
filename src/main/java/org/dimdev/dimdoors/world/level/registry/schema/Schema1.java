package org.dimdev.dimdoors.world.level.registry.schema;

import java.util.Map;
import java.util.function.Supplier;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import org.dimdev.dimdoors.world.level.registry.RiftSchemas;

public class Schema1 extends Schema {
	public Schema1(int versionKey, Schema parent) {
		super(versionKey, parent);
	}

	@Override
	public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> entityTypes, Map<String, Supplier<TypeTemplate>> blockEntityTypes) {
		schema.registerType(false, RiftSchemas.RIFT_DATA_TYPE_REF, DSL::remainder);
	}
}
