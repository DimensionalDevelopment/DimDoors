package org.dimdev.dimdoors.item.door.data.condition;

import java.util.Objects;
import java.util.function.Function;

import com.google.gson.JsonObject;
import com.mojang.serialization.Lifecycle;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;

import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;

public interface Condition {
	Registry<ConditionType<?>> REGISTRY = FabricRegistryBuilder.<ConditionType<?>, SimpleRegistry<ConditionType<?>>>from(new SimpleRegistry<>(RegistryKey.ofRegistry(DimensionalDoors.id("rift_data_condition")), Lifecycle.stable(), false)).buildAndRegister();

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
			DimensionalDoors.apiSubscribers.forEach(d -> d.registerConditionTypes(REGISTRY));
		}

		static <T extends Condition> ConditionType<T> register(String name, Function<JsonObject, T> fromJson) {
			return Registry.register(REGISTRY, DimensionalDoors.id(name), json -> fromJson.apply(json));
		}
	}
}
