package org.dimdev.dimdoors.world

import com.mojang.serialization.Codec
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.util.Unit
import org.dimdev.dimcore.api.PlatformRegistry
import org.dimdev.dimcore.util.DataValue
import org.dimdev.dimdoors.DimensionalDoors

object DataValues : PlatformRegistry.DataValuePlatformRegistry(DimensionalDoors.getSided()) {
    @JvmField val FRAY_VALUE: DataValue<Int> = create("fray", { 0 }, Codec.INT, ByteBufCodecs.INT)
    @JvmField val POCKET_GENERATED: DataValue<Boolean> = create("pocket_generated", { false }, Codec.BOOL)
    @JvmField val TRANSCENDENT_PROJECTILE: DataValue<Unit> = create("transcendent_projectile", { Unit.INSTANCE }, Unit.CODEC)
}
