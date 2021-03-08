package org.dimdev.dimdoors.world.pocket.type;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.CompoundTag;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;

public class IdReferencePocket extends AbstractPocket<IdReferencePocket> {
	public static String KEY = "id_reference";

	protected int referencedId;

	@Override
	public IdReferencePocket fromTag(CompoundTag tag) {
		super.fromTag(tag);

		this.referencedId = tag.getInt("referenced_id");

		return this;
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		tag = super.toTag(tag);

		tag.putInt("referenced_id", referencedId);

		return tag;
	}

	@Override
	public AbstractPocketType<IdReferencePocket> getType() {
		return AbstractPocketType.ID_REFERENCE;
	}

	@Override
	public Pocket getReferencedPocket() {
		return DimensionalRegistry.getPocketDirectory(getWorld()).getPocket(referencedId);
	}

	public static IdReferencePocketBuilder builder() {
		return new IdReferencePocketBuilder(AbstractPocketType.ID_REFERENCE);
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
		public IdReferencePocketBuilder fromTag(CompoundTag tag) {
			if (tag.contains("referenced_id", NbtType.INT)) referencedId = tag.getInt("referenced_id");
			return this;
		}

		@Override
		public CompoundTag toTag(CompoundTag tag) {
			if (referencedId != Integer.MIN_VALUE) tag.putInt("referenced_id", referencedId);
			return tag;
		}

		public IdReferencePocketBuilder referencedId(int referencedId) {
			this.referencedId = referencedId;
			return this;
		}
	}
}
