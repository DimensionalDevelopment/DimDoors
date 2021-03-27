package org.dimdev.dimdoors.block.door.data.condition;

import java.util.Objects;
import java.util.function.Function;

import com.google.gson.JsonObject;
import com.mojang.serialization.Lifecycle;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;

public interface Condition {
	Registry<ConditionType<?>> REGISTRY = FabricRegistryBuilder.<ConditionType<?>, SimpleRegistry<ConditionType<?>>>from(new SimpleRegistry<>(RegistryKey.ofRegistry(new Identifier("dimdoors", "rift_data_condition")), Lifecycle.stable())).buildAndRegister();

	boolean matches(EntranceRiftBlockEntity rift);

	default JsonObject toJson(JsonObject json) {
		json.addProperty("type", getType().getId());
		this.toJsonInner(json);
		return json;
	}

	void toJsonInner(JsonObject json);

	ConditionType<?> getType();

	static Condition fromJson(JsonObject json) {
		Identifier type = new Identifier(json.getAsJsonPrimitive("type").getAsString());
		return Objects.requireNonNull(REGISTRY.get(type)).fromJson(json);
	}

	interface ConditionType<T extends Condition> {
		ConditionType<AlwaysTrueCondition> ALWAYS_TRUE = register("always_true", j -> AlwaysTrueCondition.INSTANCE);
		ConditionType<AllCondition> ALL = register("all", AllCondition::fromJson);
		ConditionType<AnyCondition> ANY = register("any", AnyCondition::fromJson);
		ConditionType<InverseCondition> INVERSE = register("inverse", InverseCondition::fromJson);
		ConditionType<WorldMatchCondition> WORLD_MATCH = register("world_match", WorldMatchCondition::fromJson);

		T fromJson(JsonObject json);

		default String getId() {
			return String.valueOf(REGISTRY.getId(this));
		}

		static void register() {
			DimensionalDoorsInitializer.apiSubscribers.forEach(d -> d.registerConditionTypes(REGISTRY));
		}

		static <T extends Condition> ConditionType<T> register(String name, Function<JsonObject, T> fromJson) {
			return Registry.register(REGISTRY, new Identifier("dimdoors", name), json -> fromJson.apply(json));
		}
	}
}
