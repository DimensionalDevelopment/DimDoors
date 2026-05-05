package org.dimdev.dimdoors.compat.sable;

import dev.ryanhcode.sable.companion.SableCompanion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.item.RaycastHelper;

import java.util.function.BiFunction;

public class SableCompat {
    public static void init() {
        SableHelper.INSTANCE = new ActiveSableHelper();
    }
}
