package org.dimdev.dimdoors.world.pocket.type.addon;

import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.mixin.client.accessor.DimensionSpecialEffectsMixin;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

public class SkyAddon implements AutoSyncedAddon {
	public static ResourceLocation ID = DimensionalDoors.id("sky");

	private ResourceLocation effect;

	private long dayTime = 6000L;
	private byte moonPhase;

	public boolean setEfffect(ResourceLocation effect) {
		this.effect = effect;
		return true;
	}

	public void setDayTime(long dayTime) {
		this.dayTime = dayTime;
	}

	public void setMoonPhase(byte moonPhase) {
		this.moonPhase = moonPhase;
	}


	@Override
	public PocketAddon fromNbt(CompoundTag nbt) {
		ResourceLocation tag = null;

		this.effect = !nbt.contains("effect") && nbt.contains("world") ? ResourceLocation.tryParse(nbt.getString("world")) : nbt.contains("effect") ? ResourceLocation.tryParse(nbt.getString("effect")) : null;
		this.dayTime = nbt.contains("dayTime") ? nbt.getLong("dayTime") : 12000L;
		this.moonPhase = nbt.contains("moonPhase") ? nbt.getByte("moonPhase") : 0;
		return this;
	}

	@Override
	public CompoundTag toNbt(CompoundTag nbt) {
		AutoSyncedAddon.super.toNbt(nbt);

		nbt.putString("effect", this.effect.toString());
		nbt.putLong("dayTime", this.dayTime);
		nbt.putByte("moonPhase", this.moonPhase);

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
		this.dayTime = buf.readLong();
		this.moonPhase = buf.readByte();
		return this;
	}

	@Override
	public FriendlyByteBuf write(FriendlyByteBuf buf) {
		buf.writeResourceLocation(effect);
		buf.writeLong(dayTime);
		buf.writeByte(moonPhase);
		return buf;
	}


	public interface SkyPocketBuilder<T extends Pocket.PocketBuilder<T, ?>> extends PocketBuilderExtension<T> {
		default T dimenionType(ResourceLocation effect) {

			this.<SkyBuilderAddon>getAddon(ID).effect = effect;

			return getSelf();
		}

		default T dayTime(long dayTime) {

			this.<SkyBuilderAddon>getAddon(ID).dayTime = dayTime;

			return getSelf();
		}

		default T moonPhase(byte moonPhase) {

			this.<SkyBuilderAddon>getAddon(ID).moonPhase = moonPhase;

			return getSelf();
		}
	}

	public static class SkyBuilderAddon implements PocketBuilderAddon<SkyAddon> {

		private ResourceLocation effect = BuiltinDimensionTypes.OVERWORLD_EFFECTS;
		private long dayTime = 12000L;
		private byte moonPhase = 0;

		@Override
		public void apply(Pocket pocket) {
			SkyAddon addon = new SkyAddon();
			addon.effect = effect;
			addon.dayTime = dayTime;
			addon.moonPhase = moonPhase;
			pocket.addAddon(addon);
		}

		@Override
		public ResourceLocation getId() {
			return ID;
		}

		@Override
		public PocketBuilderAddon<SkyAddon> fromNbt(CompoundTag nbt) {
			this.effect = ResourceLocation.tryParse(nbt.getString("effect"));
			this.dayTime = nbt.getLong("dayTime");
			this.moonPhase = nbt.getByte("moonPhase");

			return this;
		}

		@Override
		public CompoundTag toNbt(CompoundTag nbt) {
			PocketBuilderAddon.super.toNbt(nbt);

			nbt.putString("effect", effect.toString());
			nbt.putLong("dayTime", dayTime);
			nbt.putByte("moonPhase", moonPhase);

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

	public static float timeOfDay(long dayTime) {
		double d = Mth.frac((double)dayTime / 24000.0 - 0.25);
		double e = 0.5 - Math.cos(d * Math.PI) / 2.0;
		return (float)(d * 2.0 + e) / 3.0f;
	}

	public float getSunAngle() {
		float f = this.getTimeOfDay();
		return f * ((float)Math.PI * 2);
	}

	public float getTimeOfDay() {
		return timeOfDay(getDayTime());
	}

	private long getDayTime() {
		return dayTime;
	}

	public int getMoonPhase() {
		return moonPhase % 8;
	}

	public float getStarBrightness() {
		float f = this.getTimeOfDay();
		float g = 1.0f - (Mth.cos(f * ((float)Math.PI * 2)) * 2.0f + 0.25f);
		g = Mth.clamp(g, 0.0f, 1.0f);
		return g * g * 0.5f;
	}
}
