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

public class IdReferencePocket extends AbstractPocket<IdReferencePocket> {
	public static MapCodec<IdReferencePocket> CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(instance).and(Codec.INT.fieldOf("referenced_id").forGetter(a -> a.referencedId)).apply(instance, IdReferencePocket::new));

	public static String KEY = "id_reference";

	protected int referencedId;

	public IdReferencePocket() {}

	public IdReferencePocket(Integer integer, ResourceKey<Level> levelResourceKey, Integer integer2) {
		super(integer, levelResourceKey);
		this.referencedId = integer2;
	}

	@Override
	public IdReferencePocket fromNbt(CompoundTag nbt) {
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
	public AbstractPocketType<IdReferencePocket> getType() {
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
		return new IdReferencePocketBuilder(AbstractPocketType.ID_REFERENCE.get());
	}

	public static class IdReferencePocketBuilder extends AbstractPocketBuilder<IdReferencePocketBuilder, IdReferencePocket> {
		private int referencedId = Integer.MIN_VALUE;

		protected IdReferencePocketBuilder(AbstractPocketType<IdReferencePocket> type) {
			super(type);
		}

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

		public IdReferencePocketBuilder referencedId(int referencedId) {
			this.referencedId = referencedId;
			return this;
		}
	}
}
