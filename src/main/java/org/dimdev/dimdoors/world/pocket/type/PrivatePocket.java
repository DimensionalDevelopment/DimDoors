package org.dimdev.dimdoors.world.pocket.type;

import org.dimdev.dimdoors.world.pocket.type.addon.DyeableAddon;

public class PrivatePocket extends LazyGenerationPocket implements DyeableAddon.DyeablePocket {
	public static String KEY = "private_pocket";

	public static PrivatePocketBuilder<?, PrivatePocket> builderPrivatePocket() {
		return new PrivatePocketBuilder<>(AbstractPocketType.PRIVATE_POCKET);
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
		return AbstractPocketType.PRIVATE_POCKET;
	}

	public static String getKEY() {
		return KEY;
	}
}
