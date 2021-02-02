package org.dimdev.dimdoors.pockets.virtual;

import com.mojang.serialization.*;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import org.dimdev.dimdoors.pockets.virtual.generator.ChunkGenerator;
import org.dimdev.dimdoors.pockets.virtual.generator.SchematicGenerator;
import org.dimdev.dimdoors.pockets.virtual.selection.DepthDependentSelector;
import org.dimdev.dimdoors.util.PocketGenerationParameters;
import org.dimdev.dimdoors.util.Weighted;
import org.dimdev.dimdoors.world.pocket.Pocket;

import java.util.function.Supplier;


// TODO: do something about getting correct Pocket sizes
public abstract class VirtualSingularPocket implements VirtualPocket {
	public static final Registry<VirtualSingularPocketType<? extends VirtualSingularPocket>> REGISTRY = FabricRegistryBuilder.from(new SimpleRegistry<VirtualSingularPocketType<? extends VirtualSingularPocket>>(RegistryKey.ofRegistry(new Identifier("dimdoors", "virtual_pocket_type")), Lifecycle.stable())).buildAndRegister();
	/*
	public static final Codec<VirtualPocket> CODEC = new Codec<VirtualPocket>() {
		@Override
		public <T> DataResult<Pair<VirtualPocket, T>> decode(DynamicOps<T> dynamicOps, T input) {
			Identifier id = new Identifier("dimdoors", Codec.STRING.decode(dynamicOps, dynamicOps.get(input, "virtual_type").getOrThrow(false, System.err::println)).getOrThrow(false, System.err::println).getFirst());
			return REGISTRY.get(id).getCodec().decode(dynamicOps, input).map(pair -> pair.mapFirst(virtualPocket -> (VirtualPocket) virtualPocket));
		}

		@Override
		public <T> DataResult<T> encode(VirtualPocket input, DynamicOps<T> ops, T prefix) {
			return null; // TODO: write encode function
		}
	};
	 */

	public static VirtualSingularPocket deserialize(CompoundTag tag) {
		Identifier id = Identifier.tryParse(tag.getString("type")); // TODO: return some NONE VirtualPocket if type cannot be found or deserialization fails.
		return REGISTRY.get(id).fromTag(tag);
	}

	public static CompoundTag serialize(VirtualSingularPocket virtualSingularPocket) {
		return virtualSingularPocket.toTag(new CompoundTag());
	}

	public abstract VirtualSingularPocket fromTag(CompoundTag tag);

	public CompoundTag toTag(CompoundTag tag) {
		return this.getType().toTag(tag);
	}

	public abstract Pocket prepareAndPlacePocket(PocketGenerationParameters parameters);

	public abstract VirtualSingularPocketType<? extends VirtualSingularPocket> getType();

	public abstract String getKey();


	public interface VirtualSingularPocketType<T extends VirtualSingularPocket> {
		VirtualSingularPocketType<SchematicGenerator> SCHEMATIC = register(new Identifier("dimdoors", SchematicGenerator.KEY), SchematicGenerator::new);
		VirtualSingularPocketType<ChunkGenerator> CHUNK = register(new Identifier("dimdoors", ChunkGenerator.KEY), ChunkGenerator::new);

		VirtualSingularPocketType<DepthDependentSelector> DEPTH_DEPENDENT = register(new Identifier("dimdoors", DepthDependentSelector.KEY), DepthDependentSelector::new);


		VirtualSingularPocket fromTag(CompoundTag tag);

		CompoundTag toTag(CompoundTag tag);

		static void register() {
		}

		static <U extends VirtualSingularPocket> VirtualSingularPocketType<U> register(Identifier id, Supplier<U> constructor) {
			return Registry.register(REGISTRY, id, new VirtualSingularPocketType<U>() {
				@Override
				public VirtualSingularPocket fromTag(CompoundTag tag) {
					return constructor.get().fromTag(tag);
				}

				@Override
				public CompoundTag toTag(CompoundTag tag) {
					tag.putString("type", id.toString());
					return tag;
				}
			});
		}
	}
}
