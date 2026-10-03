package org.dimdev.dimdoors.pockets

import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.entity.DispenserBlockEntity
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity
import net.minecraft.world.level.storage.loot.LootTable
import org.apache.logging.log4j.Logger
import org.dimdev.dimcore.api.ext.castOrNull
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.api.util.Location.Companion.ofWorld
import org.dimdev.dimdoors.api.util.math.MathUtil
import org.dimdev.dimdoors.block.entity.Rift
import org.dimdev.dimdoors.rift.registry.LinkProperties
import org.dimdev.dimdoors.rift.registry.PocketRegistry.Companion.instance
import org.dimdev.dimdoors.rift.targets.PocketEntranceMarker
import org.dimdev.dimdoors.rift.targets.PocketExitMarker
import org.dimdev.dimdoors.rift.targets.VirtualTarget
import org.dimdev.dimdoors.world.ModLootTables
import org.dimdev.dimdoors.world.pocket.type.Pocket

object TemplateUtils {
//    fun setupEntityPlaceholders(entities: MutableList<CompoundTag?>, entityTag: CompoundTag) {
//        if (entityTag.contains("placeholder")) {
//            val x = entityTag.getDouble("x")
//            val y = entityTag.getDouble("y")
//            val z = entityTag.getDouble("z")
//            val yaw = entityTag.getFloat("yaw")
//            val pitch = entityTag.getFloat("pitch")
//
//            val newTag: CompoundTag?
//            if ("monolith" == entityTag.getString("placeholder")) {
//                val monolith: MonolithEntity = ModEntityTypes.MONOLITH.value().create(null)
//                monolith.setPos(x, y, z)
//                monolith.setYRot(yaw)
//                monolith.pitch = pitch
//                newTag = monolith.saveWithoutId(CompoundTag())
//            } else {
//                throw RuntimeException("Unknown entity placeholder: " + entityTag.getString("placeholder"))
//            }
//            entities.add(newTag)
//        } else {
//            entities.add(entityTag)
//        }
//    }

    fun setupLootTable(world: ServerLevel, randomizable: RandomizableContainerBlockEntity, logger: Logger) {
        val table: ResourceKey<LootTable> = when (randomizable) {
            is DispenserBlockEntity -> {
                logger.debug("Now populating dispenser.")
                ModLootTables.DISPENSER_PROJECTILES
            }

            else -> {
                logger.debug("Now populating chest.")
                ModLootTables.DUNGEON_CHEST
            }
        }

        randomizable.setLootTable(table)
        randomizable.setLootTableSeed(world.getRandom().nextLong())
    }

    fun registerRifts(
        rifts: MutableList<out Rift>,
        linkTo: VirtualTarget<*>?,
        linkProperties: LinkProperties?,
        pocket: Pocket<*, *>
    ) {
        val world: ServerLevel = DimensionalDoors.getWorld(pocket.world)!!
        val entranceWeights = mutableMapOf<Rift, Float>()

        // Add logging to debug
        DimensionalDoors.LOGGER.info("Registering {} rifts for pocket {}", rifts.size, pocket.id)

        for (rift in rifts) {
            rift.data.destination.castOrNull<PocketEntranceMarker>()?.let {
                entranceWeights.put(rift, it.weight)
            }
        }

        if (entranceWeights.isEmpty()) {
            DimensionalDoors.LOGGER.warn("No entrance markers found in pocket {}", pocket.id)
            return
        }

        val selectedEntrance = MathUtil.weightedRandom(entranceWeights)
        DimensionalDoors.LOGGER.info(
            "Selected entrance at {} for pocket {}",
            selectedEntrance!!.riftBlockPos,
            pocket.id
        )

        // Replace entrances with appropriate destinations
        for (rift in rifts) {
            val destination = rift.data.destination

            if (destination is PocketEntranceMarker) {
                if (rift === selectedEntrance) {
                    rift.setDestination(destination.ifDestination)
                    rift.register()

                    val entranceLocation = ofWorld(world, rift.riftBlockPos)
                    instance.addPocketEntrance(pocket, entranceLocation)
                    DimensionalDoors.LOGGER.info(
                        "Registered pocket entrance at {} {}",
                        entranceLocation.worldId.location(),
                        entranceLocation.blockPos
                    )
                } else {
                    rift.setDestination(destination.otherwiseDestination)
                }
            }
        }

        for (rift in rifts) {
            if (rift.data.destination is PocketExitMarker) {
                if (linkProperties != null) rift.properties = linkProperties

                val exitDestination = (if (rift.properties?.isOneWay != true) linkTo else VirtualTarget.NoneTarget) ?: run {
                    DimensionalDoors.LOGGER.warn("No exit link target supplied for rift at {} in pocket {}", rift.riftBlockPos, pocket.id)
                    VirtualTarget.NoneTarget
                }

                rift.setDestination(exitDestination)

                if (exitDestination !== VirtualTarget.NoneTarget) {
                    exitDestination.location = ofWorld(world, rift.riftBlockPos)
                }
            }
        }

        for (rift in rifts) {
            rift.register()
            rift.markStateChanged()
        }
    }

    fun linkRifts(from: Location?, to: Location?) {
        if (to == null) return
        val fromBe = from?.blockEntity?.castOrNull<Rift>() ?: return

        fromBe.setDestination(to.asTarget())
        fromBe.markStateChanged()

        val toBe = to.blockEntity?.castOrNull<Rift>() ?: return
        val properties = toBe.properties ?: return

        toBe.properties = properties.withLinksRemaining(properties.linksRemaining - 1)
        toBe.updateProperties()
        toBe.markStateChanged()
    }
}

    //    public static void replacePlaceholders(Schematic schematic, WorldGenLevel world) {
    //        // Replace placeholders (some schematics will contain them)
    //        List<CompoundTag> blockEntities = new ArrayList<>();
    //        for (CompoundTag blockEntityTag : schematic.getBlockEntities()) {
    //            if (blockEntityTag.contains("placeholder")) {
    //                int x = blockEntityTag.getInt("x");
    //                int y = blockEntityTag.getInt("y");
    //                int z = blockEntityTag.getInt("z");
    //                BlockPos pos = new BlockPos(x, y, z);
    //
    //                CompoundTag newTag = new CompoundTag();
    //                EntranceRiftBlockEntity rift = new EntranceRiftBlockEntity(pos, Schematic.getBlockSample(schematic).getBlockState(pos));
    //                switch (blockEntityTag.getString("placeholder")) {
    //                    case "deeper_depth_door" -> {
    //                        rift.setProperties(DefaultDungeonDestinations.POCKET_LINK_PROPERTIES);
    //                        rift.setDestination(DefaultDungeonDestinations.getDeeperDungeonDestination());
    //                        rift.saveAdditional(newTag, world.registryAccess());
    //                    }
    //                    case "less_deep_depth_door" -> {
    //                        rift.setProperties(DefaultDungeonDestinations.POCKET_LINK_PROPERTIES);
    //                        rift.setDestination(DefaultDungeonDestinations.getShallowerDungeonDestination());
    //                        rift.saveAdditional(newTag, world.registryAccess());
    //                    }
    //                    case "overworld_door" -> {
    //                        rift.setProperties(DefaultDungeonDestinations.POCKET_LINK_PROPERTIES);
    //                        rift.setDestination(DefaultDungeonDestinations.getOverworldDestination());
    //                        rift.saveAdditional(newTag, world.registryAccess());
    //                    }
    //                    case "entrance_door" -> {
    //                        rift.setProperties(DefaultDungeonDestinations.POCKET_LINK_PROPERTIES);
    //                        rift.setDestination(DefaultDungeonDestinations.getTwoWayPocketEntrance());
    //                        rift.saveAdditional(newTag, world.registryAccess());
    //                    }
    //                    case "gateway_portal" -> {
    //                        rift.setProperties(DefaultDungeonDestinations.OVERWORLD_LINK_PROPERTIES);
    //                        rift.setDestination(DefaultDungeonDestinations.getGateway());
    //                        rift.saveAdditional(newTag, world.registryAccess());
    //                    }
    //                    default -> throw new RuntimeException("Unknown block entity placeholder: " + blockEntityTag.getString("placeholder"));
    //                }
    //                rift.setWorld(world.getLevel());
    //                blockEntities.add(newTag);
    //            } else {
    //                blockEntities.add(blockEntityTag);
    //            }
    //        }
    //        schematic.setBlockEntities(blockEntities);
    //
    //        List<CompoundTag> entities = new ArrayList<>();
    //        for (CompoundTag entityTag : schematic.getEntities()) {
    //            TemplateUtils.setupEntityPlaceholders(entities, entityTag);
    //        }
    //        schematic.setEntities(entities);
    //    }

