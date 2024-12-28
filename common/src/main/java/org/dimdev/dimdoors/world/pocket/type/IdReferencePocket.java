package org.dimdev.dimdoors.world.pocket.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.PocketDirectory;

public class IdReferencePocket extends AbstractPocket {
	public static final MapCodec<IdReferencePocket> CODEC = RecordCodecBuilder.mapCodec(instance ->
			AbstractPocket.commonCodecFields(instance)
					.and(Codec.INT.fieldOf("referenced_id").forGetter(IdReferencePocket::getReferencedId)
	).apply(instance, IdReferencePocket::new));

	public static String KEY = "id_reference";

	protected int referencedId;

	public IdReferencePocket(int id, ResourceKey<Level> world, int referencedId) {
		super(id, world);
		this.referencedId = referencedId;
	}

	public IdReferencePocket() {}

	public int getReferencedId() {
		return referencedId;
	}

	@Override
	public V fromNbt(CompoundTag nbt) {
		super.fromNbt(nbt);

		this.referencedId = nbt.getInt("referenced_id");

		return this;
	}

	@Override
	public CompoundTag toNbt(CompoundTag nbt) {
		nbt = super.toNbt(nbt);

		nbt.putInt("referenced_id", referencedId);

		return nbt;
	}

	@Override
	public AbstractPocketType<IdReferencePocket, IdReferencePocketBuilder> getType() {
		return AbstractPocketType.ID_REFERENCE.get();
	}

	@Override
	public Pocket getReferencedPocket() {
		return getReferencedPocket(DimensionalRegistry.getPocketDirectory(getWorld()));
	}

	@Override
	public Pocket getReferencedPocket(PocketDirectory directory) {
		return directory.getPocket(referencedId);
	}

	public static IdReferencePocketBuilder builder() {
		return new IdReferencePocketBuilder();
	}

	public static class IdReferencePocketBuilder extends AbstractPocketBuilder<IdReferencePocketBuilder, IdReferencePocket> {
		public static final MapCodec<IdReferencePocketBuilder> CODEC = RecordCodecBuilder.mapCodec(instance ->
				AbstractPocketBuilder.commonCodecFields(instance)
						.and(Codec.INT.fieldOf("referenced_id").forGetter(a -> a.referencedId)
						).apply(instance, IdReferencePocketBuilder::configure));

		private int referencedId = Integer.MIN_VALUE;

		@Override
		public IdReferencePocket build() {
			IdReferencePocket pocket = super.build();
			pocket.referencedId = referencedId;
			return pocket;
		}

		@Override
		public IdReferencePocketBuilder fromNbt(CompoundTag nbt) {
			if (nbt.contains("referenced_id", Tag.TAG_INT)) referencedId = nbt.getInt("referenced_id");
			return this;
		}

		@Override
		public CompoundTag toNbt(CompoundTag nbt) {
			if (referencedId != Integer.MIN_VALUE) nbt.putInt("referenced_id", referencedId);
			return nbt;
		}

		@Override
		public AbstractPocketType<IdReferencePocket, IdReferencePocketBuilder> getType() {
			return AbstractPocketType.ID_REFERENCE.get();
		}

		public IdReferencePocketBuilder referencedId(int referencedId) {
			this.referencedId = referencedId;
			return this;
		}

		private static IdReferencePocketBuilder configure(int id, ResourceKey<Level> world, int referenceId) {
			return new IdReferencePocketBuilder().id(id).world(world).referencedId(referenceId);
		}
	}
}
