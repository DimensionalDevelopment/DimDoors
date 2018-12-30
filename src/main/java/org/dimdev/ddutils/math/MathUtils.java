package org.dimdev.ddutils.math;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import java.util.Map;
import java.util.Random;

public final class MathUtils {

    public static <T> T weightedRandom(Map<T, Float> weights) {
        if (weights.size() == 0) return null;
        int totalWeight = 0;
        for (float weight : weights.values()) {
            totalWeight += weight;
        }
        Random random = new Random();
        float f = random.nextFloat() * totalWeight;
        for (Map.Entry<T, Float> e : weights.entrySet()) {
            f -= e.getValue();
            if (f < 0) return e.getKey();
        }
        return null;
    }

    public static Vector3d toFlow(Vec3d vec){
        return new Vector3d(vec.x, vec.y, vec.z);
    }

    public static Vector3i toFlow(Vec3i vec){
        return new Vector3i(vec.getX(), vec.getY(), vec.getZ());
    }
}
