package org.dimdev.dimdoors.util.schematic;

import java.util.HashMap;
import java.util.Map;

public class SchematicConverter {
	public static Map<String, String> CONVERSIONS = new HashMap<>();
	private static final String[] COLORS = new String[]{"white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black"};
	private static final String[] facingStrings = new String[] {"north", "south", "east", "west"};
	private static final String[] BRICK_VARIANTS = new String[]{"stone_brick", "nether_brick"};

	public static String updateId(String id) {

		if (id.equals("minecraft:redstone_torch[facing=north]")) {
			System.out.println();
		}
		id = CONVERSIONS.getOrDefault(id, id);
		return id;
	}


	static {
		reloadConversions();
	}

	public static void reloadConversions() {
		CONVERSIONS.clear();
		//SeparateClassToAvoidIdeaLag.a();
		CONVERSIONS.put("minecraft:double_stone_slab[variant=stone_brick]", "minecraft:brick_slab[type=double]");
		CONVERSIONS.put("minecraft:double_stone_slab", "minecraft:stone_slab[type=double]");
		CONVERSIONS.put("minecraft:double_stone_slab[seamless=true]", "minecraft:smooth_stone");
		CONVERSIONS.put("minecraft:double_wooden_slab", "minecraft:oak_slab[type=double]");
		CONVERSIONS.put("minecraft:wooden_slab[half=top]", "minecraft:oak_slab[type=top]");
		CONVERSIONS.put("minecraft:wooden_slab", "minecraft:oak_slab");
		CONVERSIONS.put("minecraft:planks", "minecraft:oak_planks");
		CONVERSIONS.put("dimdoors:ancient_fabric", "dimdoors:black_ancient_fabric");
		CONVERSIONS.put("dimdoors:fabric", "dimdoors:black_fabric");
		CONVERSIONS.put("minecraft:reeds", "minecraft:sugar_cane");
		CONVERSIONS.put("minecraft:wooden_door", "minecraft:oak_door[facing=north]");
		CONVERSIONS.put("minecraft:wooden_door[facing=north]", "minecraft:oak_door[facing=north]");
		CONVERSIONS.put("minecraft:wooden_door[facing=south]", "minecraft:oak_door[facing=south]");
		CONVERSIONS.put("minecraft:wooden_door[facing=east]", "minecraft:oak_door[facing=east]");
		CONVERSIONS.put("minecraft:wooden_door[facing=west]", "minecraft:oak_door[facing=west]");
		CONVERSIONS.put("minecraft:wooden_door[half=upper]", "minecraft:oak_door[half=upper]");
		CONVERSIONS.put("minecraft:log[variant=spruce]", "minecraft:spruce_log");
		CONVERSIONS.put("minecraft:bed[facing=south]", "minecraft:red_bed[facing=south,part=foot]");
		CONVERSIONS.put("minecraft:bed[facing=south,part=head]", "minecraft:red_bed[facing=south,part=head]");

		CONVERSIONS.put("minecraft:double_stone_slab[variant=nether_brick]", "minecraft:nether_brick_slab[type=double]");


		CONVERSIONS.put("dimdoors:ancient_fabric[color=white]", "dimdoors:white_ancient_fabric");
		CONVERSIONS.put("dimdoors:fabric[color=white]", "dimdoors:white_fabric");
		CONVERSIONS.put("dimdoors:ancient_fabric[color=orange]", "dimdoors:orange_ancient_fabric");
		CONVERSIONS.put("dimdoors:fabric[color=orange]", "dimdoors:orange_fabric");
		CONVERSIONS.put("dimdoors:ancient_fabric[color=lightBlue]", "dimdoors:light_blue_ancient_fabric");
		CONVERSIONS.put("dimdoors:fabric[color=lightBlue]", "dimdoors:light_blue_fabric");

		CONVERSIONS.put("minecraft:wooden_button[facing=north]", "minecraft:oak_button[face=wall,facing=north]");
		CONVERSIONS.put("minecraft:wooden_button[facing=south]", "minecraft:oak_button[face=wall,facing=south]");
		CONVERSIONS.put("minecraft:wooden_button[facing=east]", "minecraft:oak_button[face=wall,facing=east]");
		CONVERSIONS.put("minecraft:wooden_button[facing=west]", "minecraft:oak_button[face=wall,facing=west]");
		CONVERSIONS.put("minecraft:wooden_button", "minecraft:oak_button[face=wall,facing=north]");

		CONVERSIONS.put("minecraft:fence", "minecraft:oak_fence");
		CONVERSIONS.put("minecraft:log[axis=z,variant=spruce]", "minecraft:spruce_log[axis=z]");
		CONVERSIONS.put("minecraft:golden_rail[powered=true,shape=ascending_north]", "minecraft:powered_rail[powered=true,shape=ascending_north]");

		CONVERSIONS.put("minecraft:log", "minecraft:oak_log");
		CONVERSIONS.put("minecraft:red_flower", "minecraft:poppy");
		CONVERSIONS.put("minecraft:yellow_flower", "minecraft:dandelion");
		CONVERSIONS.put("minecraft:wall_sign[facing=north]", "minecraft:oak_wall_sign[facing=north]");
		CONVERSIONS.put("minecraft:wall_sign[facing=south]", "minecraft:oak_wall_sign[facing=south]");
		CONVERSIONS.put("minecraft:wall_sign[facing=east]", "minecraft:oak_wall_sign[facing=east]");
		CONVERSIONS.put("minecraft:wall_sign[facing=west]", "minecraft:oak_wall_sign[facing=west]");
		CONVERSIONS.put("minecraft:wall_sign", "minecraft:oak_wall_sign[facing=north]");

		CONVERSIONS.put("minecraft:leaves[check_decay=false,decayable=false]", "minecraft:oak_leaves[persistent=true]");
		CONVERSIONS.put("minecraft:leaves[decayable=false]", "minecraft:oak_leaves[persistent=true]");
		CONVERSIONS.put("minecraft:leaves[check_decay=false]", "minecraft:oak_leaves[persistent=true]");

		CONVERSIONS.put("minecraft:leaves[variant=jungle]", "minecraft:jungle_leaves[persistent=true]");
		CONVERSIONS.put("minecraft:leaves[check_decay=false,variant=spruce]", "minecraft:spruce_leaves[persistent=true]");



		CONVERSIONS.put("minecraft:powered_repeater", "minecraft:repeater[facing=north,powered=true]");
		CONVERSIONS.put("minecraft:powered_repeater[delay=1]", "minecraft:repeater[delay=1,facing=north,powered=true]");
		CONVERSIONS.put("minecraft:powered_repeater[delay=2]", "minecraft:repeater[delay=2,facing=north,powered=true]");
		CONVERSIONS.put("minecraft:powered_repeater[delay=3]", "minecraft:repeater[delay=3,facing=north,powered=true]");
		CONVERSIONS.put("minecraft:powered_repeater[delay=4]", "minecraft:repeater[delay=4,facing=north,powered=true]");

		CONVERSIONS.put("minecraft:powered_repeater[facing=north]", "minecraft:repeater[facing=north,powered=true]");
		CONVERSIONS.put("minecraft:powered_repeater[facing=south]", "minecraft:repeater[facing=south,powered=true]");
		CONVERSIONS.put("minecraft:powered_repeater[facing=east]", "minecraft:repeater[facing=east,powered=true]");
		CONVERSIONS.put("minecraft:powered_repeater[facing=west]", "minecraft:repeater[facing=west,powered=true]");
		CONVERSIONS.put("minecraft:powered_repeater[facing=north,delay=2]", "minecraft:repeater[delay=2,facing=north,powered=true]");
		CONVERSIONS.put("minecraft:powered_repeater[facing=south,delay=2]", "minecraft:repeater[delay=2,facing=south,powered=true]");
		CONVERSIONS.put("minecraft:powered_repeater[facing=east,delay=2]", "minecraft:repeater[delay=2,facing=east,powered=true]");
		CONVERSIONS.put("minecraft:powered_repeater[facing=west,delay=2]", "minecraft:repeater[delay=2,facing=west,powered=true]");
		CONVERSIONS.put("minecraft:powered_repeater[facing=north,delay=3]", "minecraft:repeater[delay=3,facing=north,powered=true]");
		CONVERSIONS.put("minecraft:powered_repeater[facing=south,delay=3]", "minecraft:repeater[delay=3,facing=south,powered=true]");
		CONVERSIONS.put("minecraft:powered_repeater[facing=east,delay=3]", "minecraft:repeater[delay=3,facing=east,powered=true]");
		CONVERSIONS.put("minecraft:powered_repeater[facing=west,delay=3]", "minecraft:repeater[delay=3,facing=west,powered=true]");
		CONVERSIONS.put("minecraft:powered_repeater[facing=north,delay=4]", "minecraft:repeater[delay=4,facing=north,powered=true]");
		CONVERSIONS.put("minecraft:powered_repeater[facing=south,delay=4]", "minecraft:repeater[delay=4,facing=south,powered=true]");
		CONVERSIONS.put("minecraft:powered_repeater[facing=east,delay=4]", "minecraft:repeater[delay=4,facing=east,powered=true]");
		CONVERSIONS.put("minecraft:powered_repeater[facing=west,delay=4]", "minecraft:repeater[delay=4,facing=west,powered=true]");

		CONVERSIONS.put("minecraft:unpowered_repeater", "minecraft:repeater[facing=north,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_repeater[delay=1]", "minecraft:repeater[delay=1,facing=north,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_repeater[delay=2]", "minecraft:repeater[delay=2,facing=north,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_repeater[delay=3]", "minecraft:repeater[delay=3,facing=north,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_repeater[delay=4]", "minecraft:repeater[delay=4,facing=north,powered=false]");

		CONVERSIONS.put("minecraft:unpowered_repeater[facing=north]", "minecraft:repeater[facing=north,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_repeater[facing=south]", "minecraft:repeater[facing=south,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_repeater[facing=east]", "minecraft:repeater[facing=east,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_repeater[facing=west]", "minecraft:repeater[facing=west,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_repeater[facing=north,delay=2]", "minecraft:repeater[delay=2,facing=north,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_repeater[facing=south,delay=2]", "minecraft:repeater[delay=2,facing=south,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_repeater[facing=east,delay=2]", "minecraft:repeater[delay=2,facing=east,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_repeater[facing=west,delay=2]", "minecraft:repeater[delay=2,facing=west,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_repeater[facing=north,delay=3]", "minecraft:repeater[delay=3,facing=north,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_repeater[facing=south,delay=3]", "minecraft:repeater[delay=3,facing=south,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_repeater[facing=east,delay=3]", "minecraft:repeater[delay=3,facing=east,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_repeater[facing=west,delay=3]", "minecraft:repeater[delay=3,facing=west,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_repeater[facing=north,delay=4]", "minecraft:repeater[delay=4,facing=north,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_repeater[facing=south,delay=4]", "minecraft:repeater[delay=4,facing=south,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_repeater[facing=east,delay=4]", "minecraft:repeater[delay=4,facing=east,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_repeater[facing=west,delay=4]", "minecraft:repeater[delay=4,facing=west,powered=false]");


		CONVERSIONS.put("minecraft:unpowered_repeater[delay=2,facing=north]", "minecraft:repeater[delay=2,facing=north,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_repeater[delay=2,facing=south]", "minecraft:repeater[delay=2,facing=south,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_repeater[delay=2,facing=east]", "minecraft:repeater[delay=2,facing=east,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_repeater[delay=2,facing=west]", "minecraft:repeater[delay=2,facing=west,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_repeater[delay=3,facing=north]", "minecraft:repeater[delay=3,facing=north,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_repeater[delay=3,facing=south]", "minecraft:repeater[delay=3,facing=south,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_repeater[delay=3,facing=east]", "minecraft:repeater[delay=3,facing=east,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_repeater[delay=3,facing=west]", "minecraft:repeater[delay=3,facing=west,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_repeater[delay=4,facing=north]", "minecraft:repeater[delay=4,facing=north,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_repeater[delay=4,facing=south]", "minecraft:repeater[delay=4,facing=south,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_repeater[delay=4,facing=east]", "minecraft:repeater[delay=4,facing=east,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_repeater[delay=4,facing=west]", "minecraft:repeater[delay=4,facing=west,powered=false]");


		CONVERSIONS.put("minecraft:powered_repeater[delay=2,facing=north]", "minecraft:repeater[delay=2,facing=north,powered=true]");
		CONVERSIONS.put("minecraft:powered_repeater[delay=2,facing=south]", "minecraft:repeater[delay=2,facing=south,powered=true]");
		CONVERSIONS.put("minecraft:powered_repeater[delay=2,facing=east]", "minecraft:repeater[delay=2,facing=east,powered=true]");
		CONVERSIONS.put("minecraft:powered_repeater[delay=2,facing=west]", "minecraft:repeater[delay=2,facing=west,powered=true]");
		CONVERSIONS.put("minecraft:powered_repeater[delay=3,facing=north]", "minecraft:repeater[delay=3,facing=north,powered=true]");
		CONVERSIONS.put("minecraft:powered_repeater[delay=3,facing=south]", "minecraft:repeater[delay=3,facing=south,powered=true]");
		CONVERSIONS.put("minecraft:powered_repeater[delay=3,facing=east]", "minecraft:repeater[delay=3,facing=east,powered=true]");
		CONVERSIONS.put("minecraft:powered_repeater[delay=3,facing=west]", "minecraft:repeater[delay=3,facing=west,powered=true]");
		CONVERSIONS.put("minecraft:powered_repeater[delay=4,facing=north]", "minecraft:repeater[delay=4,facing=north,powered=true]");
		CONVERSIONS.put("minecraft:powered_repeater[delay=4,facing=south]", "minecraft:repeater[delay=4,facing=south,powered=true]");
		CONVERSIONS.put("minecraft:powered_repeater[delay=4,facing=east]", "minecraft:repeater[delay=4,facing=east,powered=true]");
		CONVERSIONS.put("minecraft:powered_repeater[delay=4,facing=west]", "minecraft:repeater[delay=4,facing=west,powered=true]");


		CONVERSIONS.put("minecraft:powered_comparator", "minecraft:comparator[facing=north,powered=true]");
		CONVERSIONS.put("minecraft:powered_comparator[delay=1]", "minecraft:comparator[delay=1,facing=north,powered=true]");
		CONVERSIONS.put("minecraft:powered_comparator[delay=2]", "minecraft:comparator[delay=2,facing=north,powered=true]");
		CONVERSIONS.put("minecraft:powered_comparator[delay=3]", "minecraft:comparator[delay=3,facing=north,powered=true]");
		CONVERSIONS.put("minecraft:powered_comparator[delay=4]", "minecraft:comparator[delay=4,facing=north,powered=true]");

		CONVERSIONS.put("minecraft:powered_comparator[facing=north]", "minecraft:comparator[facing=north,powered=true]");
		CONVERSIONS.put("minecraft:powered_comparator[facing=south]", "minecraft:comparator[facing=south,powered=true]");
		CONVERSIONS.put("minecraft:powered_comparator[facing=east]", "minecraft:comparator[facing=east,powered=true]");
		CONVERSIONS.put("minecraft:powered_comparator[facing=west]", "minecraft:comparator[facing=west,powered=true]");
		CONVERSIONS.put("minecraft:powered_comparator[facing=north,delay=2]", "minecraft:comparator[delay=2,facing=north,powered=true]");
		CONVERSIONS.put("minecraft:powered_comparator[facing=south,delay=2]", "minecraft:comparator[delay=2,facing=south,powered=true]");
		CONVERSIONS.put("minecraft:powered_comparator[facing=east,delay=2]", "minecraft:comparator[delay=2,facing=east,powered=true]");
		CONVERSIONS.put("minecraft:powered_comparator[facing=west,delay=2]", "minecraft:comparator[delay=2,facing=west,powered=true]");
		CONVERSIONS.put("minecraft:powered_comparator[facing=north,delay=3]", "minecraft:comparator[delay=3,facing=north,powered=true]");
		CONVERSIONS.put("minecraft:powered_comparator[facing=south,delay=3]", "minecraft:comparator[delay=3,facing=south,powered=true]");
		CONVERSIONS.put("minecraft:powered_comparator[facing=east,delay=3]", "minecraft:comparator[delay=3,facing=east,powered=true]");
		CONVERSIONS.put("minecraft:powered_comparator[facing=west,delay=3]", "minecraft:comparator[delay=3,facing=west,powered=true]");
		CONVERSIONS.put("minecraft:powered_comparator[facing=north,delay=4]", "minecraft:comparator[delay=4,facing=north,powered=true]");
		CONVERSIONS.put("minecraft:powered_comparator[facing=south,delay=4]", "minecraft:comparator[delay=4,facing=south,powered=true]");
		CONVERSIONS.put("minecraft:powered_comparator[facing=east,delay=4]", "minecraft:comparator[delay=4,facing=east,powered=true]");
		CONVERSIONS.put("minecraft:powered_comparator[facing=west,delay=4]", "minecraft:comparator[delay=4,facing=west,powered=true]");

		CONVERSIONS.put("minecraft:unpowered_comparator", "minecraft:comparator[facing=north,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_comparator[delay=1]", "minecraft:comparator[delay=1,facing=north,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_comparator[delay=2]", "minecraft:comparator[delay=2,facing=north,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_comparator[delay=3]", "minecraft:comparator[delay=3,facing=north,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_comparator[delay=4]", "minecraft:comparator[delay=4,facing=north,powered=false]");

		CONVERSIONS.put("minecraft:unpowered_comparator[facing=north]", "minecraft:comparator[facing=north,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_comparator[facing=south]", "minecraft:comparator[facing=south,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_comparator[facing=east]", "minecraft:comparator[facing=east,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_comparator[facing=west]", "minecraft:comparator[facing=west,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_comparator[facing=north,delay=2]", "minecraft:comparator[delay=2,facing=north,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_comparator[facing=south,delay=2]", "minecraft:comparator[delay=2,facing=south,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_comparator[facing=east,delay=2]", "minecraft:comparator[delay=2,facing=east,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_comparator[facing=west,delay=2]", "minecraft:comparator[delay=2,facing=west,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_comparator[facing=north,delay=3]", "minecraft:comparator[delay=3,facing=north,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_comparator[facing=south,delay=3]", "minecraft:comparator[delay=3,facing=south,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_comparator[facing=east,delay=3]", "minecraft:comparator[delay=3,facing=east,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_comparator[facing=west,delay=3]", "minecraft:comparator[delay=3,facing=west,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_comparator[facing=north,delay=4]", "minecraft:comparator[delay=4,facing=north,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_comparator[facing=south,delay=4]", "minecraft:comparator[delay=4,facing=south,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_comparator[facing=east,delay=4]", "minecraft:comparator[delay=4,facing=east,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_comparator[facing=west,delay=4]", "minecraft:comparator[delay=4,facing=west,powered=false]");

		CONVERSIONS.put("minecraft:unpowered_comparator[powered=true]", "minecraft:comparator[facing=north,powered=false,mode=subtract]");
		CONVERSIONS.put("minecraft:unpowered_comparator[powered=true,facing=north]", "minecraft:comparator[facing=north,powered=false,mode=subtract]");
		CONVERSIONS.put("minecraft:unpowered_comparator[powered=true,facing=south]", "minecraft:comparator[facing=south,powered=false,mode=subtract]");
		CONVERSIONS.put("minecraft:unpowered_comparator[powered=true,facing=east]", "minecraft:comparator[facing=east,powered=false,mode=subtract]");
		CONVERSIONS.put("minecraft:unpowered_comparator[powered=true,facing=west]", "minecraft:comparator[facing=west,powered=false,mode=subtract]");

		CONVERSIONS.put("minecraft:powered_comparator[powered=true]", "minecraft:comparator[facing=north,powered=true,mode=subtract]");
		CONVERSIONS.put("minecraft:powered_comparator[powered=true,facing=north]", "minecraft:comparator[facing=north,powered=true,mode=subtract]");
		CONVERSIONS.put("minecraft:powered_comparator[powered=true,facing=south]", "minecraft:comparator[facing=south,powered=true,mode=subtract]");
		CONVERSIONS.put("minecraft:powered_comparator[powered=true,facing=east]", "minecraft:comparator[facing=east,powered=true,mode=subtract]");
		CONVERSIONS.put("minecraft:powered_comparator[powered=true,facing=west]", "minecraft:comparator[facing=west,powered=true,mode=subtract]");

		CONVERSIONS.put("minecraft:unpowered_comparator[delay=2,facing=north]", "minecraft:comparator[delay=2,facing=north,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_comparator[delay=2,facing=south]", "minecraft:comparator[delay=2,facing=south,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_comparator[delay=2,facing=east]", "minecraft:comparator[delay=2,facing=east,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_comparator[delay=2,facing=west]", "minecraft:comparator[delay=2,facing=west,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_comparator[delay=3,facing=north]", "minecraft:comparator[delay=3,facing=north,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_comparator[delay=3,facing=south]", "minecraft:comparator[delay=3,facing=south,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_comparator[delay=3,facing=east]", "minecraft:comparator[delay=3,facing=east,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_comparator[delay=3,facing=west]", "minecraft:comparator[delay=3,facing=west,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_comparator[delay=4,facing=north]", "minecraft:comparator[delay=4,facing=north,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_comparator[delay=4,facing=south]", "minecraft:comparator[delay=4,facing=south,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_comparator[delay=4,facing=east]", "minecraft:comparator[delay=4,facing=east,powered=false]");
		CONVERSIONS.put("minecraft:unpowered_comparator[delay=4,facing=west]", "minecraft:comparator[delay=4,facing=west,powered=false]");


		CONVERSIONS.put("minecraft:powered_comparator[delay=2,facing=north]", "minecraft:comparator[delay=2,facing=north,powered=true]");
		CONVERSIONS.put("minecraft:powered_comparator[delay=2,facing=south]", "minecraft:comparator[delay=2,facing=south,powered=true]");
		CONVERSIONS.put("minecraft:powered_comparator[delay=2,facing=east]", "minecraft:comparator[delay=2,facing=east,powered=true]");
		CONVERSIONS.put("minecraft:powered_comparator[delay=2,facing=west]", "minecraft:comparator[delay=2,facing=west,powered=true]");
		CONVERSIONS.put("minecraft:powered_comparator[delay=3,facing=north]", "minecraft:comparator[delay=3,facing=north,powered=true]");
		CONVERSIONS.put("minecraft:powered_comparator[delay=3,facing=south]", "minecraft:comparator[delay=3,facing=south,powered=true]");
		CONVERSIONS.put("minecraft:powered_comparator[delay=3,facing=east]", "minecraft:comparator[delay=3,facing=east,powered=true]");
		CONVERSIONS.put("minecraft:powered_comparator[delay=3,facing=west]", "minecraft:comparator[delay=3,facing=west,powered=true]");
		CONVERSIONS.put("minecraft:powered_comparator[delay=4,facing=north]", "minecraft:comparator[delay=4,facing=north,powered=true]");
		CONVERSIONS.put("minecraft:powered_comparator[delay=4,facing=south]", "minecraft:comparator[delay=4,facing=south,powered=true]");
		CONVERSIONS.put("minecraft:powered_comparator[delay=4,facing=east]", "minecraft:comparator[delay=4,facing=east,powered=true]");
		CONVERSIONS.put("minecraft:powered_comparator[delay=4,facing=west]", "minecraft:comparator[delay=4,facing=west,powered=true]");


		CONVERSIONS.put("minecraft:stonebrick", "minecraft:stone_bricks");
		CONVERSIONS.put("minecraft:log[axis=z,variant=jungle]", "minecraft:jungle_log[axis=z]");
		//CONVERSIONS.put("minecraft:unlit_redstone_torch", "minecraft:redstone_torch[lit=false]");

		for (boolean lit : new boolean[]{false, true}) {
			CONVERSIONS.put("minecraft:" + (lit ? "" : "unlit_") + "redstone_torch", "minecraft:redstone_torch[lit=" + (lit ? "true" : "false") + "]");
			for (String facing : facingStrings) {
				CONVERSIONS.put("minecraft:" + (lit ? "" : "unlit_") + "redstone_torch[facing=" + facing + "]", "minecraft:redstone_wall_torch[facing=" + facing + ",lit=" + (lit ? "true" : "false") + "]");
			}
		}

		for (String color : COLORS) {
			CONVERSIONS.put("minecraft:wool[color=" + color + "]", "minecraft:" + color + "_wool");
			CONVERSIONS.put("minecraft:stained_hardened_clay[color=" + color + "]", "minecraft:" + color + "_terracotta");
		}

		for (String facing : facingStrings) {
			CONVERSIONS.put("minecraft:torch[facing=" + facing + "]", "minecraft:wall_torch[facing=" + facing + "]");
		}

		CONVERSIONS.put("minecraft:stonebrick[variant=cracked_stonebrick]", "minecraft:cracked_stone_bricks");
		CONVERSIONS.put("minecraft:stonebrick[variant=chiseled_stonebrick]", "minecraft:chiseled_stone_bricks");
		CONVERSIONS.put("minecraft:monster_egg[variant=stone_brick]", "minecraft:infested_chiseled_stone_bricks");
		CONVERSIONS.put("minecraft:nether_brick", "minecraft:nether_bricks");
		CONVERSIONS.put("minecraft:noteblock", "minecraft:note_block");
		CONVERSIONS.put("minecraft:quartz_ore", "minecraft:nether_quartz_ore");
		CONVERSIONS.put("minecraft:grass", "minecraft:grass_block");

		CONVERSIONS.put("minecraft:stone_slab", "minecraft:smooth_stone_slab");
		CONVERSIONS.put("minecraft:stone_slab[half=top]", "minecraft:smooth_stone_slab[type=top]");
		CONVERSIONS.put("minecraft:stone_slab[variant=cobblestone]", "minecraft:cobblestone_slab");
		CONVERSIONS.put("minecraft:stone_slab[half=top,variant=cobblestone]", "minecraft:cobblestone_slab[type=top]");
		for (String variant : BRICK_VARIANTS) {
			CONVERSIONS.put("minecraft:stone_slab[variant=" + variant + "]", "minecraft:" + variant + "_slab");
			CONVERSIONS.put("minecraft:stone_slab[half=top,variant=" + variant + "]", "minecraft:" + variant + "_slab[type=top]");
		}

		CONVERSIONS.put("minecraft:stone_stairs", "minecraft:cobblestone_stairs");
		CONVERSIONS.put("minecraft:stone_stairs[half=top]", "minecraft:cobblestone_stairs[half=top]");
		for(String facing : facingStrings) {
			CONVERSIONS.put("minecraft:stone_stairs[facing=" + facing + "]", "minecraft:cobblestone_stairs[facing=" + facing + "]");
			CONVERSIONS.put("minecraft:stone_stairs[facing=" + facing + ",half=top]", "minecraft:cobblestone_stairs[facing=" + facing + ",half=top]");
		}


		CONVERSIONS.put("minecraft:tnt[explode=true]", "minecraft:tnt[unstable=true]");
		CONVERSIONS.put("minecraft:web", "minecraft:cobweb");


		for (int i = 1; i < 8; i++) {
			CONVERSIONS.put("minecraft:lava[level=" + i + "]", "minecraft:flowing_lava[level=" + i + "]");
		}
		for (int i = 8; i < 16; i++) {
			CONVERSIONS.put("minecraft:lava[level=" + i + "]", "minecraft:flowing_lava[level=" + i + ",falling=true]");
		}

		for (int i = 1; i < 8; i++) {
			CONVERSIONS.put("minecraft:water[level=" + i + "]", "minecraft:flowing_water[level=" + i + "]");
		}
		for (int i = 8; i < 16; i++) {
			CONVERSIONS.put("minecraft:water[level=" + i + "]", "minecraft:flowing_water[level=" + i + ",falling=true]");
		}

		CONVERSIONS.put("minecraft:tallgrass[type=tall_grass]", "minecraft:tall_grass");
		CONVERSIONS.put("minecraft:stonebrick[variant=mossy_stonebrick]", "minecraft:mossy_stone_bricks");
		CONVERSIONS.put("minecraft:waterlily", "minecraft:lily_pad");
		CONVERSIONS.put("minecraft:lit_redstone_lamp", "minecraft:redstone_lamp[lit=true]");
		CONVERSIONS.put("minecraft:snow_layer", "minecraft:snow");
	}
}
