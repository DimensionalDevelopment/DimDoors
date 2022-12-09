package org.dimdev.dimdoors.tag;

import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import org.dimdev.dimdoors.DimensionalDoors;

public class ModBlockTags {
	public static final TagKey<Block> MINOR_PLANTS = of("minor_plants");
	public static final TagKey<Block> DECAY_TO_AIR = of("decay_to_air");
	public static final TagKey<Block> DECAY_TO_RAIL = of("decay_to_rail");
    public static final TagKey<Block> DECAY_TO_GRITTY_STONE = of("decay_to_gritty_stone");
	public static final TagKey<Block> DECAY_TO_SOLID_STATIC = of("decay_to_solid_static");
	public static final TagKey<Block> DECAY_UNRAVELED_FENCE = of("decay_unraveled_fence");
	public static final TagKey<Block> DECAY_UNRAVELED_GATE = of("decay_unraveled_gate");
	public static final TagKey<Block> DECAY_UNRAVELED_BUTTON = of("decay_unraveled_button");
	public static final TagKey<Block> DECAY_UNRAVELED_SLAB = of("decay_unraveled_slab");
	public static final TagKey<Block> DECAY_UNRAVELED_STAIRS = of("decay_unraveled_stairs");
	public static final TagKey<Block> DECAY_TO_GLASS_PANE = of("decay_to_glass_pane");
	public static final TagKey<Block> DECAY_TO_RUST = of("decay_to_rust");
	public static final TagKey<Block> DECAY_TO_UNRAVELED_SPIKE = of("decay_to_unraveled_spike");
	public static final TagKey<Block> DECAY_TO_WITHER_ROSE = of("decay_to_wither_rose");
	public static final TagKey<Block> DECAY_TO_CLAY = of("decay_to_clay");
	public static final TagKey<Block> DECAY_CLAY_FENCE = of("decay_clay_fence");
	public static final TagKey<Block> DECAY_CLAY_GATE = of("decay_clay_gate");
	public static final TagKey<Block> DECAY_CLAY_BUTTON = of("decay_clay_button");
	public static final TagKey<Block> DECAY_CLAY_SLAB = of("decay_clay_slab");
	public static final TagKey<Block> DECAY_CLAY_STAIRS = of("decay_clay_stairs");

	public static final TagKey<Block> DECAY_TO_DARK_SAND = of("decay_to_dark_sand");
	public static final TagKey<Block> DECAY_DARK_SAND_FENCE = of("decay_dark_sand_fence");
	public static final TagKey<Block> DECAY_DARK_SAND_GATE = of("decay_dark_sand_gate");
	public static final TagKey<Block> DECAY_DARK_SAND_BUTTON = of("decay_dark_sand_button");
	public static final TagKey<Block> DECAY_DARK_SAND_SLAB = of("decay_dark_sand_slab");
	public static final TagKey<Block> DECAY_DARK_SAND_STAIRS = of("decay_dark_sand_stairs");

	public static final TagKey<Block> DECAY_TO_AMALGAM = of("decay_to_amalgam");

    private static TagKey<Block> of(String id) {
		return TagKey.of(RegistryKeys.BLOCK, DimensionalDoors.id(id));
	}
}
