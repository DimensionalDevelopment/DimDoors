package org.dimdev.dimdoors.world.pocket.type.addon

import org.dimdev.dimcore.api.PlatformRegistry
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.ModRegistryKeys
import org.dimdev.dimdoors.world.pocket.type.addon.PreventBlockModificationAddon.PreventBlockModificationBuilderAddon
import org.dimdev.dimdoors.world.pocket.type.addon.environment.EnvironmentAddon

object PocketAddons : PlatformRegistry.BuilderTypePlatformRegistry<PocketAddon, PocketAddon.PocketBuilderAddon<*, *>>(ModRegistryKeys.POCKET_ADDON_TYPE, DimensionalDoors.getSided(), true) {
    @JvmField val DYEABLE_ADDON: PocketAddonType<DyeableAddon, DyeableAddon.DyeableBuilderAddon> = create("dyeable", DyeableAddon.CODEC, DyeableAddon.BUILDER_CODEC,DyeableAddon::DyeableBuilderAddon)
    @JvmField var PREVENT_BLOCK_MODIFICATION_ADDON: PocketAddonType<PreventBlockModificationAddon, PreventBlockModificationBuilderAddon> = create("prevent_block_modification", PreventBlockModificationAddon.codec, PreventBlockModificationAddon.PreventBlockModificationBuilderAddon.codec, { PreventBlockModificationBuilderAddon }, PreventBlockModificationAddon.streamCodec)
    @JvmField val ENVIRONMENT_ADDON: PocketAddonType<EnvironmentAddon, EnvironmentAddon.EnvironmentBuilderAddon> = create("environment", EnvironmentAddon.CODEC, EnvironmentAddon.EnvironmentBuilderAddon.CODEC, { EnvironmentAddon.EnvironmentBuilderAddon() }, EnvironmentAddon.STREAM_CODEC)
    @JvmField val MUSIC_ADDON: PocketAddonType<MusicAddon, MusicAddon.MusicAddonBuilder> = create("music", MusicAddon.CODEC, MusicAddon.MusicAddonBuilder.CODEC, MusicAddon::MusicAddonBuilder, MusicAddon.STREAM_CODEC)
//    @JvmField val DUNGEON: PocketAddonType<DungeonAddon> = create("dungeon", DungeonAddon.CODEC, )
}
