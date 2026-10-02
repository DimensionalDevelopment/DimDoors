package org.dimdev.dimdoors.datagen

import com.google.common.hash.HashCode
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.data.CachedOutput
import net.minecraft.data.DataProvider
import net.minecraft.data.PackOutput
import org.dimdev.dimdoors.painting.ModPaintings
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.util.concurrent.CompletableFuture
import javax.imageio.ImageIO

class DefaultPaintingDataGenerator(private val output: FabricDataOutput, private val registriesFuture: CompletableFuture<HolderLookup.Provider>) : DataProvider {
    override fun run(cachedOutput: CachedOutput): CompletableFuture<*> {
        val path = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "textures/painting")

        return registriesFuture.thenApply { provider -> provider.lookupOrThrow(Registries.PAINTING_VARIANT) }
            .thenApply { lookup -> ModPaintings.PAINTINGS_TO_DECAY_INTO.filter { it.location().path.startsWith("placeholder") }.map { lookup.getOrThrow(it) } }
            .thenAccept { references -> references.forEach { painting ->
                val key = painting.key()
                val value = painting.value()

                val width = value.width() * 16
                val height = value.height() * 16

                try {
                    val image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)

                    val graphics = image.createGraphics()

                    graphics.color = Color.decode("#452719")
                    graphics.fillRect(0, 0, width, height)
                    graphics.color = Color.decode("#404040")
                    graphics.fillRect(1, 1, width - 2, height - 2)

                    val writer = ByteArrayOutputStream()

                    ImageIO.write(image, "PNG", writer)

                    val filePath = path.file(key.location(), "png")

                    val bytes = writer.toByteArray()

                    cachedOutput.writeIfNeeded(filePath, bytes, HashCode.fromBytes(bytes))
                } catch (e: Exception) {
                    throw RuntimeException(e)
                }
            } }
    }

    override fun getName(): String = "Paintings Textures"
}
