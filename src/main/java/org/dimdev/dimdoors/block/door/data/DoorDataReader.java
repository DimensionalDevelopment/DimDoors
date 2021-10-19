package org.dimdev.dimdoors.block.door.data;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.block.door.data.condition.AlwaysTrueCondition;
import org.dimdev.dimdoors.block.door.data.condition.InverseCondition;
import org.dimdev.dimdoors.block.door.data.condition.WorldMatchCondition;
import org.dimdev.dimdoors.pockets.DefaultDungeonDestinations;
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.targets.PrivatePocketExitTarget;
import org.dimdev.dimdoors.rift.targets.PrivatePocketTarget;
import org.dimdev.dimdoors.rift.targets.PublicPocketTarget;
import org.dimdev.dimdoors.rift.targets.UnstableTarget;
import org.dimdev.dimdoors.world.ModDimensions;

import net.minecraft.util.Pair;
import net.minecraft.util.Rarity;
import net.minecraft.util.Util;

import net.fabricmc.fabric.api.util.TriState;

public class DoorDataReader {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().setLenient().create();
	private static final Logger LOGGER = LogManager.getLogger();
	public static final DoorData DEFAULT_IRON_DIMENSIONAL_DOOR = new DoorData(
			"dimdoors:iron_dimensional_door",
			new DoorData.UnbakedItemSettings(
					"minecraft:iron_door",
					OptionalInt.of(1),
					OptionalInt.empty(),
					Optional.of(Rarity.UNCOMMON),
					TriState.FALSE
					),
			new DoorData.UnbakedBlockSettings(
					"minecraft:iron_door",
					OptionalInt.of(10)
			), new RiftDataList(Util.make(new LinkedList<>(), list -> list.add(new Pair<>(new RiftDataList.OptRiftData(Optional.of(new PublicPocketTarget()), Optional.empty()).toJson(new JsonObject()), AlwaysTrueCondition.INSTANCE)))

	), true);
	public static final DoorData DEFAULT_GOLD_DIMENSIONAL_DOOR = new DoorData(
			"dimdoors:gold_dimensional_door",
			new DoorData.UnbakedItemSettings(
					"dimdoors:gold_door",
					OptionalInt.of(1),
					OptionalInt.empty(),
					Optional.of(Rarity.UNCOMMON),
					TriState.FALSE
			),
			new DoorData.UnbakedBlockSettings(
					"dimdoors:gold_door",
					OptionalInt.of(10)
			), new RiftDataList(Util.make(new LinkedList<>(), list -> list.add(new Pair<>(new RiftDataList.OptRiftData(Optional.of(DefaultDungeonDestinations.getDeeperDungeonDestination()), Optional.of(DefaultDungeonDestinations.POCKET_LINK_PROPERTIES)).toJson(new JsonObject()), AlwaysTrueCondition.INSTANCE)))
	), true);
	public static final DoorData DEFAULT_OAK_DIMENSIONAL_DOOR = new DoorData(
			"dimdoors:oak_dimensional_door",
			new DoorData.UnbakedItemSettings(
					"minecraft:oak_door",
					OptionalInt.of(1),
					OptionalInt.empty(),
					Optional.of(Rarity.UNCOMMON),
					TriState.FALSE
			),
			new DoorData.UnbakedBlockSettings(
					"minecraft:oak_door",
					OptionalInt.of(10)
			), new RiftDataList(Util.make(new LinkedList<>(), list -> list.add(new Pair<>(new RiftDataList.OptRiftData(Optional.of(DefaultDungeonDestinations.getShallowerDungeonDestination()), Optional.of(DefaultDungeonDestinations.POCKET_LINK_PROPERTIES)).toJson(new JsonObject()), AlwaysTrueCondition.INSTANCE)))
	), true);
	public static final DoorData DEFAULT_QUARTZ_DIMENSIONAL_DOOR = new DoorData(
			"dimdoors:quartz_dimensional_door",
			new DoorData.UnbakedItemSettings(
					"dimdoors:quartz_door",
					OptionalInt.of(1),
					OptionalInt.empty(),
					Optional.of(Rarity.UNCOMMON),
					TriState.FALSE
			),
			new DoorData.UnbakedBlockSettings(
					"dimdoors:quartz_door",
					OptionalInt.of(10)
			), new RiftDataList(Util.make(new LinkedList<>(), list -> {
				WorldMatchCondition condition = new WorldMatchCondition(ModDimensions.PERSONAL);
				list.add(new Pair<>(new RiftDataList.OptRiftData(Optional.of(new PrivatePocketExitTarget()), Optional.empty()).toJson(new JsonObject()), condition));
				list.add(new Pair<>(new RiftDataList.OptRiftData(Optional.of(new PrivatePocketTarget()), Optional.empty()).toJson(new JsonObject()), new InverseCondition(condition)));
			})
	), true);
	public static final DoorData DEFAULT_UNSTABLE_DIMENSIONAL_DOOR = new DoorData(
			"dimdoors:unstable_dimensional_door",
			new DoorData.UnbakedItemSettings(
					"minecraft:iron_door",
					OptionalInt.of(1),
					OptionalInt.empty(),
					Optional.of(Rarity.UNCOMMON),
					TriState.FALSE
			),
			new DoorData.UnbakedBlockSettings(
					"minecraft:iron_door",
					OptionalInt.of(10)
			), new RiftDataList(Util.make(new LinkedList<>(), list -> list.add(new Pair<>(new RiftDataList.OptRiftData(Optional.of(new UnstableTarget()), Optional.of(LinkProperties.builder().linksRemaining(1).groups(IntStream.of(0, 1).boxed().collect(Collectors.toSet())).build())).toJson(new JsonObject()), AlwaysTrueCondition.INSTANCE)))
	), true);

	public static void read() {
		Path doorDir = DimensionalDoorsInitializer.getConfigRoot().resolve("doors");

		if (Files.exists(doorDir) && !Files.isDirectory(doorDir)) {
			try {
				Files.delete(doorDir);
			} catch (IOException e) {
				LOGGER.error("Error deleting " + doorDir, e);
				return;
			}
		}

		if (Files.notExists(doorDir)) {
			try {
				Files.createDirectory(doorDir);
				writeDefault(doorDir);
			} catch (IOException e) {
				LOGGER.error("Error creating directory " + doorDir, e);
				return;
			}
		}

		if (Files.isDirectory(doorDir)) {
			List<Path> paths;
			try {
				paths = Files.list(doorDir).collect(Collectors.toList());
			} catch (IOException e) {
				LOGGER.error("Error retrieving paths in directory " + doorDir, e);
				return;
			}

			for (Path p : paths) {
				if (!Files.isDirectory(p) && Files.isRegularFile(p)) {
					String jsonStr;
					try {
						jsonStr = Files.readString(p);
					} catch (IOException e) {
						LOGGER.error("Error reading " + p, e);
						return;
					}
					JsonObject json = GSON.fromJson(jsonStr, JsonObject.class);
					// TODO: someone check whether this makes sense.
					try {
						try (DoorData ignored = DoorData.fromJson(json)) {
							LOGGER.info("Loaded door json from {} with id {}", p.toAbsolutePath().toString(), ignored.getId());
						}
					} catch (Exception e) {
						LOGGER.error("Error trying to load door json from path " + p.toAbsolutePath().toString(), e);
					}
				}
			}
		}
	}

	private static void writeDefault(Path root) throws IOException {
		writeDefault(root.resolve("iron_dimensional_door.json"), DEFAULT_IRON_DIMENSIONAL_DOOR);
		writeDefault(root.resolve("gold_dimensional_door.json"), DEFAULT_GOLD_DIMENSIONAL_DOOR);
		writeDefault(root.resolve("oak_dimensional_door.json"), DEFAULT_OAK_DIMENSIONAL_DOOR);
		writeDefault(root.resolve("quartz_dimensional_door.json"), DEFAULT_QUARTZ_DIMENSIONAL_DOOR);
//		writeDefault(root.resolve("unstable_dimensional_door.json"), DEFAULT_UNSTABLE_DIMENSIONAL_DOOR);
	}

	private static void writeDefault(Path path, DoorData doorData) {
		try {
			Files.createFile(path);
		} catch (IOException e) {
			LOGGER.error("Error creating " + path, e);
			return;
		}
		String json = GSON.toJson(doorData.toJson(new JsonObject()));
		try {
			Files.writeString(path, json);
		} catch (IOException e) {
			LOGGER.error("Error writing to " + path, e);
		}
	}
}
