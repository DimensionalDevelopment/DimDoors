package org.dimdev.dimdoors.block.door.data.condition;

import com.google.gson.JsonObject;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;

public record WorldMatchCondition(RegistryKey<World> world) implements Condition {
	public static WorldMatchCondition fromJson(JsonObject json) {
		RegistryKey<World> key = RegistryKey.of(RegistryKeys.WORLD, new Identifier(json.getAsJsonPrimitive("world").getAsString()));
		return new WorldMatchCondition(key);
	}

	@Override
	public boolean matches(EntranceRiftBlockEntity rift) {
		//noinspection ConstantConditions
		return rift.getWorld().getRegistryKey() == this.world;
	}

	@Override
	public void toJsonInner(JsonObject json) {
		json.addProperty("world", world.getValue().toString());
	}

	@Override
	public ConditionType<?> getType() {
		return ConditionType.WORLD_MATCH;
	}
}
