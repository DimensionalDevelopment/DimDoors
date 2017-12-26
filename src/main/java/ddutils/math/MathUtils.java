package ddutils.math;

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
}
