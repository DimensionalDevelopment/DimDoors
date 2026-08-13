package org.dimdev.dimdoors.world.fray;

import com.mojang.serialization.Codec;
import net.minecraft.network.codec.ByteBufCodecs;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimcore.util.DataValue;

public final class ModDataValues {
    public static final DataValue<Integer> FRAY_VALUE = DimensionalDoors.getSided().registerDataValue("fray", () -> 0, Codec.INT, ByteBufCodecs.INT);

    public static void init() {

    }
}
