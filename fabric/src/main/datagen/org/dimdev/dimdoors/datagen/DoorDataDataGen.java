package org.dimdev.dimdoors.datagen;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.dimdev.dimdoors.ModRegistryKeys;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.item.door.data.RiftDataList;
import org.dimdev.dimdoors.item.door.data.condition.Condition;
import org.dimdev.dimdoors.item.door.data.condition.WorldMatchCondition;
import org.dimdev.dimdoors.pockets.PocketGenerator;
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.targets.*;
import org.dimdev.dimdoors.world.ModDimensions;

import java.util.Set;

public class DoorDataDataGen {
    public static void bootstrap(DimDoorsDynamicRegistryProvider.RegistrationHelper ctx) {
        ctx.register(key(ModBlocks.QUARTZ_DOOR), RiftDataList.builder()
                .add(PrivatePocketExitTarget.INSTANCE, Condition.level(ModDimensions.PERSONAL))
                .add(PrivatePocketTarget.INSTANCE, Condition.not(Condition.level(ModDimensions.PERSONAL)))
                .builder()
        );

        var dungeonBuilder = DungeonTarget.builder()
                .acceptedGroups(Set.of(0))
                .newRiftWeight(1.0f)
                .weightMaximum(100.0)
                .coordFactor(1.0)
                .noLinkBack(true)
                .positiveDepthFactor(10000)
                .negativeDepthFactor(160.0)
                .noLink(false);

        var properties = LinkProperties.builder()
                .entranceWeight(0.0f)
                .groups(0, 1)
                .floatingWeight(0.0f)
                .linksRemaining(1)
                .oneWay(false)
                .build();

        ctx.register(key(Blocks.CRIMSON_DOOR), RiftDataList.of(
                dungeonBuilder.dungeonGroup(PocketGenerator.NETHER_DUNGEONS).build(),
                properties,
                Condition.alwaysTrue())
        );

        ctx.register(key(ModBlocks.AMALGAM_DOOR), RiftDataList.of(
                dungeonBuilder.dungeonGroup(PocketGenerator.ALL_DUNGEONS).build(),
                properties,
                Condition.alwaysTrue())
        );

        ctx.register(key(ModBlocks.STONE_DOOR), RiftDataList.of(
                dungeonBuilder.dungeonGroup(PocketGenerator.MYTH_DUNGEONS).build(),
                properties,
                Condition.alwaysTrue())
        );


        ctx.register(key(Blocks.IRON_DOOR), RiftDataList.of(PublicPocketTarget.of(), Condition.alwaysTrue()));

        ctx.register(key(Blocks.OAK_DOOR), RiftDataList.of(EscapeTarget.of(true), Condition.alwaysTrue()));
    }

    private static ResourceKey<RiftDataList> key(Block door) {
        return ResourceKey.create(ModRegistryKeys.DOOR_DATA, door.builtInRegistryHolder().key().location());
    }
}
