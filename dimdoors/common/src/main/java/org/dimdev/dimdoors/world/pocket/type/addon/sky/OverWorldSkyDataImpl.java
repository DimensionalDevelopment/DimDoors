package org.dimdev.dimdoors.world.pocket.type.addon.sky;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class OverWorldSkyDataImpl implements OverWorldSkyData {
    public static final MapCodec<OverWorldSkyData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.LONG.optionalFieldOf("day_time", 12000L).forGetter(OverWorldSkyData::getDayTime),
            Codec.INT.optionalFieldOf("moon_phase", 0).forGetter(OverWorldSkyData::getMoonPhase),
            Vec3.CODEC.optionalFieldOf("skyColor", new Vec3(0.486f, 0.654f, 1.0f)).forGetter(OverWorldSkyData::getSkyColor),
            Codec.FLOAT.optionalFieldOf("rain_level", 0.0f).forGetter(OverWorldSkyData::getRainLevel),
            Codec.FLOAT.optionalFieldOf("thunder_level", 0.0f).forGetter(OverWorldSkyData::getThunderLevel)
    ).apply(instance, OverWorldSkyDataImpl::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, OverWorldSkyData> STREAM_CODEC = new StreamCodec<RegistryFriendlyByteBuf, OverWorldSkyData>() {
        @Override
        public @NotNull OverWorldSkyData decode(RegistryFriendlyByteBuf buf) {
            return new OverWorldSkyDataImpl(
                    buf.readVarLong(),
                    buf.readVarInt(),
                    buf.readVec3(),
                    buf.readFloat(),
                    buf.readFloat()
            );
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, OverWorldSkyData data) {
            buf.writeVarLong(data.getDayTime());
            buf.writeVarInt(data.getMoonPhase());
            buf.writeVec3(data.getSkyColor());
            buf.writeFloat(data.getRainLevel());
            buf.writeFloat(data.getThunderLevel());
        }
    };

    private long dayTime;
    private int moonPhase;
    private Vec3 skyColor;
    private float rainLevel;
    private float thunderLevel;

    private final float[] sunriseCol = new float[4];

    public OverWorldSkyDataImpl(long dayTime, int moonPhase, Vec3 skyColor, float rainLevel, float thunderLevel) {
        this.dayTime = dayTime;
        this.moonPhase = moonPhase;
        this.skyColor = skyColor;
        this.rainLevel = rainLevel;
        this.thunderLevel = thunderLevel;
    }


    public void setDayTime(long dayTime) {
        this.dayTime = dayTime;
    }

    public void setMoonPhase(byte moonPhase) {
        this.moonPhase = moonPhase;
    }

    @Override
    public long getDayTime() {
        return dayTime;
    }

    public int getMoonPhase() {
        return moonPhase % 8;
    }


    public Vec3 getSkyColor() {
        return skyColor;
    }

    public float[] getSunriseColors() {
        return sunriseCol;
    }

    public float getRainLevel() {
        return rainLevel;
    }

    public float getThunderLevel() {
        return thunderLevel;
    }
}
