package org.dimdev.dimdoors.world.pocket.type;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.dimdev.dimdoors.pockets.generator.LazyPocketGenerator;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;
import org.dimdev.dimdoors.world.pocket.type.addon.DyeableAddon;
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon;

import java.util.List;

public class PrivatePocket extends LazyGenerationPocket implements DyeableAddon.DyeablePocket {
	public static String KEY = "private_pocket";

	public PrivatePocket(int id, ResourceKey<Level> world, int range, BoundingBox box, VirtualLocation virtualLocation, List<PocketAddon> addons, LazyPocketGenerator generator, int toBeGennedChunkCount) {
		super(id, world, range, box, virtualLocation, addons, generator, toBeGennedChunkCount);
	}

	public static PrivatePocketBuilder<?, PrivatePocket> builderPrivatePocket() {
		return new PrivatePocketBuilder<>(AbstractPocketType.PRIVATE_POCKET.get());
	}

	public static class PrivatePocketBuilder<P extends PrivatePocketBuilder<P, T>, T extends PrivatePocket> extends PocketBuilder<P, T> implements DyeableAddon.DyeablePocketBuilder<P> {
		protected PrivatePocketBuilder(AbstractPocketType<T> type) {
			super(type);
		}

		@Override
		public void initAddons() {
			super.initAddons();
			addAddon(new DyeableAddon.DyeableBuilderAddon());
			this.dyeColor(PocketColor.WHITE);
		}
	}

	@Override
	public AbstractPocketType<?> getType() {
		return AbstractPocketType.PRIVATE_POCKET.get();
	}

	public static String getKEY() {
		return KEY;
	}
}
