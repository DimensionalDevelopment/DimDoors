package org.dimdev.dimdoors.pockets;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import org.dimdev.dimdoors.pockets.generator.SchematicGenerator;
import org.dimdev.dimdoors.pockets.selection.DepthDependentSelector;
import org.dimdev.dimdoors.util.PocketGenerationParameters;
import org.dimdev.dimdoors.util.Weighted;
import org.dimdev.dimdoors.world.pocket.Pocket;


// TODO: do something about getting correct Pocket sizes
public abstract class VirtualPocket implements Weighted<PocketGenerationParameters> {
	public static final Registry<VirtualPocketType<? extends VirtualPocket>> REGISTRY = FabricRegistryBuilder.from(new SimpleRegistry<VirtualPocketType<? extends VirtualPocket>>(RegistryKey.ofRegistry(new Identifier("dimdoors", "virtual_pocket_type")), Lifecycle.stable())).buildAndRegister();
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

	public abstract void init(String group);

	public abstract Pocket prepareAndPlacePocket(PocketGenerationParameters parameters);

	public abstract String toString();
	// TODO: are equals() and hashCode() necessary?

	public abstract VirtualPocketType<? extends VirtualPocket> getType();

	public abstract String getKey();

	public interface VirtualPocketType<T extends VirtualPocket> {
		VirtualPocketType<SchematicGenerator> SCHEMATIC = register(new Identifier("dimdoors", SchematicGenerator.KEY), SchematicGenerator.CODEC);

		VirtualPocketType<DepthDependentSelector> DEPTH_DEPENDENT = register(new Identifier("dimdoors", DepthDependentSelector.KEY), DepthDependentSelector.CODEC);



		Codec<T> getCodec();

		static void register() {
		}

		static <T extends VirtualPocket> VirtualPocketType<T> register(Identifier id, Codec<T> codec) {
			return Registry.register(REGISTRY, id, () -> codec);
		}
	}
}
