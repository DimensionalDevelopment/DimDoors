package org.dimdev.dimdoors.datagen

import com.mojang.serialization.MapCodec
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.Util
import net.minecraft.core.Holder
import net.minecraft.core.HolderLookup
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.contents.TranslatableContents
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.decoration.PaintingVariant
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.material.Fluid
import org.dimdev.dimdoors.ModRegistries
import org.dimdev.dimdoors.block.ModBlocks
import org.dimdev.dimdoors.enchantment.ModEnchants
import org.dimdev.dimdoors.entity.ModEntityTypes
import org.dimdev.dimdoors.entity.stat.ModStats
import org.dimdev.dimdoors.fluid.ModFluids
import org.dimdev.dimdoors.item.ArmorSet
import org.dimdev.dimdoors.item.ModItems
import org.dimdev.dimdoors.painting.ModPaintings
import org.dimdev.dimdoors.rift.targets.VirtualTarget
import org.dimdev.dimdoors.rift.targets.VirtualTargets
import org.dimdev.dimdoors.world.ModBiomes
import java.util.Locale
import java.util.concurrent.CompletableFuture
import kotlin.jvm.optionals.getOrNull

class LanguageProvider(dataOutput: FabricDataOutput, registryLookup: CompletableFuture<HolderLookup.Provider>) :
    AbstractLanguageProvider(dataOutput, registryLookup, "en_us") {
    public override fun generateTranslations() {
        add(ModItems.DECAY.holder.value())
        add(ModItems.DIMENSIONAL_DOORS.holder.value())

        addHolder(ModBlocks.GOLD_DOOR)
        addHolder(ModBlocks.QUARTZ_DOOR)
        addHolder(ModBlocks.STONE_DOOR)
        addHolder(ModBlocks.DIMENSIONAL_PORTAL)
        addHolder(ModBlocks.BLACK_FABRIC)
        addHolder(ModBlocks.WHITE_FABRIC)
        addHolder(ModBlocks.ORANGE_FABRIC)
        addHolder(ModBlocks.MAGENTA_FABRIC)
        addHolder(ModBlocks.LIGHT_BLUE_FABRIC)
        addHolder(ModBlocks.LIGHT_GRAY_FABRIC)
        addHolder(ModBlocks.YELLOW_FABRIC)
        addHolder(ModBlocks.LIME_FABRIC)
        addHolder(ModBlocks.PINK_FABRIC)
        addHolder(ModBlocks.GRAY_FABRIC)
        addHolder(ModBlocks.CYAN_FABRIC)
        addHolder(ModBlocks.PURPLE_FABRIC)
        addHolder(ModBlocks.BLUE_FABRIC)
        addHolder(ModBlocks.BROWN_FABRIC)
        addHolder(ModBlocks.GREEN_FABRIC)
        addHolder(ModBlocks.RED_FABRIC)
        addHolder(ModBlocks.BLACK_ANCIENT_FABRIC)
        addHolder(ModBlocks.WHITE_ANCIENT_FABRIC)
        addHolder(ModBlocks.ORANGE_ANCIENT_FABRIC)
        addHolder(ModBlocks.MAGENTA_ANCIENT_FABRIC)
        addHolder(ModBlocks.LIGHT_BLUE_ANCIENT_FABRIC)
        addHolder(ModBlocks.LIGHT_GRAY_ANCIENT_FABRIC)
        addHolder(ModBlocks.YELLOW_ANCIENT_FABRIC)
        addHolder(ModBlocks.LIME_ANCIENT_FABRIC)
        addHolder(ModBlocks.PINK_ANCIENT_FABRIC)
        addHolder(ModBlocks.GRAY_ANCIENT_FABRIC)
        addHolder(ModBlocks.CYAN_ANCIENT_FABRIC)
        addHolder(ModBlocks.PURPLE_ANCIENT_FABRIC)
        addHolder(ModBlocks.BLUE_ANCIENT_FABRIC)
        addHolder(ModBlocks.BROWN_ANCIENT_FABRIC)
        addHolder(ModBlocks.GREEN_ANCIENT_FABRIC)
        addHolder(ModBlocks.RED_ANCIENT_FABRIC)
        addHolder(ModBlocks.DECAYED_BLOCK)
        addHolder(ModBlocks.UNFOLDED_BLOCK)
        addHolder(ModBlocks.UNWARPED_BLOCK)
        addHolder(ModBlocks.UNRAVELLED_BLOCK)
        addHolder(ModBlocks.UNRAVELLED_FABRIC)
        addHolder(ModBlocks.DETACHED_RIFT)
        addHolder(ModBlocks.ETERNAL_FLUID)
        addHolder(ModBlocks.SOLID_STATIC)
        addHolder(ModBlocks.TESSELATING_LOOM)
        addHolder(ModBlocks.REALITY_SPONGE)
        addHolder(ModBlocks.DRIFTWOOD_WOOD)
        addHolder(ModBlocks.DRIFTWOOD_LOG)
        addHolder(ModBlocks.DRIFTWOOD_PLANKS)
        addHolder(ModBlocks.DRIFTWOOD_LEAVES)
        addHolder(ModBlocks.DRIFTWOOD_SAPLING)
        addHolder(ModBlocks.DRIFTWOOD_FENCE)
        addHolder(ModBlocks.DRIFTWOOD_GATE)
        addHolder(ModBlocks.DRIFTWOOD_BUTTON)
        addHolder(ModBlocks.DRIFTWOOD_SLAB)
        addHolder(ModBlocks.DRIFTWOOD_STAIRS)
        addHolder(ModBlocks.DRIFTWOOD_DOOR)
        addHolder(ModBlocks.DRIFTWOOD_TRAPDOOR)
        addHolder(ModBlocks.AMALGAM_BLOCK)
        addHolder(ModBlocks.AMALGAM_DOOR)
        addHolder(ModBlocks.AMALGAM_TRAPDOOR)
        addHolder(ModBlocks.RUST)
        addHolder(ModBlocks.AMALGAM_SLAB)
        addHolder(ModBlocks.AMALGAM_STAIRS)
        addHolder(ModBlocks.AMALGAM_ORE)
        addHolder(ModBlocks.CLOD_BLOCK)
        addHolder(ModBlocks.CLOD_ORE)
        addHolder(ModBlocks.UNRAVELED_SPIKE)
        addHolder(ModBlocks.PALE_SAND)
        addHolder(ModBlocks.DARK_SAND_LAYER)
        addHolder(ModBlocks.DARK_SAND)
        addHolder(ModBlocks.LINT_LAYER)
        addHolder(ModBlocks.STONE_SLAB)
        addHolder(ModBlocks.STONE_STAIRS)
        addHolder(ModBlocks.STONE_WALL)

        ModBlocks.DecayGroupSet.SETS.forEach(::addBlockSet)

        addHolder(ModBlocks.GRITTY_STONE)
        addHolder(ModBlocks.LEAK)

        add(ModItems.RIFT_KEY, "Rift Key")

        addDoorAutoGen(Blocks.IRON_DOOR, "Public Door") {
            info(0, "Place on the block under a rift")
            info(1, "to activate that rift or place")
            info(2, "anywhere else to create a")
            info(3, "pocket dimension.")
        }

        addDoorAutoGen(ModBlocks.STONE_DOOR, "Dungeon Door") {
            info(0, "Place on the block under a rift")
            info(1, "to activate that rift or place")
            info(2, "anywhere else to create a")
            info(3, "dungeon.")
        }

        add(ModItems.RIFT_REMOVER) {
            add("closing", "The rift will close soon")
            add("already_closing", "This rift is already closing")
            info(0, "Use near exposed rift")
            info(1, "to remove it and")
            info(2, "any nearby rifts.")
        }

        addDoorAutoGen(ModBlocks.GOLD_DOOR, "Dimensional Gold Door") {
            info(0, "Similar to a Dimensional Door")
            info(1, "but shinier")
        }

        add(ModBlocks.DIALING_DOOR) {}

        addArmor(ModItems.WORLD_THREAD_ARMOR, "Woven World Thread")

        add(ModItems.RIFT_SIGNATURE) {
            add("stored", "Location stored")
            add("created", "Rift created")

            add("bound") {
                info(1, "Leads to (%d, %d, %d)")
                info(0, "at dimension %d")
            }

            add("unbound") {
                info(0, "First click stores a location;.")
                info(1, "second click creates a pair of")
                info(2, "rifts linking the two locations.")
            }
        }

        addHolder(ModItems.STABILIZED_RIFT_SIGNATURE) {
            add("stored", "Location stored")
            add("created", "Rift created")

            add("bound") {
                info(0, "Leads to (%d, %d, %d)")
                info(1, "at dimension %d")
            }
            add("unbound") {
                info(0, "First click stores a location,.")
                info(1, "other clicks create rifts linking")
                info(2, "the first and last locations together.")
            }
        }

        add(ModItems.RIFT_CONFIGURATION_TOOL) {
            //TODO: Figure out better working later.
            info(0, "Shift right click on")
            info(1, "a door to set")
            info(2, "to an id for")
            info(3, "pocket config in")
            info(4, "a datapack.")
        }


        add(ModItems.RIFT_STABILIZER) {
            info("Use on a rift's core to stop its growth.")
            add("stabilized", "The rift has been stabilized and will stop growing")
            add("already_stabilized", "This rift is already stable")
        }

        add(ModItems.RIFT_BLADE) {
            add("rift_miss", "You can only use this item on a rift's core")
            info(0, "Opens temporary doors on rifts")
            info(1, "and has a teleport attack.")
        }

        add(ModItems.WORLD_THREAD, "World Thread")
        add(ModItems.INFRANGIBLE_FIBER, "Infrangible Fiber")
        add(ModItems.FRAYED_FILAMENT, "Frayed Filament")
        add(ModItems.STABLE_FABRIC, "Stable Fabric")

        add(ModItems.FARSHOT, "Farshot")

        addDoorAutoGen(Blocks.OAK_DOOR, "Escape Door") {
            info(0, "Place on the block under a rift")
            info(1, "to create a portal, or place anywhere")
            info(2, "in a pocket dimension to exit.")
        }

        addDoorAutoGen(ModBlocks.QUARTZ_DOOR, "Personal Door") {
            info("Creates a pathway to your personal pocket.")
        }

        addDoorAutoGen(ModBlocks.AMALGAM_DOOR, "Myth Door") {
            info(0, "Create a pathway to")
            info(1, "a dungeon so mythical")
            info(2, "that a normal door")
            info(3, "can not maintain.")
        }

        addDoorAutoGen(Blocks.CRIMSON_DOOR, "Myth Door") {
            info(0, "Create a gateway to")
            info(1, "to an infernal dungeon.")
        }

        addDisc(ModItems.CREEPY_RECORD, "Stevenrs11 - Creepy")

        add(ModItems.ETERNAL_FLUID_BUCKET)
        add(ModItems.LEAK_BUCKET)

        addDisc(ModItems.WHITE_VOID_RECORD, "Lachney - White Void")

        add(ModItems.DIMENSIONAL_ERASER) {
            info("Erases entities")
        }

        add(ModItems.MONOLITH_SPAWNER, "Monolith Spawner")
        add(ModItems.MASK_WAND, "Mask Wand")
        add(ModItems.MASK_SHARD, "Mask Shard")
        add(ModItems.FUZZY_FIREBALL, "Fuzzy Fireball")
        add(ModItems.FABRIC_OF_FINALITY, "Fabric of Finality")
        add(ModItems.LIMINAL_LINT, "Liminal Lint")
        add(ModItems.ENDURING_FIBERS, "Enduring Fibers")
        add(ModItems.RIFT_PEARL, "Rift Pearl")
        //        add(ModItems.FABRIC_OF_REALITY, "Fabric of Reality");
        add(ModItems.AMALGAM_LUMP, "Amalgam Lump")
        add(ModItems.CLOD, "Clod")

        addArmor(ModItems.GARMENT_OF_REALITY_ARMOR, "Garment of Reality")

        addDisc(ModItems.THEY_STARE_BACK_RECORD, "Firel - They Stare Back")

        add(ModFluids.ETERNAL_FLUID)
        add(ModFluids.LEAK)

        add(ModEntityTypes.MONOLITH)
        add(ModEntityTypes.MASK)

        add("commands") {
            builder.add("commands.dimteleport.usage", "/dimteleport <dimension> <x> <y> <z> [yaw] [pitch]")
            add("fabricconvert") {
                builder.add("commands.fabricconvert.usage", "/fabricconvert")
                builder.add("commands.fabricconvert.success", "All fabric of reality has been converted to black.")
            }

            add("pocket") {
                builder.add("commands.pocket.usage", "/pocket <group> <name> [setup]")
                builder.add("commands.pocket.group_not_found", "Group %s not found")
            }

            add("dimdoors") {
                builder.add("pocket.template_not_found", "Template %s not found")
                builder.add("saveschem.usage", "/saveschem <name>")
                builder.add("saveschem.success", "Pocket %s has been successfully saved")
            }

            add("generic.dimdoors.not_in_pocket_dim", "You must be in a pocket dimension to use this command.")
            add("generic.dimdoors.not_in_pocket", "You must be in a pocket to use this command.")
            add("generic.unknownValue", "Unknown value '%s'")
            add("pocket.unknownPocketTemplate", "Unknown Pocket Template '%s'")
            add("pocket.placedSchem", "Placed schematic %s at %s in world %s")
            add("pocket.loadedSchem", "Loaded schematic %s to clipboard. Paste it using //paste")
            add("pocket.log.creation.off", "Toggled logging of pocket creation off.")
            add("pocket.log.creation.on", "Toggled logging of pocket creation on.")
            add("pocket.log.creation.generating", "Generating pocket from template '%s' at location %s %s %s")
        }

        add("rifts") {
            add("unlinked1", "This rift doesn't lead anywhere")
            add("unlinked2", "This rift has closed")
            add("isLocked", "This rift is locked")
            add("cantUnlock", "Can't unlock this door")
            add("unlocked", "Unlocked")
            add("locked", "Locked")

            add("destinations") {
                add("escape.cannot_escape_limbo", "Nice try, but you'll need to either die or find some eternal fabric to get out of Limbo.")
                add("escape.not_in_pocket_dim", "You can only use this to escape from a pocket dimension!")
                add("escape.did_not_use_rift", "You didn't use a rift to enter the pocket dimension, so you ended up in Limbo!")
                add("escape.rift_has_closed", "The rift you used to enter the pocket dimension has closed and you ended up in Limbo!")
                add("private_pocket_exit.did_not_use_rift", "You didn't use a rift to enter the pocket dimension and you ended up in Limbo!")
                add("private_pocket_exit.rift_has_closed", "The rift you used to enter the pocket dimension has closed and you ended up in Limbo!")
                add("dialing.cant_use_dialing_door_in_dialing_pocket", "You can't use a dialing door in a dialing pocket.")
            }

            add("entrances.rift_too_close", "Placing a door this close to a tear in the world would be dangerous. Shift-right-click to place anyway, or place it on the rift's core (tesseract) to bind it to the rift.")
            add("entrances.cannot_be_placed_on_rift", "This type of door can't be placed on a rift.")
        }


        add("tools") {
            add("rift_miss", "You can only use this item on a rift's core.")
            add("signature_blocked", "Usage of the signature was blocked.")
            add("target_became_block", "Failed, there is now a block at the stored location.")
        }

        add(VirtualTargets.AVAILABLE_LINK, "Random")

        add(VirtualTargets.ESCAPE, "Escape")
        add(VirtualTargets.RIFT_REFERENCE, "Rift Reference")
        add(VirtualTargets.LIMBO, "Limbo")
        add(VirtualTargets.PUBLIC_POCKET, "Public Pocket")
        add(VirtualTargets.POCKET_ENTRANCE, "Pocket Entrance")
        add(VirtualTargets.POCKET_EXIT, "Pocket Exit")
        add(VirtualTargets.PRIVATE, "Private Pocket Entrance")
        add(VirtualTargets.PRIVATE_POCKET_EXIT, "Private Pocket Exit")
        add(VirtualTargets.ID_MARKER, "Id Marker")
        add(VirtualTargets.UNSTABLE, "Unstable")
        add(VirtualTargets.NONE, "None")

        add("category.dimdoors") {
            add("tesselating", "Tesselating")
            add("decays_into", "Decays Into")
        }

        add("dimdoors.destination", "Destination type")

        add("config") {
            add("dimdoors") {
                addTitle("Dimensional Doors")
                add("general") {
                    addCategory("General Settings")
                    addOption(
                        "depthSpreadFactor",
                        "Depth Spread Factor",
                        "The scale of the dispersion when escaping from a pocket or limbo, in blocks/depth. Limbo is treated as depth 50."
                    )
                    addOption(
                        "riftCloseSpeed",
                        "Rift Close Speed",
                        "The speed at which rifts close when using the rift remover, in units of rift size per tick."
                    )
                    addOption(
                        "riftGrowthSpeed",
                        "Rift Growth Speed",
                        "The speed at which rifts grow, in units of rift size per tick."
                    )
                    addOption(
                        "enableRiftDecay",
                        "Rift Growth Speed",
                        "When true, blocks around a growing rift will unravel over time."
                    )
                    addOption(
                        "teleportOffset",
                        "Teleport Offset",
                        "Distance in blocks to teleport the player in front of the dimensional door."
                    )
                    addOption(
                        "riftBoundingBoxInCreative",
                        "Rift Bounding Box in Creative",
                        "When true, shows the bounding boxes of floating rifts when the player is in creative."
                    )
                    addOption(
                        "endermanSpawnChance",
                        "Enderman spawn chance",
                        "The chance that an enderman spawns at a detached rift."
                    )
                    addOption(
                        "endermanAggressiveChance",
                        "Enderman aggressive chance",
                        "The chance that an enderman spawned by a detached rift attacks the closest player."
                    )
                    addOption(
                        "enableDebugMessages",
                        "Enable Debug Messages",
                        "When true, debug messages will be printed."
                    )
                }

                add("doors") {
                    addCategory("Doors Settings")
                    addOption(
                        "closeDoorBehind",
                        "Close Door Behind",
                        "When true, Dimensional Doors will automatically close when the player enters their portal."
                    )
                    addOption("doorList", "Doors", "Set overrides for enabling/disabling certain doors")
                    addOption(
                        "doorList.mode",
                        "Mode",
                        "Enable - Only generate dimensional variants of these doors. Disable - Prevent generating dimensional variants of these doors"
                    )
                    addOption(
                        "doorList.doors",
                        "Doors",
                        "A list of block ids for doors. If the door's item id is different than the block id, add that as well."
                    )
                    addOption(
                        "placeRiftsInCreativeMode",
                        "Place Rifts in Creative Mode",
                        "If enabled, breaking a door in creative mode will spawn a rift"
                    )
                }

                add("pockets") {
                    addCategory("Pocket Settings")
                    addOption(
                        "pocketGridSize",
                        "Pocket Grid Size",
                        "Sets how many chunks apart all pockets in any pocket dimensions should be placed."
                    )
                    addOption(
                        "maxPocketSize",
                        "Maximum Pocket Size",
                        "Sets the maximum size of any pocket. A size of x will allow for pockets up to (x + 1) * (x + 1) chunks."
                    )
                    addOption(
                        "privatePocketSize",
                        "Private Pocket Size",
                        "Sets the minimum size of a newly created Private Pocket. If this is set to any value bigger than maxPocketSize, the value of maxPocketSize will be used instead."
                    )
                    addOption(
                        "publicPocketSize",
                        "Public Pocket Size",
                        "Sets the minimum size of a newly created Public Pocket. If this is set to any value bigger than privatePocketSize, the value of privatePocketSize will be used instead."
                    )
                    addOption(
                        "defaultWeightEquation",
                        "Default Weight Equation",
                        "Sets the equation to be used to compute weight when there is no / invalid weight equation present in the pocket generator json"
                    )
                    addOption(
                        "fallbackWeight",
                        "Fallback weight",
                        "Sets the fallback weight to be used if the default weight equation fails."
                    )
                    addOption(
                        "classicPocketsResourcePackActivationType",
                        "Classic Resource Pack Activation Type",
                        "Default - Disabled but can be enabled, Default Enabled - Enabled but can be disabled, Always Enabled - Can not be disabled"
                    )
                    addOption(
                        "defaultPocketsResourcePackActivationType",
                        "Default Resource Pack Activation Type",
                        "Default - Disabled but can be enabled, Default Enabled - Enabled but can be disabled, Always Enabled - Can not be disabled"
                    )
                    addOption(
                        "asyncWorldEditPocketLoading",
                        "Async WorldEdit Pocket Loading",
                        "Sets loading pockets to your WorldEdit clipboard asynchronous or synchronous. Only affects when WorldEdit is installed."
                    )
                    addOption(
                        "blocksColoredPerDye",
                        "Blocks Colored Per Dye",
                        "The amount of blocks covered by a single dye whe dyeing a private pocket."
                    )
                }

                add("world") {
                    addCategory("Worldgen Settings")
                    addOption(
                        "clusterGenChance",
                        "Cluster Generation Chance",
                        "Sets the chance (out of 1) that a cluster of rifts will generate in a given chunk."
                    )
                    addOption(
                        "gatewayGenChance",
                        "Gateway Generation Chance",
                        "Sets the chance (out of 1) that a dimensional gateway will generate in a given chunk."
                    )
                    addOption(
                        "clusterDimBlacklist",
                        "Cluster Dimension Blacklist",
                        "Dimension Blacklist for the generation of Rift Scar clusters. Add a dimension ID here to prevent generation in certain dimensions."
                    )
                    addOption(
                        "gatewayDimBlacklist",
                        "Gateway Dimension Blacklist",
                        "Dimension Blacklist for the generation of Dimensional Portal gateways. Add a dimension ID here to prevent generation in certain dimensions."
                    )
                }

                add("dungeons") {
                    addCategory("Dungeon Settings")
                    addOption(
                        "maxDungeonDepth",
                        "Maximum Dungeon Depth",
                        "The depth at which limbo is located. If a Rift reaches any deeper than this while searching for a new destination, the player trying to enter the Rift will be sent straight to Limbo."
                    )
                }

                add("monoliths") {
                    addCategory("Monolith Settings")
                    addOption(
                        "dangerousLimboMonoliths",
                        "Dangerous Limbo Monoliths",
                        "When true, Monoliths in Limbo attack the player and deal damage."
                    )
                    addOption(
                        "monolithTeleportation",
                        "Monolith Teleportation",
                        "When true, being exposed to the gaze of Monoliths for too long, will cause the player to be teleported to the void above Limbo."
                    )
                }

                add("limbo") {
                    addCategory("Limbo Settings")
                    addOption(
                        "universalLimbo",
                        "Universal Limbo",
                        "When true, players are also teleported to Limbo when they die in any non-Pocket Dimension (except Limbo itself). Otherwise, players only go to Limbo if they die in a Pocket Dimension."
                    )
                    addOption(
                        "hardcoreLimbo",
                        "Hardcore Limbo",
                        "When true, a player dying in Limbo will respawn in Limbo, making Eternal Fluid or Golden Dimensional Doors the only way to escape Limbo."
                    )
                    addOption(
                        "limboBlocksCorruptingExitWorldAmount",
                        "Exit World Decay Radius",
                        "The radius around a player in which blocks can decay upon exiting limbo."
                    )
                    addOption(
                        "worldsLeadingToLimbo",
                        "Worlds Leading to Limbo",
                        "Defines a blacklist/whitelist of worlds that will send the player to limbo upon death."
                    )
                    addOption(
                        "worldsLeadingToLimbo.list",
                        "List of world ids",
                        "List of the ids for worlds in the blacklist/whitelist."
                    )
                    addOption(
                        "worldsLeadingToLimbo.blacklist",
                        "Is it a blacklist?",
                        "Boolean that determines if list is a blacklist or white list for worlds."
                    )
                    addOption(
                        "limboReturnDistance",
                        "Limbo Return Radius",
                        "Distance from spawn that limbo returns you"
                    )
                    addOption(
                        "escapeTargetWorld",
                        "Escape To World",
                        "Defines the id of the world players will spawn in upon exiting Limbo.  Leaving this blank will spawn players in the world their respawn point is in."
                    )
                    addOption(
                        "escapeTargetWorldYSpawn",
                        "Escape To World Y Level",
                        "Defines the Y coordinate the player will spawn at when using \"Escape To World\""
                    )
                    addOption(
                        "escapeToWorldSpawn",
                        "Escape to World Spawn",
                        "Boolean that determines if players exiting limbo will return relative to the worldspawn instead.  If true, escapeTargetWorld has no effect."
                    )
                    addOption(
                        "limboReturnDistanceMax",
                        "Max Limbo Return Distance",
                        "Defines the maximum distance out the possible return locations can be from the target center.\n Setting both return distances to 0 cause the player to exactly appear at target center."
                    )
                    addOption(
                        "limboReturnDistanceMin",
                        "Min Limbo Return Distance",
                        "Defines the minimum distance out the possible return locations can be from the target center.\n Setting both return distances to 0 cause the player to exactly appear at target center."
                    )
                    addOption(
                        "decaySurroundings",
                        "Decay Surroundings",
                        "Does escaping limbo cause limbo decay around the location?"
                    )
                    addOption(
                        "tryPlayerBedSpawn",
                        "Try Player Bed Spawn",
                        "When true, the bed spawn of the player will be used as the center of possible return locations if available."
                    )
                    addOption(
                        "defaultToWorldSpawn",
                        "Default To World Spawn",
                        "When true, the world spawn of the world the player is escaping from limbo to will be used as the center of possible return location."
                    )
                    addOption(
                        "genericDesthMesages",
                        "Generic Death Messsages",
                        "When true, instead of using custom death messages, \\\"...and was sent to Limbo\\\" will be added to the end of regular death messages.\""
                    )
                }

                add("graphics") {
                    addCategory("Graphics Settings")
                    addOption(
                        "highlightRiftCoreFor",
                        "Time to Highlight Rift Core",
                        "How long, in milliseconds, the rift's core (tesseract animation) should be shown for when attempting to place a door near a large rift but not directly on it. Set to -1 to disable."
                    )
                    addOption(
                        "showRiftCore",
                        "Always Show Rift Cores",
                        "Set this to true to always show rifts' cores (tesseract animation)."
                    )
                    addOption(
                        "riftSize",
                        "Rift Size",
                        "Multiplier affecting how large rifts should be rendered, 1 being the default size."
                    )
                    addOption(
                        "riftJitter",
                        "Rift Jitter",
                        "Multiplier affecting how much rifts should jitter, 1 being the default size."
                    )
                }
                add("decay") {
                    addCategory("Decay Settings")
                    addOption(
                        "decaySpreadChance",
                        "Decay Spread Chance",
                        "Chance for Unravelled Fabric random ticks to attempt decay spread."
                    )
                    addOption(
                        "decayDelay",
                        "Decay Delay",
                        "In minecraft ticks (20 per second on a healthy server or game), the delay between when a queued decay is scheduled and it fired."
                    )
                    addOption(
                        "decaysIntoAir",
                        "dimdoors.config.option.decay.decaysIntoAir.tooltip",
                        "When true, rifts will turn blocks into air instead of decay and has a chanece of dropping world thread."
                    )
                }
            }
        }

        add("advancement") {
            add("dimdoors") {
                addDesc("root", "Dimensional Doors", "Venture into the depths")
                addDesc("dark_ostiology", "Dark Ostiology", "Place an Oak Dimensional Door")
                addDesc("darklight", "Darklight", "Obtain Fabric of Reality")
                addDesc("door_to_adventure", "Door to Adventure", "Enter a dungeon")
                addDesc("enter_limbo", "Limbo", "Enter Limbo")
                addDesc("hole_in_the_sky", "Hole in the Sky", "Encounter a Rift")
                addDesc("home_away_from_home", "Home away from Home", "Enter your private pocket")
                addDesc("lost_and_found", "Lost and Found", "Open a chest in a Dungeon")
                addDesc("out_of_time", "Out of Time", "Set your spawn point in a pocket dimension")
                addDesc("public_pocket", "Public Pocket", "Enter a Public Pocket")
                addDesc("string_theory", "String Theory", "Collect World Thread")
                addDesc("world_unfurled", "World Unfurled", "Collect Unravelled Fabric")
                addDesc("unravelled_but_immutable", "Unravelled But Immutable", "Obtain Infrangible Fiber")
                addDesc("fuzzy_unreality", "Fuzzy Unreality", "Obtain Frayed Filament")
            }
            add("mode") {
                add("enable", "Enable")
                add("disable", "Disable")
            }
            add("pocket") {
                add("dyeAlreadyAbsorbed", "The pocket is already that color, so the rift didn't absorb the dye.")
                add("pocketHasBeenDyed", "The pocket has been dyed %s.")
                add("remainingNeededDyes", "The pocket has %s/%s of the dyes needed to be colored %s.")
            }
        }


        add(
            "argument.dimdoors.schematic.invalidNamespace",
            "Invalid schematic namespace. Expected one of %s, found %s."
        )
        add("command.dimdoors.schematicv2.unknownSchematic", "Unknown schematic \"%s\" in namespace \"%s\" ")

        add(ModBiomes.PUBLIC_BLACK_VOID_KEY, "Black void (Public Pockets)")
        add(ModBiomes.DUNGEON_DANGEROUS_BLACK_VOID_KEY, "Dangerous Black void (Dungeon Pockets)")
        add(ModBiomes.LIMBO_KEY, "Limbo")
        add(ModBiomes.PERSONAL_WHITE_VOID_KEY, "White void (Private Pockets)")

        add("limbo") {
            add("death") {
                add("fell") {
                    add("accident") {
                        add("ladder", $$"%1$s fell off a ladder and fell into limbo")
                        add("vines", $$"%1$s fell off some vines and fell into limbo")
                        add("weeping_vines", $$"%1$s fell off some weeping vines and fell into limbo")
                        add("twisting_vines", $$"%1$s fell off some twisting vines and fell into limbo")
                        add("scaffolding", $$"%1$s fell off scaffolding and fell into limbo")
                        add("other_climbable", $$"%1$s fell while climbing and fell into limbo")
                        add("generic", $$"%1$s fell from a high place and fell into limbo")
                    }
                    add("killer", $$"%1$s was doomed to fall and fell into limbo")
                    add("assist", $$"%1$s was doomed to fall by %2$s and fell into limbo")
                    add("assist.item", $$"%1$s was doomed to fall by %2$s using %3$s and fell into limb")
                    add("finish", $$"%1$s fell too far and was sent to limbo by %2$s")
                    add("finish.item", $$"%1$s fell too far and was finished by %2$s using %3$s and fell into limbo")
                }
                add("attack") {
                    add("lightningBolt", $$"%1$s was struck by lightning and was sent to limbo")
                    add("lightningBolt.player", $$"%1$s was struck by lightning whilst fighting %2$s and was sent to limbo")
                    add("inFire", $$"%1$s went to Limbo in flames")
                    add("inFire.player", $$"%1$s walked into fire whilst fighting %2$s and was sent to Limbo")
                    add("onFire", $$"%1$s burned to Limbo")
                    add("onFire.player", $$"%1$s was burnt to a crisp whilst fighting %2$s and was sent tp Limbo")
                    add("lava", $$"%1$s tried to swim in lava and sank into Limbo and sank into Limbo")
                    add("lava.player", $$"%1$s tried to swim in lava to escape %2$s and sank into Limbo")
                    add("hotFloor", $$"%1$s discovered the floor was lava and sank into Limbo")
                    add("hotFloor.player", $$"%1$s walked into danger zone due to %2$s and sank into Limbo")
                    add("inWall", $$"%1$s suffocated into Limbo")
                    add("inWall.player", $$"%1$s suffocated into Limbo whilst fighting %2$s")
                    add("cramming", $$"%1$s was squished too much ans sent to Limbo")
                    add("cramming.player", $$"%1$s was squashed by %2$s")
                    add("drown", $$"%1$s drowned and sank into Limbo")
                    add("drown.player", $$"%1$s drowned whilst trying to escape %2$s and sank into Limbo")
                    add("starve", $$"%1$s starved to death and shriveled into Limbo")
                    add("starve.player", $$"%1$s starved to death whilst fighting %2$s and shriveled into Limbo")
                    add("cactus", $$"%1$s pricked a hole in reality")
                    add("cactus.player", $$"%1$s walked into a cactus whilst trying to escape %2$s and was sent to Limbo")
                    add("generic", $$"%1$s was sent to Limbo")
                    add("generic.player", $$"%1$s was sent to Limbo because of %2$s")
                    add("explosion", $$"%1$s was blown to Limbo")
                    add("explosion.player", $$"%1$s was blown to Limbo by %2$s")
                    add("explosion.player.item", $$"%1$s was blown to Limbo by %2$s using %3$s")
                    add("magic", $$"%1$s was cast into Limbo by magic")
                    add("magic.player", $$"%1$s was cast into Limbo by magic whilst trying to escape %2$s")
                    add("even_more_magic", $$"%1$s was cast into Limbo by even more magic")
                    add("message_too_long", "Actually, message was too long to deliver fully. Sorry! Here's stripped version, %s")
                    add("wither", $$"%1$s withered into Limbo")
                    add("wither.player", $$"%1$s withered into Limbo whilst fighting %2$s")
                    add("witherSkull", $$"%1$s was shot by a skull into Limbo from %2$s")
                    add("anvil", $$"%1$s was squashed into Limbo by a falling anvil")
                    add("anvil.player", $$"%1$s was squashed into Limbo by a falling anvil whilst fighting %2$s")
                    add("fallingBlock", $$"%1$s was squashed into Limbo by a falling block")
                    add("fallingBlock.player", $$"%1$s was squashed into Limbo by a falling block whilst fighting %2$s")
                    add("stalagmite", $$"%1$s was impaled into Limbo on a stalagmite")
                    add("stalagmite.player", $$"%1$s was impaled into Limbo on a stalagmite whilst fighting %2$s")
                    add("fallingStalactite", $$"%1$s was skewered into Limbo by a falling stalactite")
                    add("fallingStalactite.player", $$"%1$s was skewered into Limbo by a falling stalactite whilst fighting %2$s")
                    add("mob", $$"%1$s was slain by %2$s and was sent to Limbo")
                    add("mob.item", $$"%1$s was slain by %2$s using %3$s and was sent to Limbo")
                    add("player", $$"%1$s was slain by %2$s and was sent to Limbo")
                    add("player.item", $$"%1$s was slain by %2$s using %3$s and was sent to Limbo")
                    add("arrow", $$"%1$s was shot by %2$s and was sent to Limbo")
                    add("arrow.item", $$"%1$s was shot by %2$s using %3$s and was sent to Limbo")
                    add("fireball", $$"%1$s was fireballed into Limbo by %2$s")
                    add("fireball.item", $$"%1$s was fireballed into Limbo by %2$s using %3$s")
                    add("thrown", $$"%1$s was pummeled into Limbo by %2$s")
                    add("thrown.item", $$"%1$s was pummeled into Limbo by %2$s using %3$s")
                    add("indirectMagic", $$"%1$s was killed by %2$s using magic and was sent to Limbo")
                    add("indirectMagic.item", $$"%1$s was sent by %2$s using %3$s and was sent to Limbo")
                    add("thorns", $$"%1$s was sent to Limbo trying to hurt %2$s")
                    add("thorns.item", $$"%1$s was sent to Limbo by %3$s trying to hurt %2$s")
                    add("trident", $$"%1$s was impaled by %2$s into Limbo")
                    add("trident.item", $$"%1$s was impaled by %2$s with %3$s into Limbo")
                    add("fall", $$"%1$s hit the ground too hard and dropped into Limbo")
                    add("fall.player", $$"%1$s hit the ground too hard whilst trying to escape %2$s and dropped into Limbo"
                    )
                    add("outOfWorld", $$"%1$s fell into Limbo")
                    add("outOfWorld.player", $$"%1$s didn't want to live in the same world as %2$s and went to Limbo")
                    add("dragonBreath", $$"%1$s was roasted in dragon breath and was sent to Limbo")
                    add("dragonBreath.player", $$"%1$s was roasted in dragon breath by %2$s and was sent to Limbo")
                    add("flyIntoWall", $$"%1$s experienced kinetic energy and flew into Limbo")
                    add("flyIntoWall.player", $$"%1$s experienced kinetic energy whilst trying to escape %2$s and flew into Limbo"
                    )
                    add("fireworks", $$"%1$s went into Limbo with a bang")
                    add("fireworks.player", $$"%1$s went into Limbo with a bang whilst fighting %2$s")
                    add("fireworks.item", $$"%1$s went into Limbo with a bang due to a firework fired from %3$s by %2$s"
                    )
                    add("badRespawnPoint.message", $$"%1$s was killed by %2$s and was sent to Limbo")
                    add("badRespawnPoint.link", "Intentional Game Design")
                    add("sweetBerryBush", $$"%1$s poked a hole in reality")
                    add("sweetBerryBush.player", $$"%1$s poked a hole in reality whilst trying to escape %2$s")
                    add("sting", $$"%1$s bugged out to Limbo")
                    add("sting.player", $$"%1$s bugged out to Limbo by %2$s")
                    add("freeze", $$"%1$s froze into Limbo")
                    add("freeze.player", $$"%1$s was frozen into Limbo by %2$s")
                }
                add("generic", " and was sent to limbo.")
            }

            add("exit") {
                add("eternal_fluid", $$"%1$s bathed in reality")
                add("generic", $$"%1$s escaped Limbo")
                add("rift", $$"%1$s found a rift leading out of Limbo")
            }
        }

        addStats(ModStats.DEATHS_IN_POCKETS, "Deaths in Pocket")
        addStats(ModStats.TIMES_BEEN_TO_DUNGEON, "Times been to Dungeon")
        addStats(ModStats.TIMES_SENT_TO_LIMBO, "Times sent to Limbo")
        addStats(ModStats.TIMES_TELEPORTED_BY_MONOLITH, "Times teleported by Monolith")

        add("resourcePackActivationType") {
            add("normal", "Normal")
            add("defaultEnabled", "Default Enabled")
            add("alwaysEnabled", "Always Enabled")
        }

        add(ModEnchants.STRING_THEORY_ENCHANTMENT, "String Theory")
        add(ModEnchants.RENDING_ENCHANTMENT, "Rending")
        add(ModEnchants.TRANSCENDENT_ENCHANTMENT, "Transcendent")
        add(ModEnchants.TREPIDATION_ENCHANTMENT, "Trepidation")

        add(ModPaintings.LIMBO, "Limbo", "Waterpicker")
        add(ModPaintings.PORTAL, "Portal", "timetravellingBlockhead")
        add(ModPaintings.EYES, "Eyes", "Anims")
        add(ModPaintings.FREEDOM, "Freedom", "ImprovInAFedora")
        add(ModPaintings.GATEWAY_AT_NIGHT, "Gateway At Night", "timetravellingBlockhead")

        add(ModFluids.ETERNAL_FLUID, "Eternal Fluid")
        add(ModFluids.FLOWING_ETERNAL_FLUID, "Flowing Eternal Fluid")
        add(ModFluids.LEAK, "Leak")
        add(ModFluids.FLOWING_LEAK, "Flowing Leak")
    }

    private fun <T: VirtualTarget<*>> add(type: Holder<MapCodec<VirtualTarget<*>>>, name: String) {
        val key = Util.makeDescriptionId("virtual_type", ModRegistries.VIRTUAL_TYPE.getKey(type))
        add(key, name)
    }

    private fun info(value: String) {
        add("info", value)
    }

    private fun info(i: Int, value: String) {
        add("info$i", value)
    }

    private fun addDoorAutoGen(block: Holder<Block>, name: String, runnable: Runnable) {
        addDoorAutoGen(block.value(), name, runnable)
    }

    private fun addDoorAutoGen(block: Block, name: String, runnable: Runnable) {
        val key = Util.makeDescriptionId("autogen", BuiltInRegistries.BLOCK.getKey(block!!))
        add(key) {
            add("name", name)
            runnable.run()
        }
    }

    private fun addStats(stat: ResourceLocation, name: String) {
        builder.add(Util.makeDescriptionId("stat", stat), name)
    }

    private fun addDesc(key: String, name: String, desc: String) {
        add(key, name)
        add("$key.desc", desc)
    }

    private fun addCategory(value: String) = add("category", value)

    private fun addTitle(value: String) = add("title", value)

    private fun addOption(key: String, value: String, tooltip: String) {
        add("option.$key", value)
        add("option.$key.tooltip", tooltip)
    }

    private fun addArmor(set: ArmorSet, prefix: String) {
        add(set.helmet, "$prefix Helmet")
        add(set.chestplate, "$prefix Chestplate")
        add(set.leggings, "$prefix Leggings")
        add(set.boots, "$prefix Boots")
    }

    private fun add(item: Item, entry: String) {
        builder.add(item, entry)
    }


    private fun addDisc(item: Item, entry: String) {
        val key = item.descriptionId

        builder.add(key, "Music Disc")
        builder.add("$key.desc", entry)
    }

    private fun add(key: ResourceKey<PaintingVariant>, name: String, author: String) {
        val baseLang = key.location().toLanguageKey("painting")
        builder.add("$baseLang.title", name)
        builder.add("$baseLang.author", author)
    }

    private fun add(supplier: Fluid, contents: String) = builder.add(BuiltInRegistries.FLUID.getKey(supplier).toLanguageKey("fluid"), contents)

    private fun addBlockSet(set: ModBlocks.DecayGroupSet) {
        add(set.fence)
        add(set.gate)
        add(set.button)
        add(set.slab)
        add(set.stairs)
        add(set.wall)
    }

    private fun <T: Any> addCapitalizedEntry(registry: Registry<T>, entry: T) {
        val location = registry.getKey(entry) ?: return
        builder.add(translationKey(registry, entry), location.path.capitialize())
    }

    private fun add(item: Item, name: String, runnable: Runnable) {
        val key = item.descriptionId
        builder.add(key, name)

        add(key, runnable)
    }

    private fun <T : Any> translationKey(registry: Registry<T>, entry: T): String {
        val location = registry.getKey(entry)
        val registryKey = registry.key().location()
        return location!!.toLanguageKey((if (registryKey.path == "minecraft") "" else registryKey.path + ".") + registryKey.path)
    }

    private fun <T: Any> addHolder(holder: Holder<T>, runnable: Runnable = Runnable {}) {
        val entry = holder.value()
        val id = holder.unwrapKey().getOrNull() ?: return

        when (entry) {
            is Block -> {
                val value = BuiltInRegistries.BLOCK.getKey(entry).path.capitialize()
                builder.add(entry, value)
                add(entry.descriptionId, runnable)
            }

            is Item -> {
                val value = BuiltInRegistries.ITEM.getKey(entry).path.capitialize()
                builder.add(entry, value)
                add(entry.descriptionId, runnable)
            }

            is CreativeModeTab -> {
                val string = BuiltInRegistries.CREATIVE_MODE_TAB.getKey(entry)?.path?.capitialize() ?: return
                add(entry.displayName, string)
            }

            is Fluid -> {
                addCapitalizedEntry(BuiltInRegistries.FLUID, entry)
                add(BuiltInRegistries.FLUID.getKey(entry).toLanguageKey("fluid"), runnable)
            }

            else -> {}
        }
    }


    private fun add(key: ResourceKey<Biome>, name: String) {
        builder.add(Util.makeDescriptionId("biome", key.location()), name)
    }

    private fun add(key: ResourceKey<Enchantment>, name: String) {
        builder.addEnchantment(key, name)
    }

    private fun add(component: Component, string: String) {
        val contents = component.contents
        if (contents is TranslatableContents) {
            builder.add(contents.key, string)
        }
    }

    fun String.capitialize(): String = name.split("_").joinToString(" ", transform = { this.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } })


}
