package org.dimdev.dimdoors.util.schematic

import com.mojang.brigadier.context.CommandContext
import com.mojang.datafixers.schemas.Schema
import net.minecraft.SharedConstants
import net.minecraft.commands.CommandSourceStack
import net.minecraft.nbt.NbtAccounter
import net.minecraft.nbt.NbtIo
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

object SchemFixer {
    @JvmStatic
    fun main(ctx: CommandContext<CommandSourceStack>): Int {
        run()
        return 1
    }

    @JvmStatic
    fun run() {
        try {
            val main = Paths.get("D:\\Git Repos\\DimDoors\\dimdoors\\common\\src\\main\\resources")

            val dataVersion = SharedConstants.getCurrentVersion().dataVersion.version

            println("Current version is $dataVersion")

            Files.walk(main).use { paths ->
                paths.filter { it.toString().endsWith(".schem") && main.relativize(it).any { part -> part.toString() == "schematic" } }.forEach { path ->
                    try {
                        val nbt = NbtIo.readCompressed(path, NbtAccounter.unlimitedHeap())

                        if (nbt.contains("DataVersion")) {
                            val version = nbt.getInt("DataVersion")

                            if (version < dataVersion || nbt.getInt("Version") < 3) {
                                NbtIo.writeCompressed(SchematicUpgrader.update(nbt), path)

                                println(main.relativize(path).toString() + ": " + version)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun fixInPath(thing: String) {
        try {
            val lolPath = Paths.get(thing)
            Files.walk(lolPath, 6).use { pathStream ->
                pathStream.filter { it.toString().endsWith(".schem") }.forEach { path ->
                    try {
                        val loadedSchem = Schematic.fromNbt(NbtIo.readCompressed(path, NbtAccounter.unlimitedHeap()))
                        NbtIo.writeCompressed(Schematic.toNbt(loadedSchem), path)
                        println("Fixed $path")
                    } catch (e: IOException) {
                        throw RuntimeException(e)
                    }
                }
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    @JvmStatic
    fun merp(schema: Schema) {
        println()
    }
}
