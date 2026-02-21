package org.dimdev.dimdoors.world.pocket.type.addon.sky;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public interface OverWorldSkyData extends SkyData {

    static float timeOfDay(long dayTime) {
        double d = Mth.frac((double)dayTime / 24000.0 - 0.25);
        double e = 0.5 - Math.cos(d * Math.PI) / 2.0;
        return (float)(d * 2.0 + e) / 3.0f;
    }

    default float getSunAngle() {
        float f = this.getTimeOfDay();
        return f * ((float)Math.PI * 2);
    }

    default float getTimeOfDay() {
        return timeOfDay(getDayTime());
    }

    long getDayTime();
    int getMoonPhase();

    default float getStarBrightness() {
        float f = this.getTimeOfDay();
        float g = 1.0f - (Mth.cos(f * ((float)Math.PI * 2)) * 2.0f + 0.25f);
        g = Mth.clamp(g, 0.0f, 1.0f);
        return g * g * 0.5f;
    }

    float[] getSunriseColors();

    @Nullable
    default float[] getSunriseColor(float f) {
        float h = 0.4F;
        float i = Mth.cos(f * ((float)Math.PI * 2F)) - 0.0F;
        float j = -0.0F;
        if (i >= -0.4F && i <= 0.4F) {
            float k = (i - -0.0F) / 0.4F * 0.5F + 0.5F;
            float l = 1.0F - (1.0F - Mth.sin(k * (float)Math.PI)) * 0.99F;
            l *= l;

            var sunriseCol = getSunriseColors();

            sunriseCol[0] = k * 0.3F + 0.7F;
            sunriseCol[1] = k * k * 0.7F + 0.2F;
            sunriseCol[2] = k * k * 0.0F + 0.2F;
            sunriseCol[3] = l;
            return sunriseCol;
        } else {
            return null;
        }
    }

    default Vec3 getCorrectedSkyColor() {
        float g = this.getTimeOfDay();
        Vec3 vec33 = getSkyColor();
        float h = Mth.cos(g * ((float)Math.PI * 2F)) * 2.0F + 0.5F;
        h = Mth.clamp(h, 0.0F, 1.0F);
        float i = (float)vec33.x * h;
        float j = (float)vec33.y * h;
        float k = (float)vec33.z * h;
        float l = this.getRainLevel();
        if (l > 0.0F) {
            float m = (i * 0.3F + j * 0.59F + k * 0.11F) * 0.6F;
            float n = 1.0F - l * 0.75F;
            i = i * n + m * (1.0F - n);
            j = j * n + m * (1.0F - n);
            k = k * n + m * (1.0F - n);
        }

        float m = getThunderLevel();
        if (m > 0.0F) {
            float n = (i * 0.3F + j * 0.59F + k * 0.11F) * 0.2F;
            float o = 1.0F - m * 0.75F;
            i = i * o + n * (1.0F - o);
            j = j * o + n * (1.0F - o);
            k = k * o + n * (1.0F - o);
        }

//        int p = this.getSkyFlashTime();
//        if (p > 0) {
//            float o = (float)p - f;
//            if (o > 1.0F) {
//                o = 1.0F;
//            }
//
//            o *= 0.45F;
//            i = i * (1.0F - o) + 0.8F * o;
//            j = j * (1.0F - o) + 0.8F * o;
//            k = k * (1.0F - o) + 1.0F * o;
//        }

        return new Vec3(i, j, k);
    }

    Vec3 getSkyColor();

    float getRainLevel();

    float getThunderLevel();

    @Override
    default SkyDataType<?> type() {
        return SkyDataType.OVERWORLD;
    }
}
