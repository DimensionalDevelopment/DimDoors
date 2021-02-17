package org.dimdev.dimdoors.world.pocket.type.addon;

import net.minecraft.entity.Entity;
import net.minecraft.util.DyeColor;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

public interface DyeablePocket extends PocketAddon<DyeablePocket> {

	void setDyeColor(Pocket.PocketColor dyeColor);

	boolean addDye(Entity entity, DyeColor dyeColor);

	interface DyeablePocketBuilder<T extends Pocket.PocketBuilder<T, ? extends DyeablePocket>> extends PocketBuilderExtension<T, DyeablePocket> {
		default public T dyeColor(Pocket.PocketColor dyeColor) {

			this.getAddon(DyeableBuilderAddon.class).dyeColor = dyeColor;

			return getSelf();
		}
	}

	class DyeableBuilderAddon implements PocketBuilderAddon<DyeablePocket> {
		private Pocket.PocketColor dyeColor = Pocket.PocketColor.NONE;

		@Override
		public void apply(DyeablePocket pocket) {
			pocket.setDyeColor(dyeColor);
		}
	}
}
