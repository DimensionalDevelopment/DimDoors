package org.dimdev.dimdoors.pockets.addon.environment

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import org.dimdev.dimdoors.pockets.addon.PocketAddon
import org.dimdev.dimdoors.pockets.addon.PocketAddons
import org.dimdev.dimdoors.pockets.addon.environment.cloud.CloudData
import org.dimdev.dimdoors.pockets.addon.environment.sky.SkyData
import org.dimdev.dimdoors.pockets.addon.environment.weather.WeatherData
import org.dimdev.dimdoors.pockets.type.Pocket

class EnvironmentAddon(environment: Environment = EmptyEnvironment) : PocketAddon {
    var environment: Environment = EmptyEnvironment

    init {
        this.environment = environment
    }

    override val type get() = PocketAddons.ENVIRONMENT_ADDON

    val sky: SkyData
        get() = environment.sky

    val cloud: CloudData
        get() = environment.cloud

    val weather: WeatherData
        get() = environment.weather

    class EnvironmentBuilderAddon(private val environment: Environment = EmptyEnvironment) :
        PocketAddon.PocketBuilderAddon<EnvironmentAddon, EnvironmentBuilderAddon> {
        override fun apply(pocket: Pocket<*, *>): EnvironmentAddon {
            val addon = EnvironmentAddon(environment)
            pocket.addAddon(addon)

            return addon
        }

        override val type get() = PocketAddons.ENVIRONMENT_ADDON

        companion object {
            val CODEC: MapCodec<EnvironmentBuilderAddon> = RecordCodecBuilder.mapCodec { instance ->
                instance.group(
                    Environment.CODEC.optionalFieldOf("environment", EmptyEnvironment)
                        .forGetter(EnvironmentBuilderAddon::environment)
                ).apply(instance, ::EnvironmentBuilderAddon)
            }
        }
    }

    companion object {
        val CODEC: MapCodec<EnvironmentAddon> = RecordCodecBuilder.mapCodec { instance ->
                instance.group(Environment.CODEC.optionalFieldOf("environment", EmptyEnvironment).forGetter(EnvironmentAddon::environment))
                    .apply(instance, ::EnvironmentAddon)
            }
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, EnvironmentAddon> = StreamCodec.composite(
                Environment.STREAM_CODEC,
                EnvironmentAddon::environment,
            ::EnvironmentAddon
        )
    }
}