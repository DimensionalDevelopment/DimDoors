package org.dimdev.dimdoors.datagen

import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import org.dimdev.dimdoors.ModRegistryKeys
import org.dimdev.dimdoors.api.util.key
import org.dimdev.dimdoors.block.ModBlocks
import org.dimdev.dimdoors.item.door.data.RiftDataList
import org.dimdev.dimdoors.item.door.data.condition.Condition
import org.dimdev.dimdoors.pockets.PocketGenerator
import org.dimdev.dimdoors.rift.registry.LinkProperties
import org.dimdev.dimdoors.rift.targets.DungeonTarget
import org.dimdev.dimdoors.rift.targets.EscapeTarget
import org.dimdev.dimdoors.rift.targets.PrivatePocketExitTarget
import org.dimdev.dimdoors.rift.targets.PrivatePocketTarget
import org.dimdev.dimdoors.rift.targets.PublicPocketTarget
import org.dimdev.dimdoors.world.ModDimensions

object DoorDataDataGen {
    fun bootstrap(ctx: DimDoorsDynamicRegistryProvider.RegistrationHelper) {
        ctx.register(key(ModBlocks.QUARTZ_DOOR), RiftDataList.builder()
            .add(PrivatePocketExitTarget, Condition.level(ModDimensions.PERSONAL))
            .add(PrivatePocketTarget, Condition.not(Condition.level(ModDimensions.PERSONAL)))
            .builder()
        )

        val dungeonBuilder = DungeonTarget.builder()
            .acceptedGroups(mutableSetOf(0))
            .newRiftWeight(1.0f)
            .weightMaximum(100.0)
            .coordFactor(1.0)
            .noLinkBack(false)
            .positiveDepthFactor(10000.0)
            .negativeDepthFactor(160.0)
            .noLink(false)

        val properties = LinkProperties.builder {
            entranceWeight(0.0f)
            groups(0, 1)
            floatingWeight(0.0f)
            linksRemaining(1)
            oneWay(false)
        }

        ctx.register(key(Blocks.CRIMSON_DOOR), RiftDataList.of(
            dungeonBuilder.dungeonGroup(PocketGenerator.NETHER_DUNGEONS).build(),
            properties,
            Condition.alwaysTrue())
        )

        ctx.register(key(ModBlocks.AMALGAM_DOOR), RiftDataList.of(
            dungeonBuilder.dungeonGroup(PocketGenerator.MYTH_DUNGEONS).build(),
            properties,
            Condition.alwaysTrue())
        )

        ctx.register(key(ModBlocks.STONE_DOOR), RiftDataList.of(
            dungeonBuilder.dungeonGroup(PocketGenerator.ALL_DUNGEONS).build(),
            properties,
            Condition.alwaysTrue())
        )


        ctx.register(key(Blocks.IRON_DOOR), RiftDataList.of(PublicPocketTarget, Condition.alwaysTrue()))

        ctx.register(key(Blocks.OAK_DOOR), RiftDataList.of(EscapeTarget.of(true), Condition.alwaysTrue()))
    }

    private fun key(door: Block): ResourceKey<RiftDataList> = ModRegistryKeys.DOOR_DATA.key(door.builtInRegistryHolder().key().location())
}
