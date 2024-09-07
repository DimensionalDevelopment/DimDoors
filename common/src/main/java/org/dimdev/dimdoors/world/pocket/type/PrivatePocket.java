package org.dimdev.dimdoors.world.pocket.type;

import org.dimdev.dimdoors.world.pocket.type.addon.DyeableAddon;

public class PrivatePocket extends LazyGenerationPocket implements DyeableAddon.DyeablePocket {
	public static String KEY = "private_pocket";

	public static PrivatePocketBuilder<?, PrivatePocket> builderPrivatePocket() {
		return new PrivatePocketBuilder<>(AbstractPocket.AbstractPocketType.PRIVATE_POCKET.get());
	}

	public static class PrivatePocketBuilder<P extends PrivatePocketBuilder<P, T>, T extends PrivatePocket> extends Pocket.PocketBuilder<P, T> implements DyeableAddon.DyeablePocketBuilder<P> {
		protected PrivatePocketBuilder(AbstractPocket.AbstractPocketType<T> type) {
			super(type);
		}

		@Override
		public void initAddons() {
			super.initAddons();
			addAddon(new DyeableAddon.DyeableBuilderAddon());
			this.dyeColor(org.dimdev.dimdoors.world.pocket.type.PocketColor.WHITE);
		}
	}

	@Override
	public AbstractPocket.AbstractPocketType<?> getType() {
		return AbstractPocket.AbstractPocketType.PRIVATE_POCKET.get();
	}

	public static String getKEY() {
		return KEY;
	}
}
