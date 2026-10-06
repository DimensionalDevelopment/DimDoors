package org.dimdev.dimdoors

import com.mojang.serialization.MapCodec
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import org.dimdev.dimcore.api.BuilderType
import org.dimdev.dimcore.api.Type
import org.dimdev.dimdoors.block.entity.RiftData
import org.dimdev.dimdoors.item.door.data.RiftDataList
import org.dimdev.dimdoors.item.door.data.condition.Condition
import org.dimdev.dimdoors.pockets.generator.PocketGenerator
import org.dimdev.dimdoors.pockets.modifier.Modifier
import org.dimdev.dimdoors.pockets.virtual.ImplementedVirtualPocket
import org.dimdev.dimdoors.pockets.virtual.VirtualPocket
import org.dimdev.dimdoors.rift.registry.RegistryVertex
import org.dimdev.dimdoors.rift.registry.SubSystem
import org.dimdev.dimdoors.rift.targets.VirtualTarget
import org.dimdev.dimdoors.world.decay.conditions.DecayCondition
import org.dimdev.dimdoors.world.decay.pattern.DecayPattern
import org.dimdev.dimdoors.world.decay.results.DecayResult
import org.dimdev.dimdoors.world.pocket.type.AbstractPocket
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon
import org.dimdev.dimdoors.world.pocket.type.addon.environment.Environment
import org.dimdev.dimdoors.world.pocket.type.addon.environment.cloud.CloudData
import org.dimdev.dimdoors.world.pocket.type.addon.environment.sky.SkyData
import org.dimdev.dimdoors.world.pocket.type.addon.environment.weather.WeatherData

object ModRegistryKeys {
    @JvmField val POCKET_GENERATOR_TYPE = "pocket_generator_type".type<MapCodec<out PocketGenerator<*>>>()
    @JvmField val POCKET_TYPE = "abstract_pocket_type".type<BuilderType<AbstractPocket<*, *>, AbstractPocket.AbstractPocketBuilder<*, *>>>()
    @JvmField val VIRTUAL_POCKET_TYPE = "virtual_pocket_type".type<MapCodec<out ImplementedVirtualPocket<*>>>()
    @JvmField val VIRTUAL_TARGET = "virtual_type".type<MapCodec<out VirtualTarget<*>>>()
    @JvmField val MODIFIER_TYPE = "modifier_type".type<MapCodec<out Modifier>>()
    @JvmField val CONDITION_TYPE = "rift_data_condition".type<MapCodec<out Condition>>()
    @JvmField val POCKET_ADDON_TYPE = "pocket_applicable_addon_type".type<BuilderType<PocketAddon, PocketAddon.PocketBuilderAddon<*, *>>>()
    @JvmField val SUBSYSTEM_TYPE = "subsystem_type".type<SubSystem.Type<*>>()
    @JvmField val REGISTRY_VERTEX_TYPE = "registry_vertex".type<RegistryVertex>()
    val DECAY_CONDITION_TYPE = "decay_condition_type".type<MapCodec<out DecayCondition>>()
    val DECAY_RESULT_TYPE = "decay_result_type".type<MapCodec<out DecayResult>>()
    val DECAY_PATTERN_TYPE = "decay_pattern_type".type<MapCodec<out DecayPattern>>()
    val ENVIRONMENT = "environment".type<Type<Environment>>()
    val SKY_DATA = "cloud_data".type<Type<SkyData>>()
    val CLOUD_DATA = "sky_data".type<Type<CloudData>>()
    val WEATHER_DATA = "weather_data".type<Type<WeatherData>>()

    @JvmField val MODIFIER = "virtual_pocket_type".type<Modifier>()
    @JvmField val POCKET_GENERATOR = "pockets/generators".dynamic<PocketGenerator<*>>()
    @JvmField val VIRTUAL_POCKET = "pockets/virtual".dynamic<VirtualPocket>()
    @JvmField val POCKET_GROUPS = "pockets/groups".dynamic<VirtualPocket>()
    @JvmField val RIFT_DATA = "pockets/rift_data".dynamic<RiftData>()
    @JvmField val DOOR_DATA = "door/data".dynamic<RiftDataList>()

    fun register() {}

    private fun <T> String.dynamic(): ResourceKey<Registry<T>> = ResourceKey.createRegistryKey<T>(ResourceLocation.withDefaultNamespace(this))
    private fun <T> String.type(): ResourceKey<Registry<T>> = ResourceKey.createRegistryKey<T>(DimensionalDoors.id(this))
}
