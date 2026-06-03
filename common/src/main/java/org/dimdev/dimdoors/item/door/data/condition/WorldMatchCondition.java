package org.dimdev.dimdoors.item.door.data.condition;

import com.google.gson.JsonObject;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;

public record WorldMatchCondition(ResourceKey<Level> world) implements Condition {
    public static MapCodec<WorldMatchCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(Level.RESOURCE_KEY_CODEC.fieldOf("world").forGetter(WorldMatchCondition::world)).apply(instance, WorldMatchCondition::new));
    @Override
    public boolean matches(EntranceRiftBlockEntity rift) {
        return rift.getLevel().dimension().equals(this.world);
    }

    @Override
    public ConditionType<?> getType() {
        return ConditionType.WORLD_MATCH;
    }
}