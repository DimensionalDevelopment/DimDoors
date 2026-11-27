package org.dimdev.dimdoors

import org.dimdev.dimcore.util.DatapackLoader
import org.dimdev.dimdoors.decay.pattern.DecayPattern
import org.dimdev.dimdoors.item.door.RiftDataList
import org.dimdev.dimdoors.item.door.condition.Conditions
import org.dimdev.dimdoors.pockets.addon.environment.Environments
import org.dimdev.dimdoors.pockets.addon.environment.cloud.CloudDatum
import org.dimdev.dimdoors.pockets.addon.environment.sky.SkyDatum
import org.dimdev.dimdoors.pockets.addon.environment.weather.WeatherDatum
import org.dimdev.dimdoors.pockets.generator.PocketGenerator
import org.dimdev.dimdoors.pockets.generator.PocketGenerators
import org.dimdev.dimdoors.pockets.modifier.Modifiers
import org.dimdev.dimdoors.pockets.type.Pockets
import org.dimdev.dimdoors.pockets.virtual.VirtualPocket
import org.dimdev.dimdoors.pockets.virtual.VirtualPockets
import org.dimdev.dimdoors.rift.RiftData
import org.dimdev.dimdoors.rift.targets.VirtualTargets
import org.dimdev.dimdoors.subsystem.graph.RegistryVertices
import org.dimdev.dimdoors.subsystem.registry.SubsystemTypes

object ModRegistries {
    @JvmField val POCKET_GENERATOR_TYPE = PocketGenerators.registry
    @JvmField val POCKET_TYPE = Pockets.registry
    @JvmField val VIRTUAL_POCKET_TYPE = VirtualPockets.registry
    @JvmField val VIRTUAL_TYPE = VirtualTargets.registry
    @JvmField val MODIFIER_TYPE = Modifiers.registry
    @JvmField val CONDITION_TYPE = Conditions.registry
    @JvmField val SUBSYTEM_TYPE = SubsystemTypes.registry
    @JvmField val REGISTRY_VERTEX_TYPE = RegistryVertices.registry
    val ENVIRONMENT_TYPE = Environments.registry
    val WEATHER_DATA_TYPE = WeatherDatum.registry
    val SKY_DATA_TYPE = SkyDatum.registry
    val CLOUD_DATA_TYPE = CloudDatum.registry

    @JvmStatic fun register() {
        DatapackLoader.register(ModRegistryKeys.POCKET_GENERATOR, PocketGenerator.CODEC)
        DatapackLoader.register(ModRegistryKeys.POCKET_GROUPS, VirtualPocket.CODEC)
        DatapackLoader.register(ModRegistryKeys.RIFT_DATA, RiftData.CODEC)
        DatapackLoader.register(ModRegistryKeys.DOOR_DATA, RiftDataList.CODEC)
        DatapackLoader.register(ModRegistryKeys.DECAY_PATTERN, DecayPattern.CODEC)
    }
}
