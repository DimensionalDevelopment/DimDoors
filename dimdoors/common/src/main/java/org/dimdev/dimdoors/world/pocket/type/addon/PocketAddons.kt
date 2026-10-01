package org.dimdev.dimdoors.world.pocket.type.addon

import org.dimdev.dimcore.api.PlatformRegistry
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.ModRegistryKeys
import org.dimdev.dimdoors.world.pocket.type.addon.environment.EnvironmentAddon

object PocketAddons : PlatformRegistry.BuilderTypePlatformRegistry<PocketAddon, PocketAddon.PocketBuilderAddon<*, *>>(ModRegistryKeys.POCKET_ADDON_TYPE, DimensionalDoors.getSided()) {
    val DYEABLE_ADDON: PocketAddonType<DyeableAddon> = create("dyeable", DyeableAddon.CODEC, DyeableAddon.BUILDER_CODEC)
    var PREVENT_BLOCK_MODIFICATION_ADDON: PocketAddonType<PreventBlockModificationAddon> = create("prevent_block_modification", PreventBlockModificationAddon.codec, PreventBlockModificationAddon.PreventBlockModificationBuilderAddon.codec, PreventBlockModificationAddon.streamCodec)
    val ENVIRONMENT_ADDON: PocketAddonType<EnvironmentAddon> = create("environment", EnvironmentAddon.CODEC, EnvironmentAddon.EnvironmentBuilderAddon.CODEC, EnvironmentAddon.STREAM_CODEC)
    val MUSIC_ADDON: PocketAddonType<MusicAddon> = create("music", MusicAddon.CODEC, MusicAddon.MusicAddonBuilder.CODEC, MusicAddon.STREAM_CODEC)
}
