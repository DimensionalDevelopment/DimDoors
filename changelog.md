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

- Added new required dependency Liminal Library at version 1.13.0.
- Bumped up minimum supported versions for the following dependencies:
    - Fabric Loader: 0.18.4
    - NeoForge: 21.1.228
- Bumped minimum supported version for mod compatibility for following mods.
    - Sable: 2.0.3
    - Simulated: 1.3.0
    - Aeronautics: 1.3.0
    - Sodium: 0.8.12
    - Iris: 1.8.14-beta.1+1.21.1
- Reworked dimensional registry storage into separate rift, rift-link, pocket, and private-pocket saves. Old worlds will automatically be updated;
- Old Rift Key functional stripped away in favor of temporarily changing target dimensional door into target key type.
- Fixed dimensional doors and dimensional portals not teleporting when used in valid open states.
- Re-enabled rift spread decay and fixed the `decaysIntoAir` config option.
- Fixed generated dimensional doors crashing when the source door already had waterlogging support.
- Added a config option for generic Limbo death messages. (Immodial)
- Added custom equip sounds for World Thread and Garment of Reality armors. (Immodial)
- Cleaned up translations and display text for items, generated doors, advancements, and config options.
- Updated Portuguese (Brazil) translations. (Kawwabi)
- Removed old translations for German, French (Canada), Italian, Dutch, Romanian, Russian, and Chinese (Simplified). Will be readded as new fresh translations are completed.
- Corrected gray and light gray terracotta spelling error across all assets. (Immodial)
- Fixed armor enchantability and equipment tags for head, leg, and foot armor pieces. (Immodial)
- Fixed a server-side crash risk caused by client-side logging usage.
- Improved Create sliding door compatibility, including mixed double doors, redstone, waterlogging, and folding-door visuals.
- Updated Create contraption and Sable compatibility for the new rift system.
- Command blocks that are powered or meant to automatically start will now do so when pockets are generated.
- `/dimteleport` command's permission level has been changedhas entity selector support.
- `/pocket` command now has a target instead of a locator, which refers to an entity to send to the pocket. Old functionality can be accomplished with execute at <locator> run pocket ...
- Introduced a new system for rift based growth where rifts grow slower with a weight to them that increases the chance. This the first step to future proper reintroduction of rift scars.
- Abstracted most Rift code into common interface
- Improved resource loading.
- Removed deprecated virtual type (Local, Relative, Global) and their corresponding language entries.
- Fixed issue where on singleplayer game is lock up upon reentering a game without restarting.
- Added the Dialing door. It is a door with a combination lock on it. Similar to a quartz dimensional door except the target door is tied to the sequence on a combo lock isntead of player uuid.
- Reduced code duplication by alot.  