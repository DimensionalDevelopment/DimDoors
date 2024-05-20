package org.dimdev.dimdoors.item.door.data.condition;

import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;

public record WorldMatchCondition(ResourceKey<Level> world) implements Condition {
	public static WorldMatchCondition fromJson(JsonObject json) {
		ResourceKey<Level> key = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(json.getAsJsonPrimitive("world").getAsString()));
		return new WorldMatchCondition(key);
	}

	@Override
	public boolean matches(EntranceRiftBlockEntity rift) {
		//noinspection ConstantConditions
		return rift.getLevel().dimension().equals(this.world);
	}

	@Override
	public void toJsonInner(JsonObject json) {
		json.addProperty("world", world.location().toString());
	}

	@Override
	public ConditionType<?> getType() {
		return ConditionType.WORLD_MATCH.get();
	}
}
