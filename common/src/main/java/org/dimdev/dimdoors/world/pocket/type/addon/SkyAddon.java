package org.dimdev.dimdoors.world.pocket.type.addon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.world.pocket.type.AbstractPocket;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

public class SkyAddon implements PocketAddon {
	public static final MapCodec<SkyAddon> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf("effect").forGetter(SkyAddon::getEffect),
			Codec.LONG.fieldOf("dayTime").forGetter(SkyAddon::getDayTime),
			Codec.BYTE.fieldOf("moonPhase").forGetter(SkyAddon::getMoonPhase)
	).apply(instance, SkyAddon::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, SkyAddon> STREAM_CODEC = StreamCodec.composite(ResourceLocation.STREAM_CODEC, SkyAddon::getEffect, ByteBufCodecs.VAR_LONG, SkyAddon::getDayTime,  ByteBufCodecs.BYTE, SkyAddon::getMoonPhase, SkyAddon::new);

	public static ResourceLocation ID = DimensionalDoors.id("sky");

	private ResourceLocation effect;

	private long dayTime = 6000L;
	private byte moonPhase;

	protected SkyAddon(ResourceLocation effect, long dayTime, byte moonPhase) {
		this.effect = effect;
		this.dayTime = dayTime;
		this.moonPhase = moonPhase;
	}

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
	public PocketAddonType<?, ?> getType() {
		return PocketAddonType.SKY_ADDON.get();
	}

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	public ResourceLocation getEffect() {
		return effect;
	}

	public interface SkyPocketBuilder<T extends AbstractPocket.AbstractPocketBuilder<T, ?>> extends PocketBuilderExtension<T> {
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

	public static class SkyBuilderAddon implements PocketBuilderAddon<SkyAddon, SkyBuilderAddon> {
		public static MapCodec<SkyBuilderAddon> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.optionalFieldOf("effect", BuiltinDimensionTypes.OVERWORLD_EFFECTS).forGetter(a -> a.effect),
                Codec.LONG.optionalFieldOf("dayTime", 12000L).forGetter(a -> a.dayTime),
                Codec.BYTE.optionalFieldOf("moonPhase", (byte) 0).forGetter(a -> a.moonPhase)
        ).apply(instance, SkyBuilderAddon::new));

		private ResourceLocation effect = BuiltinDimensionTypes.OVERWORLD_EFFECTS;
		private long dayTime = 12000L;
		private byte moonPhase = 0;

		private SkyBuilderAddon(ResourceLocation effect, long dayTime, byte moonPhase) {
			this.effect = effect;
			this.dayTime = dayTime;
			this.moonPhase = moonPhase;
		}

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
		public PocketAddonType<SkyAddon, SkyBuilderAddon> getType() {
			return PocketAddonType.SKY_ADDON.get();
		}
	}

	public interface SkyPocket extends AddonProvider {
		default boolean sky(ResourceLocation effect) {
			ensureIsPocket();
			if (!this.hasAddon(ID)) {
				SkyAddon addon = new SkyAddon(effect, 12000, (byte) 0);
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

	public byte getMoonPhase() {
		return (byte) (moonPhase % 8);
	}

	public float getStarBrightness() {
		float f = this.getTimeOfDay();
		float g = 1.0f - (Mth.cos(f * ((float)Math.PI * 2)) * 2.0f + 0.25f);
		g = Mth.clamp(g, 0.0f, 1.0f);
		return g * g * 0.5f;
	}
}
