package org.dimdev.dimdoors.item.door.data.condition;

import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;

import java.util.Objects;
import java.util.function.Function;

public interface Condition {
    ResourceKey<Registry<ConditionType<?>>> KEY = ResourceKey.createRegistryKey(DimensionalDoors.id("rift_data_condition"));
    Registry<ConditionType<?>> REGISTRY = DimensionalDoors.getSided().createRegistry(KEY);

    boolean matches(EntranceRiftBlockEntity rift);

    default JsonObject toJson(JsonObject json) {
        json.addProperty("type", getType().getId());
        this.toJsonInner(json);
        return json;
    }

    void toJsonInner(JsonObject json);

    ConditionType<?> getType();

    static Condition fromJson(JsonObject json) {
        ResourceLocation type = ResourceLocation.tryParse(json.getAsJsonPrimitive("type").getAsString());
        return Objects.requireNonNull(REGISTRY.get(type)).fromJson(json);
    }

    interface ConditionType<T extends Condition> {
        ConditionType<?> ALWAYS_TRUE = register("always_true", j -> AlwaysTrueCondition.INSTANCE);
        ConditionType<?> ALL = register("all", AllCondition::fromJson);
        ConditionType<?> ANY = register("any", AnyCondition::fromJson);
        ConditionType<?> INVERSE = register("inverse", InverseCondition::fromJson);
        ConditionType<?> WORLD_MATCH = register("world_match", WorldMatchCondition::fromJson);

        T fromJson(JsonObject json);

        default String getId() {
            return String.valueOf(REGISTRY.getKey(this));
        }

        static void register() {
        }

        static <T extends Condition> ConditionType<T> register(String name, Function<JsonObject, T> fromJson) {
            return DimensionalDoors.getSided().register(KEY, name, fromJson::apply);
        }
    }
}
