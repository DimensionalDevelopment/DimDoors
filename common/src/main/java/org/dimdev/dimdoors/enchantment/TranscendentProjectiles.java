package org.dimdev.dimdoors.enchantment;

import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimcore.util.DataValue;

public final class TranscendentProjectiles {
    private static final DataValue<Unit> TRANSCENDENT = DimensionalDoors.getSided().registerDataValue("transcendent_projectile", () -> Unit.INSTANCE, Unit.CODEC, null);

    private TranscendentProjectiles() {
    }

    public static void init() {
    }

    public static void mark(Entity entity) {
        TRANSCENDENT.set(entity, Unit.INSTANCE);
    }

    public static boolean isMarked(Entity entity) {
        return TRANSCENDENT.has(entity);
    }
}
