package org.dimdev.dimdoors.pockets.theme;

import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface Converter extends Function<BlockState, BlockState>, Predicate<BlockState> {
	Registry<ConverterType<? extends Converter>> REGISTRY = FabricRegistryBuilder.from(new SimpleRegistry<ConverterType<? extends Converter>>(RegistryKey.ofRegistry(new Identifier("dimdoors", "converter")), Lifecycle.stable(), null)).buildAndRegister();

	Converter NONE = new Converter() {
		@Override
		public Converter fromNbt(NbtCompound nbt) {
			return this;
		}

		@Override
		public ConverterType<? extends Converter> getType() {
			return ConverterType.NONE_CONVERTER_TYPE;
		}

		@Override
		public BlockState apply(BlockState blockState) {
			return blockState;
		}

		@Override
		public boolean test(BlockState blockState) {
			return true;
		}
	};

	static Converter deserialize(NbtCompound nbt) {
		Identifier id = Identifier.tryParse(nbt.getString("type"));
		return REGISTRY.getOrEmpty(id).orElse(ConverterType.NONE_CONVERTER_TYPE).fromNbt(nbt);
	}

	static NbtCompound serialize(Converter modifier) {
		return modifier.toNbt(new NbtCompound());
	}

	Converter fromNbt(NbtCompound nbt);

	default NbtCompound toNbt(NbtCompound nbt) {
		return this.getType().toNbt(nbt);
	}

	ConverterType<? extends Converter> getType();

	interface ConverterType<T extends Converter> {
		ConverterType<Converter> NONE_CONVERTER_TYPE = register(new Identifier("dimdoors", "none"), () -> NONE);
		ConverterType<TaggedConverter> TAGGED_CONVERTER_TYPE = register(new Identifier("dimdoors", TaggedConverter.KEY), TaggedConverter::new);

		Converter fromNbt(NbtCompound nbt);

		NbtCompound toNbt(NbtCompound nbt);

		static void register() {
			DimensionalDoorsInitializer.apiSubscribers.forEach(d -> d.registerConverters(REGISTRY));
		}

		static <U extends Converter> ConverterType<U> register(Identifier id, Supplier<U> factory) {
			return Registry.register(REGISTRY, id, new ConverterType<U>() {
				@Override
				public Converter fromNbt(NbtCompound nbt) {
					return factory.get().fromNbt(nbt);
				}

				@Override
				public NbtCompound toNbt(NbtCompound nbt) {
					nbt.putString("type", id.toString());
					return nbt;
				}
			});
		}
	}
}
