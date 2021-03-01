package org.dimdev.dimdoors.block.door.condition;

import com.google.gson.JsonObject;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class WorldMatchCondition implements Condition {
	private final RegistryKey<World> world;

	public WorldMatchCondition(RegistryKey<World> world) {
		this.world = world;
	}

	public static WorldMatchCondition fromJson(JsonObject json) {
		RegistryKey<World> key = RegistryKey.of(Registry.DIMENSION, new Identifier(json.getAsJsonPrimitive("world").getAsString()));
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
