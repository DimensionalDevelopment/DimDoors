# Dimensional Doors
[![Build Status](https://api.travis-ci.org/DimensionalDevelopment/DimDoors.svg)](https://travis-ci.org/DimensionalDevelopment/DimDoors/builds)

## Overview
This is the main repository for the rewrite of Dimensional Doors by StevenRS11. This repository contains the up-to-date code base and commits submitted.

## Discord Server
For the main bits, we have a Discord server made specifically for talking about Dimensional Doors. It's also the place where the devs meet to discuss current progress on certain aspects, and get feedback from the team.

### Discord Server: [Click](https://discord.gg/f27hdrM) to open in browser or client, if installed

## Bugs and Suggestions
Experiencing a bug or just have a suggestion that is in-line with the mod's lore? Make a ticket about it!

### Bug Reports
You can report any bugs you find on [the issues page](https://github.com/DimensionalDevelopment/DimDoors/issues). Please include the following information:

 - The version of the mod you're using
 - Your Minecraft version (different versions can experience bugs that are not available in others)
 - Your forge version (different versions can experience bugs that are not available in others)
 - A list of other mods you're using, if the issue does not happen with only Dimensional Doors installed
 - A crash report, or preferably a full log (latest.log and debug.log). We have a paste site at https://paste.dimdev.org/
 
### Suggestions
For suggestions, please join [our discord server](https://discord.gg/f27hdrM), or [create a Github issue](https://github.com/DimensionalDevelopment/DimDoors/issues). Please include a detailed description with what you would like to see in the mod and explain how it can fit into the mods lore.

## Contributing
There are many ways to contribute to the project. We encourage you to join our Discord server if you have any questions about how to contribute.

### Submitting dungeons
Use the `/pocket blank blank_pocket_<n>` to create a blank, closed pocket, or `/pocket blank void_pocket_<n>` to create a pocket containing only a frame marking the pocket bounds, where `<n>` is the size of the pocket (the number of chunks on each side - 1). **Do not build outside the pocket bounds, or it will be clipped when saved. If you want to make your pocket larger after having started to build, you will have to make a new pocket and copy what you built using WorldEdit.** Use the `/saveschem <name>` command to save your pocket, and click "Create a new file" on [this](https://github.com/DimensionalDevelopment/DimDoors-Dungeon-Repo) page to submit your pocket. For any questions, please join our Discord server. 

### Coding
This project uses [Lombok](https://projectlombok.org/) to automatically generate getters and setters. You should install the Lombok plugin for [IntelliJ](https://plugins.jetbrains.com/plugin/6317-lombok-plugin) or [Eclipse](https://projectlombok.org/setup/eclipse) to be able to correctly test the project. We reccommend that you join the Discord server if you're planning on making more major changes to the code or have any questions or problems. 

### Translating
[This page](https://github.com/DimensionalDevelopment/DimDoors/tree/1.12-WIP/src/main/resources/assets/dimdoors/lang) contains all the currently translated languages. To contribute a translation, start by copying the [en_US.lang file](https://github.com/DimensionalDevelopment/DimDoors/blob/1.12-WIP/src/main/resources/assets/dimdoors/lang/en_US.lang) to a text editor, and saving it as the [correct locale code](https://minecraft.gamepedia.com/Language#Available_languages). Translate **only what is to the right of the = sign**, and then click "Create a new file" on [this page](https://github.com/DimensionalDevelopment/DimDoors/tree/1.12-WIP/src/main/resources/assets/dimdoors/lang) to submit your translation. 

### Testing
You can help us test the latest dev release and report bugs before we submit the next release by downloading and playing using the latest built jar from here: https://github.com/DimensionalDevelopment/DimDoors/releases **IMPORTANT:** These jars are untested and might cause loss of pockets, rift connections, or worlds. Always back up your worlds folder before testing them.

## Join the Team
Dimensional Development is always looking to expand (there are currently only 4 active members in the team) and introduce new members to the team and community. Join our Discord and talk to us! Tell us how you can help.
