package org.dimdev.dimdoors.world.fray;

import com.mojang.serialization.Codec;
import net.minecraft.network.codec.ByteBufCodecs;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.util.DataValue;

public final class Fray {
    public static final DataValue<Integer> FRAY_VALUE = DimensionalDoors.getSided().registerDataValue("fray", () -> 0, Codec.INT, ByteBufCodecs.INT);

    private Fray() {
    }

    public static void init() {

    }
}
