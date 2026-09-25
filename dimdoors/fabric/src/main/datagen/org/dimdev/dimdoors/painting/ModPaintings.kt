package org.dimdev.dimdoors.painting

import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.entity.decoration.PaintingVariant
import org.dimdev.dimdoors.DimensionalDoors

object ModPaintings {
    @JvmField var LIMBO = key("limbo")
    @JvmField var EYES = key("eyes")
    @JvmField var PORTAL = key("portal")
    @JvmField var FREEDOM = key("freedom")
    @JvmField var GATEWAY_AT_NIGHT = key("gateway_at_night")

    @JvmField val PAINTINGS_TO_DECAY_INTO: MutableList<ResourceKey<PaintingVariant>>

    private fun key(name: String): ResourceKey<PaintingVariant> = ResourceKey.create(Registries.PAINTING_VARIANT, DimensionalDoors.id(name))

    private fun addIntoList(
        list: MutableList<ResourceKey<PaintingVariant>>,
        key: ResourceKey<PaintingVariant>,
        width: Int,
        height: Int
    ) {
        list[(width - 1) + ((height - 1) * 4)] = key
    }

    init {
        val list = mutableListOf<ResourceKey<PaintingVariant>>()

        for (y in 0..3) {
            for (x in 0..3) {
                list.add(key("placeholder_" + (x + 1) + "_" + (y + 1)))
            }
        }

        //        list.set(6, FREEDOM);
        addIntoList(list, LIMBO, 4, 2)
        addIntoList(list, FREEDOM, 2, 2)
        addIntoList(list, PORTAL, 2, 4)

        PAINTINGS_TO_DECAY_INTO = list
    }
}
