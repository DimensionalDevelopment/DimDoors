package org.dimdev.dimdoors.block.door.data;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
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
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.targets.PrivatePocketExitTarget;
import org.dimdev.dimdoors.rift.targets.PrivatePocketTarget;
import org.dimdev.dimdoors.rift.targets.PublicPocketTarget;
import org.dimdev.dimdoors.rift.targets.RandomTarget;
import org.dimdev.dimdoors.rift.targets.UnstableTarget;
import org.dimdev.dimdoors.util.OptionalBool;
import org.dimdev.dimdoors.world.ModDimensions;

import net.minecraft.util.Pair;
import net.minecraft.util.Rarity;
import net.minecraft.util.Util;

public class DoorDataReader {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().setLenient().create();
	private static final Logger LOGGER = LogManager.getLogger();
	private static final DoorData DEFAULT_IRON_DIMENSIONAL_DOOR = new DoorData(
			"dimdoors:iron_dimensional_door",
			new DoorData.UnbakedItemSettings(
					Optional.empty(),
					OptionalInt.of(1),
					OptionalInt.empty(),
					Optional.of(Rarity.UNCOMMON),
					OptionalBool.FALSE
					),
			new DoorData.UnbakedBlockSettings(
					"minecraft:iron_door",
					OptionalInt.of(10)
			), new RiftDataList(Util.make(new LinkedList<>(), list -> list.add(new Pair<>(new RiftDataList.OptRiftData(Optional.of(new PublicPocketTarget()), Optional.empty()), AlwaysTrueCondition.INSTANCE)))
	));
	private static final DoorData DEFAULT_GOLD_DIMENSIONAL_DOOR = new DoorData(
			"dimdoors:gold_dimensional_door",
			new DoorData.UnbakedItemSettings(
					Optional.empty(),
					OptionalInt.of(1),
					OptionalInt.empty(),
					Optional.of(Rarity.UNCOMMON),
					OptionalBool.FALSE
			),
			new DoorData.UnbakedBlockSettings(
					"dimdoors:gold_door",
					OptionalInt.of(10)
			), new RiftDataList(Util.make(new LinkedList<>(), list -> list.add(new Pair<>(new RiftDataList.OptRiftData(Optional.of(RandomTarget.builder().acceptedGroups(Collections.singleton(0)).coordFactor(1).negativeDepthFactor(10000).positiveDepthFactor(80).weightMaximum(100).noLink(false).noLinkBack(false).newRiftWeight(1).build()), Optional.of(LinkProperties.builder().groups(new HashSet<>(Arrays.asList(0, 1))).linksRemaining(1).build())), AlwaysTrueCondition.INSTANCE)))
	));
	private static final DoorData DEFAULT_OAK_DIMENSIONAL_DOOR = new DoorData(
			"dimdoors:oak_dimensional_door",
			new DoorData.UnbakedItemSettings(
					Optional.empty(),
					OptionalInt.of(1),
					OptionalInt.empty(),
					Optional.of(Rarity.UNCOMMON),
					OptionalBool.FALSE
			),
			new DoorData.UnbakedBlockSettings(
					"minecraft:oak_door",
					OptionalInt.of(10)
			), new RiftDataList(Util.make(new LinkedList<>(), list -> list.add(new Pair<>(new RiftDataList.OptRiftData(Optional.of(RandomTarget.builder().acceptedGroups(Collections.singleton(0)).coordFactor(1).negativeDepthFactor(80).positiveDepthFactor(Double.MAX_VALUE).weightMaximum(100).noLink(false).newRiftWeight(0).build()), Optional.empty()), AlwaysTrueCondition.INSTANCE)))
	));
	private static final DoorData DEFAULT_QUARTZ_DIMENSIONAL_DOOR = new DoorData(
			"dimdoors:quartz_dimensional_door",
			new DoorData.UnbakedItemSettings(
					Optional.empty(),
					OptionalInt.of(1),
					OptionalInt.empty(),
					Optional.of(Rarity.UNCOMMON),
					OptionalBool.FALSE
			),
			new DoorData.UnbakedBlockSettings(
					"dimdoors:quartz_door",
					OptionalInt.of(10)
			), new RiftDataList(Util.make(new LinkedList<>(), list -> {
				WorldMatchCondition condition = new WorldMatchCondition(ModDimensions.PERSONAL);
				list.add(new Pair<>(new RiftDataList.OptRiftData(Optional.of(new PrivatePocketExitTarget()), Optional.empty()), condition));
				list.add(new Pair<>(new RiftDataList.OptRiftData(Optional.of(new PrivatePocketTarget()), Optional.empty()), new InverseCondition(condition)));
	})
	));
	private static final DoorData DEFAULT_UNSTABLE_DIMENSIONAL_DOOR = new DoorData(
			"dimdoors:unstable_dimensional_door",
			new DoorData.UnbakedItemSettings(
					Optional.empty(),
					OptionalInt.of(1),
					OptionalInt.empty(),
					Optional.of(Rarity.UNCOMMON),
					OptionalBool.FALSE
			),
			new DoorData.UnbakedBlockSettings(
					"minecraft:iron_door",
					OptionalInt.of(10)
			), new RiftDataList(Util.make(new LinkedList<>(), list -> list.add(new Pair<>(new RiftDataList.OptRiftData(Optional.of(new UnstableTarget()), Optional.of(LinkProperties.builder().linksRemaining(1).groups(IntStream.of(0, 1).boxed().collect(Collectors.toSet())).build())), AlwaysTrueCondition.INSTANCE)))
	));

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
						jsonStr = new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
					} catch (IOException e) {
						LOGGER.error("Error reading " + p, e);
						return;
					}
					JsonObject json = GSON.fromJson(jsonStr, JsonObject.class);
					try (DoorData ignored = DoorData.fromJson(json)) {
						LOGGER.info("Loaded door json from {} with id {}", p.toAbsolutePath().toString(), ignored.getId());
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
			Files.write(path, json.getBytes(StandardCharsets.UTF_8));
		} catch (IOException e) {
			LOGGER.error("Error writing to " + path, e);
		}
	}
}
