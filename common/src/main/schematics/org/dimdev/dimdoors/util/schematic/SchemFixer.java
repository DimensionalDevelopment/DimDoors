package org.dimdev.dimdoors.util.schematic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;

import net.fabricmc.loader.api.FabricLoader;

public class SchemFixer {
	public static void run() {
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
}
