package org.dimdev.dimdoors.item.door.data.condition;

import com.google.gson.JsonObject;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;

import java.util.Objects;
import java.util.function.Function;

public interface Condition {
	Registrar<ConditionType<?>> REGISTRY = RegistrarManager.get(DimensionalDoors.MOD_ID).<ConditionType<?>>builder(DimensionalDoors.id("rift_data_condition")).build();

	boolean matches(EntranceRiftBlockEntity rift);

	default JsonObject toJson(JsonObject json) {
		json.addProperty("type", getType().getId());
		this.toJsonInner(json);
		return json;
	}

	void toJsonInner(JsonObject json);

	ConditionType<?> getType();

	static Condition fromJson(JsonObject json) {
		ResourceLocation type = new ResourceLocation(json.getAsJsonPrimitive("type").getAsString());
		return Objects.requireNonNull(REGISTRY.get(type)).fromJson(json);
	}

	interface ConditionType<T extends Condition> {
		RegistrySupplier<ConditionType<?>> ALWAYS_TRUE = register("always_true", j -> AlwaysTrueCondition.INSTANCE);
		RegistrySupplier<ConditionType<?>> ALL = register("all", AllCondition::fromJson);
		RegistrySupplier<ConditionType<?>> ANY = register("any", AnyCondition::fromJson);
		RegistrySupplier<ConditionType<?>> INVERSE = register("inverse", InverseCondition::fromJson);
		RegistrySupplier<ConditionType<?>> WORLD_MATCH = register("world_match", WorldMatchCondition::fromJson);

		T fromJson(JsonObject json);

		default String getId() {
			return String.valueOf(REGISTRY.getId(this));
		}

		static void register() {}

		static <T extends Condition> RegistrySupplier<ConditionType<?>> register(String name, Function<JsonObject, T> fromJson) {
			return REGISTRY.register(DimensionalDoors.id(name), () -> fromJson::apply);
		}
	}
}
