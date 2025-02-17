package org.dimdev.dimdoors.world.pocket.type;

import org.dimdev.dimdoors.world.pocket.type.addon.DyeableAddon;

public class PrivatePocket extends LazyGenerationPocket implements DyeableAddon.DyeablePocket {
	public static String KEY = "private_pocket";

	public static PrivatePocketBuilder builderPrivatePocket() {
		return new PrivatePocketBuilder();
	}

	public static class PrivatePocketBuilder extends PocketBuilder<PrivatePocketBuilder, PrivatePocket> implements DyeableAddon.DyeablePocketBuilder<PrivatePocketBuilder> {
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
		public AbstractPocketType<PrivatePocket, PrivatePocketBuilder> getType() {
			return AbstractPocketType.PRIVATE_POCKET.get();
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
