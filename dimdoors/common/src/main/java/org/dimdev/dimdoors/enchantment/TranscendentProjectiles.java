package org.dimdev.dimdoors.enchantment;

import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import org.dimdev.dimdoors.world.DataValues;

public final class TranscendentProjectiles {
    private TranscendentProjectiles() {
    }

    public static void mark(Entity entity) {
        DataValues.TRANSCENDENT_PROJECTILE.set(entity, Unit.INSTANCE);
    }

    public static boolean isMarked(Entity entity) {
        return DataValues.TRANSCENDENT_PROJECTILE.has(entity);
    }
}
