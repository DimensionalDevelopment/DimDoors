package org.dimdev.dimdoors.world.pocket.type

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import net.minecraft.world.level.levelgen.structure.BoundingBox
import org.dimdev.dimdoors.world.pocket.VirtualLocation
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon
import java.util.function.Function

class PocketImpl : Pocket<PocketImpl, PocketImpl.Builder> {
    constructor(
        id: Int,
        world: ResourceKey<Level>,
        range: Int,
        box: BoundingBox,
        virtualLocation: VirtualLocation,
        addons: MutableList<PocketAddon>
    ) : super(id, world, range, box, virtualLocation, addons)

    constructor() : super()

    override val type get() = Pockets.POCKET

    class Builder : PocketBuilder<PocketImpl, Builder> {
        constructor(addons: MutableList<PocketAddon.PocketBuilderAddon<*, *>>) : super(addons)

        constructor() : super()

        override val type get() = Pockets.POCKET

        override fun pocket() = PocketImpl()

        override fun instance(): Builder {
            return builder()
        }

        companion object {
            val CODEC: MapCodec<Builder> = RecordCodecBuilder.mapCodec<Builder> { instance -> commonFields<Builder>(instance).apply(instance, ::Builder) }
        }
    }

    companion object {
        val CODEC: MapCodec<PocketImpl> = RecordCodecBuilder.mapCodec<PocketImpl> { instance -> commonPocketFields<PocketImpl>(instance).apply(instance, ::PocketImpl) }

        @JvmStatic
        fun builder(): Builder {
            return Builder()
        }
    }
}
