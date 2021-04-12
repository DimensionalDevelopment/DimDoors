package org.dimdev.dimdoors.world.pocket.type;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.NbtCompound;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.PocketDirectory;

public class IdReferencePocket extends AbstractPocket<IdReferencePocket> {
	public static String KEY = "id_reference";

	protected int referencedId;

	@Override
	public IdReferencePocket fromNbt(NbtCompound nbt) {
		super.fromNbt(nbt);

		this.referencedId = nbt.getInt("referenced_id");

		return this;
	}

	@Override
	public NbtCompound toNbt(NbtCompound nbt) {
		nbt = super.toNbt(nbt);

		nbt.putInt("referenced_id", referencedId);

		return nbt;
	}

	@Override
	public AbstractPocketType<IdReferencePocket> getType() {
		return AbstractPocketType.ID_REFERENCE;
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
		public IdReferencePocketBuilder fromNbt(NbtCompound nbt) {
			if (nbt.contains("referenced_id", NbtType.INT)) referencedId = nbt.getInt("referenced_id");
			return this;
		}

		@Override
		public NbtCompound toNbt(NbtCompound nbt) {
			if (referencedId != Integer.MIN_VALUE) nbt.putInt("referenced_id", referencedId);
			return nbt;
		}

		public IdReferencePocketBuilder referencedId(int referencedId) {
			this.referencedId = referencedId;
			return this;
		}
	}
}
