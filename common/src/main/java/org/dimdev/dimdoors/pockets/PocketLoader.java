package org.dimdev.dimdoors.pockets;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.api.util.*;
import org.dimdev.dimdoors.util.schematic.Schematic;

import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class PocketLoader {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final PocketLoader INSTANCE = new PocketLoader();


    private static SimpleTree<String, PocketTemplate> templates = new SimpleTree<>(String.class);
    public static Codec<PocketTemplate> CODEC = Codec.STRING.flatXmap(s -> templates.containsKey(Path.stringPath(s)) ? DataResult.success(templates.get(Path.stringPath(s))) : DataResult.error(() -> s + " no found."), template -> DataResult.error(() -> "Schematic serialization not supported"));
    private static SimpleTree<String, Tag> dataTree = new SimpleTree<>(String.class);

	public static void dump() {
	}

	public static void reload(HolderLookup.Provider provider, ResourceManager manager) {
		templates.clear();

		CompletableFuture<SimpleTree<String, PocketTemplate>> futureTemplates = ResourceUtil.loadResourcePathToMap(manager, "pockets/schematic", ".schem", new SimpleTree<>(String.class), ResourceUtil.COMPRESSED_NBT_READER.andThenReader(PocketLoader::loadPocketTemplate), ResourceUtil.PATH_KEY_PROVIDER);

		templates = futureTemplates.join();
	}

    private static PocketTemplate loadPocketTemplate(CompoundTag nbt, Path<String> id) {
		try {
			return new PocketTemplate(Schematic.fromNbt(nbt), ResourceLocation.parse(id.reduce(String::concat).orElseThrow()));
		} catch (Exception e) {
			throw new RuntimeException("Error loading " + id.toString(), e);
		}
	}


    public static PocketLoader getInstance() {
		return INSTANCE;
	}

	public static SimpleTree<String, PocketTemplate> getTemplates() {
		return templates;
	}

}