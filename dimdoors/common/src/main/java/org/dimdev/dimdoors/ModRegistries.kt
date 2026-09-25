package org.dimdev.dimdoors

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import org.dimdev.dimdoors.block.entity.RiftData
import org.dimdev.dimdoors.item.door.data.RiftDataList
import org.dimdev.dimdoors.item.door.data.condition.Conditions
import org.dimdev.dimdoors.pockets.generator.PocketGenerator
import org.dimdev.dimdoors.pockets.modifier.Modifiers
import org.dimdev.dimdoors.pockets.virtual.VirtualPockets
import org.dimdev.dimdoors.pockets.virtual.VirtualPocket
import org.dimdev.dimdoors.rift.registry.RegistryVertices
import org.dimdev.dimdoors.rift.targets.VirtualTargets
import org.dimdev.dimdoors.world.pocket.type.Pockets
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddons
import org.dimdev.dimdoors.world.pocket.type.addon.environment.Environment
import org.dimdev.dimdoors.world.pocket.type.addon.environment.Environments
import org.dimdev.dimdoors.world.pocket.type.addon.environment.cloud.CloudDatum
import org.dimdev.dimdoors.world.pocket.type.addon.environment.sky.SkyDatum
import org.dimdev.dimdoors.world.pocket.type.addon.environment.weather.WeatherDatum

object ModRegistries {
    @JvmField val POCKET_GENERATOR_TYPE = ModRegistryKeys.POCKET_GENERATOR_TYPE.createRegistry()
    @JvmField val POCKET_TYPE = Pockets.registry
    @JvmField val VIRTUAL_POCKET_TYPE = VirtualPockets.registry
    @JvmField val VIRTUAL_TYPE = VirtualTargets.registry
    @JvmField val MODIFIER_TYPE = Modifiers.registry
    @JvmField val CONDITION_TYPE = Conditions.registry
    @JvmField val SUBSYTEM_TYPE = ModRegistryKeys.SUBSYSTEM_TYPE.createRegistry()
    @JvmField val REGISTRY_VERTEX_TYPE = RegistryVertices.registry
    val ENVIRONMENT_TYPE = Environments.registry
    val WEATHER_DATA_TYPE = WeatherDatum.registry
    val SKY_DATA_TYPE = SkyDatum.registry
    val CLOUD_DATA_TYPE = CloudDatum.registry

    @JvmStatic fun register() {
        DimensionalDoors.getSided().createDynamicRegistry(ModRegistryKeys.POCKET_GENERATOR, PocketGenerator.CODEC)
        DimensionalDoors.getSided().createDynamicRegistry(ModRegistryKeys.VIRTUAL_POCKET, VirtualPocket.CODEC)
        DimensionalDoors.getSided().createDynamicRegistry(ModRegistryKeys.POCKET_GROUPS, VirtualPocket.CODEC)
        DimensionalDoors.getSided().createDynamicRegistry(ModRegistryKeys.RIFT_DATA, RiftData.CODEC)
        DimensionalDoors.getSided().createDynamicRegistry(ModRegistryKeys.DOOR_DATA, RiftDataList.CODEC)
    }
}

private fun <T> ResourceKey<Registry<T>>.createRegistry(): Registry<T> = DimensionalDoors.getSided().createRegistry(this)
