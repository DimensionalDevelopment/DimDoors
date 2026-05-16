package org.dimdev.dimdoors.datagen;

import com.google.common.hash.HashCode;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import org.dimdev.dimdoors.painting.ModPaintings;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.CompletableFuture;

public class DefaultPaintingDataGenerator implements DataProvider {
    private final FabricDataOutput output;
    private final CompletableFuture<HolderLookup.Provider> registriesFuture;

    public DefaultPaintingDataGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        this.output = output;
        this.registriesFuture = registriesFuture;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        var path = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "textures/painting");

        return registriesFuture.thenApply(provider -> provider.lookupOrThrow(Registries.PAINTING_VARIANT))
                .thenApply(lookup -> ModPaintings.PAINTINGS_TO_DECAY_INTO.stream().filter(a -> a.location().getPath().startsWith("placeholder")).map(lookup::getOrThrow).toList())
                .thenAccept(references -> references.forEach(painting -> {
                    var key = painting.key();
                    var value = painting.value();

                    var width = value.width() * 16;
                    var height = value.height() * 16;

                    try {
                        var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

                        var graphics = image.createGraphics();

                        graphics.setColor(Color.decode("#452719"));
                        graphics.fillRect(0, 0, width, height);
                        graphics.setColor(Color.decode("#404040"));
                        graphics.fillRect(1, 1, width - 2, height - 2);

                        var writer = new ByteArrayOutputStream();

                        ImageIO.write(image, "PNG", writer);

                        var filePath = path.file(key.location(), "png");

                        var bytes = writer.toByteArray();

                        cachedOutput.writeIfNeeded(filePath, bytes, HashCode.fromBytes(bytes));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }));
    }

    @Override
    public String getName() {
        return "Paintings Textures";
    }
}