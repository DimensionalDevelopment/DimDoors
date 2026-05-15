package org.dimdev.dimdoors.item.door.data.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;

public record BiomeCondition(ResourceKey<Biome> biome) implements Condition {
    public static final MapCodec<BiomeCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(ResourceKey.codec(Registries.BIOME).fieldOf("biome").forGetter(BiomeCondition::biome)).apply(instance, BiomeCondition::new));

    @Override
    public boolean matches(EntranceRiftBlockEntity rift) {
        return rift.getLevel().getBiome(rift.getBlockPos()).is(biome);
    }

    @Override
    public ConditionType<?> getType() {
        return ConditionType.BIOME;
    }
}
