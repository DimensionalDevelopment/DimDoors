package org.dimdev.dimdoors.item.door.data.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.ModRegistries;
import org.dimdev.dimdoors.ModRegistryKeys;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;

public interface  Condition {
    Codec<Condition> CODEC = Codec.lazyInitialized(() -> ModRegistries.CONDITION_TYPE.byNameCodec().dispatch(Condition::getType, ConditionType::codec));

    static WorldMatchCondition level(ResourceKey<Level> key) {
        return new WorldMatchCondition(key);
    }

    static InverseCondition not(Condition condition) {
        return new InverseCondition(condition);
    }

    static AlwaysTrueCondition alwaysTrue() {
        return AlwaysTrueCondition.INSTANCE;
    }

    boolean matches(EntranceRiftBlockEntity rift);

    ConditionType<?> getType();

    public record ConditionType<T extends Condition>(MapCodec<T> codec) {
        public static final ConditionType<?> ALWAYS_TRUE = register("always_true", AlwaysTrueCondition.CODEC);
        public static final ConditionType<?> ALL = register("all", AllCondition.CODEC);
        public static final ConditionType<?> ANY = register("any", AnyCondition.CODEC);
        public static final ConditionType<?> INVERSE = register("inverse", InverseCondition.CODEC);
        public static final ConditionType<?> WORLD_MATCH = register("world_match", WorldMatchCondition.CODEC);
        public static final ConditionType<?> BIOME = register("biome", BiomeCondition.CODEC);
        public static final ConditionType<?> HEIGHT = register("height", HeightCondition.CODEC);
        public static final ConditionType<?> WATERLOGGED = register("waterlogged", WaterloggedCondition.CODEC);
        public static final ConditionType<?> LIGHT_LEVEL = register("light_level", LightLevelCondition.CODEC);

        public static void register() {
        }

        static <T extends Condition> ConditionType<T> register(String name, MapCodec<T> codec) {
            return DimensionalDoors.getSided().register(ModRegistryKeys.CONDITION_TYPE, name, new ConditionType<>(codec));
        }
    }
}
