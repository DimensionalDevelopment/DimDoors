package org.dimdev.dimdoors.world.pocket.type

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import net.minecraft.world.level.levelgen.structure.BoundingBox
import org.dimdev.dimdoors.world.pocket.VirtualLocation
import org.dimdev.dimdoors.world.pocket.type.addon.DyeableAddon
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon

class PrivatePocket : Pocket<PrivatePocket, PrivatePocket.PrivatePocketBuilder>, DyeableAddon.DyeablePocket {
    constructor(
        id: Int,
        world: ResourceKey<Level>,
        range: Int,
        box: BoundingBox,
        virtualLocation: VirtualLocation,
        addons: MutableList<PocketAddon>
    ) : super(id, world, range, box, virtualLocation, addons)

    constructor()

    open class PrivatePocketBuilder : PocketBuilder<PrivatePocket, PrivatePocketBuilder>, DyeableAddon.DyeablePocketBuilder<PrivatePocket, PrivatePocketBuilder> {
        protected constructor(addons: MutableList<PocketAddon.PocketBuilderAddon<*, *>>) : super(addons)

        constructor() : super()

        override fun instance(): PrivatePocketBuilder {
            return builderPrivatePocket()
        }

        override val self: PrivatePocketBuilder get() = this

        override val type get() = Pockets.PRIVATE_POCKET

        override fun pocket() = PrivatePocket()

        override fun initAddons() {
            super.initAddons()
            addAddon(DyeableAddon.DyeableBuilderAddon())
            this.dyeColor(PocketColor.WHITE)
        }

        companion object {
            val CODEC: MapCodec<PrivatePocketBuilder> = RecordCodecBuilder.mapCodec { instance -> commonFields<PrivatePocketBuilder>(instance).apply(instance, ::PrivatePocketBuilder) }
        }
    }

    override val type get() = Pockets.PRIVATE_POCKET

    companion object {
        var KEY: String = "private_pocket"

        val CODEC: MapCodec<PrivatePocket> = RecordCodecBuilder.mapCodec { instance -> commonPocketFields<PrivatePocket>(instance).apply(instance, ::PrivatePocket) }

        fun builderPrivatePocket(): PrivatePocketBuilder {
            return PrivatePocketBuilder()
        }
    }
}