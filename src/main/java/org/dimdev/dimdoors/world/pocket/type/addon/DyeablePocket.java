package org.dimdev.dimdoors.world.pocket.type.addon;

import org.dimdev.dimdoors.world.pocket.type.Pocket;

public interface DyeablePocket extends PocketAddon<DyeablePocket> {

	void setDyeColor(Pocket.PocketColor dyeColor);


	interface DyeablePocketBuilder<T extends Pocket.PocketBuilder<T, P>, P extends DyeablePocket> extends PocketBuilderExtension<T, P> {
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
