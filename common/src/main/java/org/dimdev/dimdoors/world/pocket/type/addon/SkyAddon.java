package org.dimdev.dimdoors.world.pocket.type.addon;

import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.mixin.client.accessor.DimensionSpecialEffectsMixin;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

public class SkyAddon implements AutoSyncedAddon {
	public static ResourceLocation ID = DimensionalDoors.id("sky");

	private ResourceLocation effect;

	public boolean setEfffect(ResourceLocation effect) {
		this.effect = effect;
		return true;
	}

	@Override
	public PocketAddon fromNbt(CompoundTag nbt) {
		ResourceLocation tag = null;

		this.effect = !nbt.contains("effect") && nbt.contains("world") ? ResourceLocation.tryParse(nbt.getString("world")) : nbt.contains("effect") ? ResourceLocation.tryParse(nbt.getString("effect")) : null;

		return this;
	}

	@Override
	public CompoundTag toNbt(CompoundTag nbt) {
		AutoSyncedAddon.super.toNbt(nbt);

		nbt.putString("effect", this.effect.toString());

		return nbt;
	}

	@Override
	public PocketAddonType<? extends PocketAddon> getType() {
		return PocketAddonType.SKY_ADDON.get();
	}

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	public ResourceLocation getEffect() {
		return effect;
	}

	@Override
	public AutoSyncedAddon read(FriendlyByteBuf buf) {
		this.effect = buf.readResourceLocation();
		return this;
	}

	@Override
	public FriendlyByteBuf write(FriendlyByteBuf buf) {
		buf.writeResourceLocation(effect);
		return buf;
	}

	public interface SkyPocketBuilder<T extends Pocket.PocketBuilder<T, ?>> extends PocketBuilderExtension<T> {
		default T dimenionType(ResourceLocation effect) {

			this.<SkyBuilderAddon>getAddon(ID).effect = effect;

			return getSelf();
		}
	}

	public static class SkyBuilderAddon implements PocketBuilderAddon<SkyAddon> {

		private ResourceLocation effect = BuiltinDimensionTypes.OVERWORLD_EFFECTS;

		@Override
		public void apply(Pocket pocket) {
			SkyAddon addon = new SkyAddon();
			addon.effect = effect;
			pocket.addAddon(addon);
		}

		@Override
		public ResourceLocation getId() {
			return ID;
		}

		@Override
		public PocketBuilderAddon<SkyAddon> fromNbt(CompoundTag nbt) {
			this.effect = ResourceLocation.tryParse(nbt.getString("world"));

			return this;
		}

		@Override
		public CompoundTag toNbt(CompoundTag nbt) {
			PocketBuilderAddon.super.toNbt(nbt);

			nbt.putString("dimenionType", effect.toString());

			return nbt;
		}

		@Override
		public PocketAddonType<SkyAddon> getType() {
			return PocketAddonType.SKY_ADDON.get();
		}
	}

	public interface SkyPocket extends AddonProvider {
		default boolean sky(ResourceLocation effect) {
			ensureIsPocket();
			if (!this.hasAddon(ID)) {
				SkyAddon addon = new SkyAddon();
				this.addAddon(addon);
				return addon.setEfffect(effect);
			}
			return this.<SkyAddon>getAddon(ID).setEfffect(effect);
		}
	}
}
