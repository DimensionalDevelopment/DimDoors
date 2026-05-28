package org.dimdev.dimdoors.effect;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import org.dimdev.dimdoors.DimensionalDoors;

public final class ModMobEffects {
    public static final MobEffect CHASED = DimensionalDoors.getSided().register(Registries.MOB_EFFECT, "chased", new ChasedEffect());

    private ModMobEffects() {
    }

    public static Holder<MobEffect> chased() {
        return BuiltInRegistries.MOB_EFFECT.wrapAsHolder(CHASED);
    }

    public static void init() {
    }
}
