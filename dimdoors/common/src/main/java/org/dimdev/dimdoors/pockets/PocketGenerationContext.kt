package org.dimdev.dimdoors.pockets

import net.minecraft.core.Holder
import net.minecraft.core.HolderLookup
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import org.dimdev.dimcore.util.DatapackLoader
import org.dimdev.dimdoors.DimensionalDoors.Companion.config
import org.dimdev.dimdoors.rift.link.LinkProperties
import org.dimdev.dimdoors.rift.targets.VirtualTarget
import kotlin.jvm.optionals.getOrNull

/*
 * The PocketGenerationContext is the context used all throughout the generation of pockets.
 */
data class PocketGenerationContext(

    val world: ServerLevel,
    val sourceVirtualLocation: VirtualLocation,
    val linkTo: VirtualTarget<*>?,
    val linkProperties: LinkProperties?,
    val provider: HolderLookup.Provider
) {
    fun toVariableMap(stringDoubleMap: MutableMap<String, Double> = mutableMapOf()): MutableMap<String, Double> {
        stringDoubleMap["depth"] = this.sourceVirtualLocation.depth.toDouble()
        stringDoubleMap["public_size"] = config.pocketsConfig.publicPocketSize.toDouble()
        stringDoubleMap["private_size"] = config.pocketsConfig.privatePocketSize.toDouble()
        return stringDoubleMap
    }


    fun <T : Any> lookupHolder(id: ResourceKey<T>): Holder<T> {
        return DatapackLoader.lookup(id.registryKey())!!.getOrThrow(id)
    }

    fun <T : Any> lookupHolderOptional(id: ResourceKey<T>): Holder.Reference<T>? {
        return DatapackLoader.lookup(id.registryKey())?.get(id)?.getOrNull()
    }
}
