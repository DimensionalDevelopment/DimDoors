package org.dimdev.dimdoors.util.schematic;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.DetectedVersion;
import net.minecraft.SharedConstants;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.Bootstrap;
import net.minecraft.util.datafix.DataFixers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class SchemFixer {
    public static int main(CommandContext<CommandSourceStack> ctx) {
        try {

            var main = Paths.get("D:\\Git Repos\\DimDoorsNew\\common\\src\\main\\resources\\resourcepacks\\default\\data\\dimdoors\\pockets\\schematic");

            var dataVersion = SharedConstants.getCurrentVersion().getDataVersion().getVersion();

            System.out.println("Current version is " + dataVersion);

            Files.walk(main).filter(a -> a.toString().contains(".schem")).forEach(new Consumer<Path>() {
                @Override
                public void accept(Path path) {
                    try {
                        var nbt = NbtIo.readCompressed(path, NbtAccounter.unlimitedHeap());


                        if (nbt.contains("DataVersion")) {


                            var version = nbt.getInt("DataVersion");


                            if (version < dataVersion) {
                                var tag = DataFixers.getDataFixer().update(SchematicDataFixer.SCHEMATIC, new Dynamic<>(NbtOps.INSTANCE, nbt), version, dataVersion);


                                System.out.println(main.relativize(path) + ": " + version);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 1;
    }

    public static void fixInPath(String thing) {
        try {
            Path lolPath = Paths.get(thing);
            try (Stream<Path> pathStream = Files.walk(lolPath, 6)) {
                pathStream.filter(path -> path.toString().endsWith(".schem")).forEach(path -> {
                    try {
                        Schematic loadedSchem = Schematic.fromNbt(NbtIo.readCompressed(path, NbtAccounter.unlimitedHeap()));
                        NbtIo.writeCompressed(Schematic.toNbt(loadedSchem), path);
                        System.out.println("Fixed " + path);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void merp(Schema schema) {
        System.out.println();
    }
}
