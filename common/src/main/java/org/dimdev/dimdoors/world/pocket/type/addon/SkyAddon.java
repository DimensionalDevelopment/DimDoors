package org.dimdev.dimdoors.world.pocket.type.addon;

import com.mojang.datafixers.util.Function3;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

public record SkyAddon(ResourceLocation effect, long dayTime, byte moonPhase) implements AutoSyncedAddon {
	public static StreamCodec<RegistryFriendlyByteBuf, SkyAddon> STREAM_CODEC = StreamCodec.composite(
			ResourceLocation.STREAM_CODEC, SkyAddon::getEffect,
			ByteBufCodecs.VAR_LONG, SkyAddon::getDayTime,
			ByteBufCodecs.BYTE, SkyAddon::getMoonPhase,
			SkyAddon::new
	);

	public SkyAddon() {
		this(null, 12000L, (byte) 0);
	}


	public static ResourceLocation ID = DimensionalDoors.id("sky");



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
			SkyAddon addon = new SkyAddon(effect, dayTime, moonPhase);
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
