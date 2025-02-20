package org.dimdev.dimdoors.world.pocket.type;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.dimdev.dimdoors.pockets.generator.LazyPocketGenerator;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;
import org.dimdev.dimdoors.world.pocket.type.addon.DyeableAddon;
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon;

import java.util.Map;

public class PrivatePocket extends LazyGenerationPocket implements DyeableAddon.DyeablePocket {
	public static String KEY = "private_pocket";

	public static final MapCodec<PrivatePocket> CODEC = RecordCodecBuilder.mapCodec(instance -> lazyFields(instance).apply(instance, PrivatePocket::new));

	public PrivatePocket(int id, ResourceKey<Level> world, int range, BoundingBox box, VirtualLocation virtualLocation, Map<ResourceLocation, PocketAddon> addons, LazyPocketGenerator generator, int toBeGennedChunkCount) {
		super(id, world, range, box, virtualLocation, addons, generator, toBeGennedChunkCount);
	}

	public PrivatePocket() {
		super();
	}

	public static PrivatePocketBuilder builderPrivatePocket() {
		return new PrivatePocketBuilder();
	}

	public static class PrivatePocketBuilder extends PocketBuilder<PrivatePocketBuilder, PrivatePocket> implements DyeableAddon.DyeablePocketBuilder<PrivatePocketBuilder> {
		public static final MapCodec<PrivatePocketBuilder> CODEC = RecordCodecBuilder.mapCodec(instance -> commonPocketBuilderFields(instance).apply(instance, PrivatePocketBuilder::configure));

		protected PrivatePocketBuilder() {
			super();
		}

		@Override
		public void initAddons() {
			super.initAddons();
			addAddon(new DyeableAddon.DyeableBuilderAddon());
			this.dyeColor(PocketColor.WHITE);
		}

		@Override
		public AbstractPocketType<PrivatePocket, PrivatePocket.PrivatePocketBuilder> getType() {
			return AbstractPocketType.PRIVATE_POCKET.get();
		}

		private static PrivatePocket.PrivatePocketBuilder configure(Vec3i origin, Vec3i size, VirtualLocation virtualLocation, int range, Map<ResourceLocation, PocketAddon.PocketBuilderAddon<?, ?>> addons) {
			return builderPrivatePocket().offsetOrigin(origin).expand(size).range(range).virtualLocation(virtualLocation).addons(addons);
		}
	}

	@Override
	public AbstractPocketType<?, ?> getType() {
		return AbstractPocketType.PRIVATE_POCKET.get();
	}

	public static String getKEY() {
		return KEY;
	}
}
