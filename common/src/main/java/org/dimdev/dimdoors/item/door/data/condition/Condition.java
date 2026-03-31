package org.dimdev.dimdoors.item.door.data.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;

public interface Condition {
    Codec<Condition> CODEC = Codec.lazyInitialized(() -> ConditionType.CODEC.dispatch(Condition::getType, ConditionType::codec));

    boolean matches(EntranceRiftBlockEntity rift);

    ConditionType<?> getType();

    public static record ConditionType<T extends Condition>(MapCodec<T> codec) {
        public static final Registrar<ConditionType<?>> REGISTRY = RegistrarManager.get(DimensionalDoors.MOD_ID).<ConditionType<?>>builder(DimensionalDoors.id("rift_data_condition")).build();
        public static final Codec<ConditionType<?>> CODEC = ResourceLocation.CODEC.xmap(REGISTRY::get, REGISTRY::getId);

		public static final RegistrySupplier<ConditionType<?>> ALWAYS_TRUE = register("always_true", MapCodec.unit(AlwaysTrueCondition.INSTANCE));
		public static final RegistrySupplier<ConditionType<?>> ALL = register("all", AllCondition.CODEC);
		public static final RegistrySupplier<ConditionType<?>> ANY = register("any", AnyCondition.CODEC);
		public static final RegistrySupplier<ConditionType<?>> INVERSE = register("inverse", InverseCondition.CODEC);
		public static final RegistrySupplier<ConditionType<?>> WORLD_MATCH = register("world_match", WorldMatchCondition.CODEC);

        public static void register() {}

		static <T extends Condition> RegistrySupplier<ConditionType<?>> register(String name, MapCodec<T> codec) {
			return REGISTRY.register(DimensionalDoors.id(name), () -> new ConditionType<T>(codec));
		}
	}
}
