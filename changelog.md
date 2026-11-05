6.1
------

* Renabled Myth Dimensional Door (Amalgam Door) as a creative only item.
* Added jungle chain to Myth pockets.
* Added Gateway at Night painting by timetravellingBlockhead.
* Added 7 new pockets to dungeon pool. (Added Theatro, Snack Break, Mystery Egg, Missingno, Kept, Kawa's Overworld Box, Gap Hallway and Loutro)
* Fixed clouds appearing in dungeon pocket voids and limbo on neoforge.
* All main limbo decay blocks have recipes appropriate for its type and base material.
* Added Terracotta and its dyed/glazed variants as well smelting and crafting recipes.
* Driftwood saplings now work like real saplings. Grows on Unraveled fabric and will drop saplings, sticks, and frayed filament.
* Added new painting Gateway At Night by timetravellingBlockhead
* Fixed the painting Portal's author to timetravellingBlockhead
* Fixed worldsLeadingToLimbo not working.
* hardcoreLimbo config option works
* Fixed https://github.com/DimensionalDevelopment/DimDoors/issues/489
* Fixed Tesselating Loom screen not opening on neoforge
* Fixed setup_loot in pocket json config not applying. 
* Purged references to dimdoors:dungeon_chest from schematics in preparation for proper usage of setup_loot later.
* Revamped Pocket command to directly spawn a rift at the player with a specific pocket generator, pocket group, or virtual pocket. Currently needs to be in one of the pocket dimensions to work.
* Tweaked the rift configuraiton tool.
* Fixed Void Why, Trap Rib Tunnel and Ruins Small Pressure Plate
* Fixed Dimensional Door blocks just outright deleting when block underneath is deleted or its exploded.
* Fixed Dimensional Door blocks not showing proper break and mining particles.
* Added a buttload of tags for blocks and items.
* Made networking functional.

# 6.3.0
------

### Requirements
- Now requires Fabric Language Kotlin 1.14.1+ on Fabric or Kotlin for Forge 5+ on NeoForge.
- Bumped up minimum supported versions for the following dependencies:
    - Fabric Loader: 0.19.5
    - NeoForge: 21.1.228
- Supported versions for optional mod compatibility:
    - Sable: 2.0.5 (exact)
    - imguimc: 1.2.5 (exact)
    - Sodium: 0.8.12
    - Iris: 1.8.14-beta.1+1.21.1

### Breaking / Removed
- Reworked dimensional registry storage into separate saves (rift graph, rifts, pockets, private pockets, dialing, link properties, level space, dungeons, hub). Rift and pocket data from 6.2 worlds is not migrated.
- The config now lives in `config/dimdoors/` as plain JSON. The old `dimdoors-config.json5` is not read.
- Removed Create and Simulated compatibility from DimDoors. It is moving to Dimensional Industry.
- Removed the chunk loading pocket addon.
- Removed deprecated virtual types (Local, Relative, Global) and their corresponding language entries.
- Replaced the config option for Rift Signature usage in private pockets with the `pocketsRiftSignaturesWorkInPrivatePockets` gamerule.
- Removed old translations for German, French (Canada), Italian, Dutch, Romanian, Russian, and Chinese (Simplified). Will be readded as new fresh translations are completed.

### New
- Added the Quilliterate font.
- Added the Dialing Door. It is a door with a combination lock on it. Similar to a quartz dimensional door except the target door is tied to the sequence on a combo lock instead of player uuid. The combination is displayed on the door in the quilliteral font.
- Added the Hub Door (dimensional Driftwood Door). Leads to a single hub pocket shared by every player.
- Dungeons are now tracked as a whole. The gateway or door you enter through sets the dungeon's theme, and doors deeper inside stay in that theme.
- Masks now have real behavior.
    - Five types (Cyclop, Echo, Enlightened, Foresight, Sculking) that guard, patrol, or wander. They spot players by sight, by vibration, or when blocks are placed or broken nearby. Placed with the Mask Wand.
    - Getting caught puts a cursed Mask on your head. It slowly withers you, gets worse each time you're caught again, and adds an effect based on the mask type. Teleporting while masked sends you to Limbo, where the mask burns off.
    - Hitting a patrolling mask stuns it for 3 seconds. Landing on a stunned mask from above shatters it and drops a Mask Shard.
    - Added the Chased effect and the `dungeonReaper` gamerule (off by default). Shattering a mask, or being spotted while the gamerule is on, makes you Chased. While Chased, arriving in a dungeon spawns a Black mask where you came in after 10 seconds, and it hunts you. Returning to a root dimension clears it.
- Added Farshot.
    - Bow that uses ender pearls like arrows, letting them be fired further than they can be thrown. Hitting an entity swaps places with it, and firing a pearl at a detached rift lets you teleport through it.
- Added the Driftwood Chest.
- Added Lootr compatibility. Driftwood chests and loot table chests in pockets become Lootr chests.
- Added the Rift Marker. A creative tool for pocket builders that tags rifts with sequential ids.
- The Rift Configuration Tool now opens an in-game editor. Requires imguimc and operator permissions.
- Added the ability to change the colors of the dimensional portal effect.
    - Personal pockets now have a custom set of colors based on the color they're dyed.
    - Pockets can have their own custom portal colors.
    - With imguimc installed, pressing N opens a portal color editor.
- Rifts now have a chromatic aberration effect. Works with Iris shaderpacks.
- Added Limbo Air. It fills Limbo and makes blocks next to it decay.
- Added a config option for generic Limbo death messages. (Immodial)
- Added custom equip sounds for World Thread and Garment of Reality armors. (Immodial)
- Added `dimdoors:extension` and `dimdoors:develving` virtual target types that generate a new pocket based on the theme of the dungeon it a part of. Additionally, decreases and increases the current depth respectively by 1. Extension when the depth is 0, links to the start of the dungeon.
- Added the Tailor's Eyepiece. A wearable item that does the following effect.
    - Swaps out the fabric and ancient fabric's texture to ones with lines and X respectively for better visibility.
    - Displays debug text formally tied to rift configuration tool including the Size of a rift and what id when it has one.
    - Makes a dialing door's runes show up as numbers vs quilliterate font.
- WIP:
    - Liminal Transmitter. Place it on a detached rift to send redstone signals through it.
    - Monolith editing. Right click a Monolith with a Monolith Spawner to open the editor.

### Changes
- Introduced a new system for rift based growth where rifts grow slower with a weight to them that increases the chance. This is the first step to future proper reintroduction of rift scars. For now, once an exposed rift's size goes above 20, it will start unraveling blocks around it.
- Re-enabled rift spread decay and fixed the `decaysIntoAir` config option.
- Old Rift Key functionality stripped away in favor of temporarily changing the target dimensional door into the key's target. A new `temp` target restores the door to its original target.
- Reworked dimensional portal rendering. The portal effect now fills the full door and trapdoor volume instead of a plane.
- Updated dimensional door and trapdoor item models.
- Made gold and quartz doors non-occluding.
- Rift core tesseract visuals now fade in and out.
- Tweaked Monolith aggro to ignore invisible players.
- `/dimteleport` now requires permission level 2 instead of 4 and supports entity selectors.
- `/pocket` command now has a target instead of a locator, which refers to an entity to send to the pocket. Old functionality can be accomplished with `execute at <locator> run pocket ...`
- Command blocks that are powered or meant to automatically start will now do so when pockets are generated.
- Moved the lab chain into the Myth pocket group.
- Pockets generated by a chain rift in a dungeon room now count as rooms of that dungeon.
- Updated Sable compatibility for the new rift system.
- Cleaned up translations and display text for items, generated doors, advancements, and config options.
- Updated Portuguese (Brazil) translations. (Kawwabi)
- Using an escape door and being sent to limbo form a pocket now will use  

### Pack Makers
- Added `portal_colors.json` for datapack creators to customize portal colors per dimension or pocket type.
- Added support for the `.nbt` format in the schematic pocket generator type. Updated jsons to match.
- Schematics in Sponge v1 and v2 format are now upgraded to v3 and run through Minecraft's data fixers on load.
- Added DimensionType tags for controlling where Unraveled Fabric can decay and where Monoliths can exist.
- Dungeon room doors use the new `dimdoors:delving` rift target to go one level deeper within the same dungeon.
- Removed the `pockets/virtual` datapack folder. Use `pockets/groups` instead. `/pocket create` no longer has the `virtual_pocket` option.
- Pocket generators now use vanilla tags in `tags/pockets/generators` instead of the `tags` field. The `dimdoors:tag` type now takes tag ids, such as `dimdoors:myth` instead of `myth`. Generators selected by tag use the `weight` in their own json.
- Virtual pockets have an optional `level` field that sets the dimension they generate in. Defaults to the dungeon pocket dimension.
- Split the `dimdoors:template` rift target and modifier into `dimdoors:generator`, which points at a pocket generator, and `dimdoors:selector`, which points at a pocket group. The modifier's `templateId` field is now `template`.
- Moved the lab and jungle chain generators and schematics into `myth/lab/` and `myth/jungle/`.
- Public, private, hub, and dialing generators are now single files, such as `generators/public.json`. Their groups point directly at them.
- Removed the gold pocket generator and group, the example group, and the `blank_pocket` schematics.

### Fixes
- Fixed generated dimensional doors crashing when the source door already had waterlogging support.
- Corrected gray and light gray terracotta spelling error across all assets. (Immodial)
- Fixed armor enchantability and equipment tags for head, leg, and foot armor pieces. (Immodial)
- Fixed a server-side crash risk caused by client-side logging usage.
- Fixed an issue in Sable compat where sublevels connected to a sublevel a dimensional door teleported to would disappear.
- Fixed the game locking up in singleplayer when re-entering a world without restarting.
- Fixed Dimensional Eraser bloop sound not playing.
- Fixed Eternal Fluid not flowing during Limbo generation.
- Fixed Rift Signatures and Rift Reference targets not working correctly.
- Fixed Minecraft decay block datagen causing rotation issues.
- Fixed auto generated doors appearing in Chipped content.
- Fixed several pockets having no loot and misconfigured doors.

### Dev
- Ported the mod to Kotlin.
- Abstracted most Rift code into a common interface. Blocks that only need rift behavior can use a generic rift block entity, and any block entity implementing it leaves a rift when broken.
- Split the dimensional registry into subsystems connected by a rift graph.
- Reduced code duplication by a lot and improved resource loading.
- Replaced the `PocketGenerator` object with `PocketGenerationHelper`. The dimension a pocket generates in now comes from the pocket .

# Dimensional Core 1.0.0
------
Shared library split out of DimDoors. It ships bundled inside DimDoors 6.3.0, so there is nothing extra to install.

### Features
- Multiloader platform layer for Fabric and NeoForge: registries (including synced registries), creative tabs, command argument types, and platform events such as server start and stop.
- Item, fluid, and energy transfer API with Fabric and NeoForge backends. Supports custom transfer types and fluid rendering details.
- Errata: a framework for schema-driven in-game ImGui editors, with server-side permission checks. Requires imguimc on the client.
- Render node effect pipeline for post-processing effects that works with vanilla, Sodium, and Iris, with or without a shaderpack.
- Custom item renderers.
- Codec-based config files.
- Custom dimension sky, cloud, and weather rendering hooks on Fabric.
- Command builders driven by registered types.
