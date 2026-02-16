package org.dimdev.dimdoors.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import org.dimdev.dimdoors.DimensionalDoors;

public class ModBlockTags {
    public static final TagKey<Block> DRIFTWOOD_LOGS = of("driftwood_logs");
    public static final TagKey<Block> AUTO_GENERATED_DIMDOORS = of("auto_generated_dimdoors");

	public static final TagKey<Block> MINOR_PLANTS = of("minor_plants");
	public static final TagKey<Block> DECAYS_TO_AIR = decaysTo("air");
	public static final TagKey<Block> DECAYS_TO_RAIL = decaysTo("rail");
    public static final TagKey<Block> DECAYS_TO_GRITTY_STONE = decaysTo("gritty_stone");
	public static final TagKey<Block> DECAYS_TO_SOLID_STATIC = decaysTo("solid_static");
	public static final TagKey<Block> DECAYS_TO_UNRAVELED_FENCE = decaysTo("unraveled_fence");
	public static final TagKey<Block> DECAYS_TO_UNRAVELED_GATE = decaysTo("unraveled_gate");
	public static final TagKey<Block> DECAYS_TO_UNRAVELED_BUTTON = decaysTo("unraveled_button");
	public static final TagKey<Block> DECAYS_TO_UNRAVELED_SLAB = decaysTo("unraveled_slab");
	public static final TagKey<Block> DECAYS_TO_UNRAVELED_STAIRS = decaysTo("unraveled_stairs");
	public static final TagKey<Block> DECAYS_TO_TO_GLASS_PANE = decaysTo("glass_pane");
	public static final TagKey<Block> DECAYS_TO_RUST = decaysTo("rust");
	public static final TagKey<Block> DECAYS_TO_UNRAVELED_SPIKE = decaysTo("unraveled_spike");
	public static final TagKey<Block> DECAYS_TO_WITHER_ROSE = decaysTo("wither_rose");
	public static final TagKey<Block> DECAYS_TO_CLAY = decaysTo("clay");
	public static final TagKey<Block> DECAYS_TO_CLAY_FENCE = decaysTo("clay_fence");
	public static final TagKey<Block> DECAYS_TO_CLAY_GATE = decaysTo("clay_gate");
	public static final TagKey<Block> DECAYS_TO_CLAY_BUTTON = decaysTo("clay_button");
	public static final TagKey<Block> DECAYS_TO_CLAY_SLAB = decaysTo("clay_slab");
	public static final TagKey<Block> DECAYS_TO_CLAY_STAIRS = decaysTo("clay_stairs");
    public static final TagKey<Block> DECAYS_TO_CLAY_WALL = decaysTo("clay_stairs");

	public static final TagKey<Block> DECAYS_TO_DARK_SAND = decaysTo("dark_sand");
	public static final TagKey<Block> DECAYS_TO_DARK_SAND_FENCE = decaysTo("decay_dark_sand_fence");
	public static final TagKey<Block> DECAYS_TO_DARK_SAND_BUTTON = decaysTo("decay_dark_sand_button");
	public static final TagKey<Block> DECAYS_TO_DARK_SAND_SLAB = decaysTo("decay_dark_sand_slab");
	public static final TagKey<Block> DECAYS_TO_DARK_SAND_STAIRS = decaysTo("decay_dark_sand_stairs");
	public static final TagKey<Block> DECAYS_TO_DARK_SAND_WALL = decaysTo("decay_dark_sand_wall");

	public static final TagKey<Block> DECAYS_TO_AMALGAM = decaysTo("amalgam");
    public static final TagKey<Block> DECAYS_TO_AMALGAM_DOOR = decaysTo("amalgam_door");
	public static final TagKey<Block> DECAYS_TO_DIRT = decaysTo("dirt");
	public static final TagKey<Block> DECAYS_TO_DRIFTWOOD_PLANK = decaysTo("driftwood_plank");
	public static final TagKey<Block> DECAYS_TO_DRIFTWOOD_FENCE = decaysTo("driftwood_fence");
	public static final TagKey<Block> DECAYS_TO_DRIFTWOOD_GATE = decaysTo("driftwood_gate");
	public static final TagKey<Block> DECAYS_TO_DRIFTWOOD_BUTTON = decaysTo("driftwood_button");
	public static final TagKey<Block> DECAYS_TO_DRIFTWOOD_SLAB = decaysTo("driftwood_slab");
	public static final TagKey<Block> DECAYS_TO_DRIFTWOOD_STAIRS = decaysTo("driftwood_stairs");
	public static final TagKey<Block> DECAYS_TO_CHEST = decaysTo("chest");
	public static final TagKey<Block> DECAYS_TO_SKELETON_SKULL = decaysTo("skeleton_skull");
	public static final TagKey<Block> DECAYS_TO_SKELETON_WALL_SKULL = decaysTo("skeleton_wall_skull");
	public static final TagKey<Block> DECAYS_TO_NETHERWART_BLOCK = decaysTo("netherwart_block");
	public static final TagKey<Block> DECAYS_TO_AMALGAM_ORE = decaysTo("amalgam_ore");
	public static final TagKey<Block> DECAYS_TO_CLOD_ORE = decaysTo("clod_ore");
	public static final TagKey<Block> DECAYS_TO_COBBLESTONE = decaysTo("cobblestone");
	public static final TagKey<Block> DECAYS_TO_DRIFTWOOD_DOOR = decaysTo("driftwood_door");
	public static final TagKey<Block> DECAYS_TO_MUD = decaysTo("mud");
	public static final TagKey<Block> DECAYS_TO_MUD_FENCE = decaysTo("mud_fence");
	public static final TagKey<Block> DECAYS_TO_MUD_GATE = decaysTo("mud_gate");
	public static final TagKey<Block> DECAYS_TO_MUD_BUTTON = decaysTo("mud_button");
	public static final TagKey<Block> DECAYS_TO_MUD_SLAB = decaysTo("mud_slab");
	public static final TagKey<Block> DECAYS_TO_MUD_STAIRS = decaysTo("mud_stairs");
	public static final TagKey<Block> DECAYS_TO_COBBLESTONE_SLAB = decaysTo("cobblestone_slab");
	public static final TagKey<Block> DECAYS_TO_COBBLESTONE_STAIRS = decaysTo("cobblestone_stairs");
	public static final TagKey<Block> DECAYS_TO_COBBLESTONE_WALL = decaysTo("cobblestone_wall");
	public static final TagKey<Block> DECAYS_TO_RED_SANDSTONE = decaysTo("red_sandstone");
	public static final TagKey<Block> DECAYS_TO_SANDSTONE = decaysTo("sandstone");

	public static final TagKey<Block> DECAYS_TO_BLACKSTONE = decaysTo("blackstone");
	public static final TagKey<Block> DECAYS_TO_BLACKSTONE_SLAB = decaysTo("blackstone_slab");
	public static final TagKey<Block> DECAYS_TO_BLACKSTONE_STAIRS = decaysTo("blackstone_stairs");
	public static final TagKey<Block> DECAYS_TO_BLACKSTONE_WALL = decaysTo("blackstone_wall");
	public static final TagKey<Block> DECAYS_TO_DEEPSLATE = decaysTo("deepslate");
	public static final TagKey<Block> DECAYS_TO_DEEPSLATE_SLAB = decaysTo("deepslate_slab");
	public static final TagKey<Block> DECAYS_TO_DEEPSLATE_STAIRS = decaysTo("deepslate_stairs");
	public static final TagKey<Block> DECAYS_TO_DEEPSLATE_WALL = decaysTo("deepslate_wall");
	public static final TagKey<Block> DECAYS_TO_DIORITE = decaysTo("diorite");
	public static final TagKey<Block> DECAYS_TO_DIORITE_SLAB = decaysTo("diorite_slab");
	public static final TagKey<Block> DECAYS_TO_DIORITE_STAIRS = decaysTo("diorite_stairs");
	public static final TagKey<Block> DECAYS_TO_DIORITE_WALL = decaysTo("diorite_wall");
	public static final TagKey<Block> DECAYS_TO_ENDSTONE = decaysTo("endstone");
	public static final TagKey<Block> DECAYS_TO_ENDSTONE_SLAB = decaysTo("endstone_slab");
	public static final TagKey<Block> DECAYS_TO_ENDSTONE_STAIRS = decaysTo("endstone_stairs");
	public static final TagKey<Block> DECAYS_TO_ENDSTONE_WALL = decaysTo("endstone_wall");
	public static final TagKey<Block> DECAYS_TO_FURNACE = decaysTo("furnace");
	public static final TagKey<Block> DECAYS_TO_GRANITE = decaysTo("granite");
	public static final TagKey<Block> DECAYS_TO_GRANITE_SLAB = decaysTo("granite_slab");
	public static final TagKey<Block> DECAYS_TO_GRANITE_STAIRS = decaysTo("granite_stairs");
	public static final TagKey<Block> DECAYS_TO_NETHERRACK = decaysTo("netherrack");
	public static final TagKey<Block> DECAYS_TO_NETHERRACK_FENCE = decaysTo("netherrack_fence");
	public static final TagKey<Block> DECAYS_TO_NETHERRACK_SLAB = decaysTo("netherrack_slab");
	public static final TagKey<Block> DECAYS_TO_NETHERRACK_STAIRS = decaysTo("netherrack_stairs");
	public static final TagKey<Block> DECAYS_TO_NETHERRACK_WALL = decaysTo("netherrack_wall");
	public static final TagKey<Block> DECAYS_TO_PRISMARINE = decaysTo("prismarine");
	public static final TagKey<Block> DECAYS_TO_PRISMARINE_SLAB = decaysTo("prismarine_slab");
	public static final TagKey<Block> DECAYS_TO_PRISMARINE_STAIRS = decaysTo("prismarine_stairs");
	public static final TagKey<Block> DECAYS_TO_PRISMARINE_WALL = decaysTo("prismarine_wall");
	public static final TagKey<Block> DECAYS_TO_STONE = decaysTo("stone");
    public static final TagKey<Block> DECAYS_TO_GLASS = decaysTo("glass");
    public static final TagKey<Block> DECAYS_TO_GRAVEL = decaysTo("grave");
    public static final TagKey<Block> DECAYS_TO_GRAVEL_FENCE = decaysTo("gravel_fence");
    public static final TagKey<Block> DECAYS_TO_GRAVEL_GATE = decaysTo("gravel_gate");
    public static final TagKey<Block> DECAYS_TO_GRAVEL_BUTTON = decaysTo("gravel_button");
    public static final TagKey<Block> DECAYS_TO_GRAVEL_SLAB = decaysTo("gravel_slab");
    public static final TagKey<Block> DECAYS_TO_GRAVEL_STAIRS = decaysTo("gravel_stairs");
    public static final TagKey<Block> DECAYS_TO_GRAVEL_WALL = decaysTo("gravel_wall");
	public static final TagKey<Block> DECAYS_TO_RED_SAND_SLAB = decaysTo("red_sand_slab");
	public static final TagKey<Block> DECAYS_TO_RED_SAND_STAIRS = decaysTo("red_sand_stairs");
	public static final TagKey<Block> DECAYS_TO_RED_SAND_WALL = decaysTo("red_sand_wall");
	public static final TagKey<Block> DECAYS_TO_SAND = decaysTo("sand");
	public static final TagKey<Block> DECAYS_TO_SAND_SLAB = decaysTo("sand_slab");
	public static final TagKey<Block> DECAYS_TO_SAND_STAIRS = decaysTo("sand_stairs");
	public static final TagKey<Block> DECAYS_TO_SAND_WALL = decaysTo("sand_wall");
	public static final TagKey<Block> DECAYS_TO_DRIFTWOOD_WOOD = decaysTo("driftwood_wood");
	public static final TagKey<Block> DECAYS_TO_BASALT = decaysTo("basalt");
	public static final TagKey<Block> DECAYS_TO_DARK_PRISMARINE = decaysTo("dark_prismarine");
	public static final TagKey<Block> DECAYS_TO_DARK_PRISMARINE_SLAB = decaysTo("dark_prismarine_slab");
	public static final TagKey<Block> DECAYS_TO_DARK_PRISMARINE_STAIRS = decaysTo("dark_prismarine_stairs");
	public static final TagKey<Block> DECAYS_TO_CLOD_BLOCK = decaysTo("clod_block");
	public static final TagKey<Block> DECAYS_TO_OBSIDIAN = decaysTo("obsidian");
	public static final TagKey<Block> DECAYS_TO_STONE_BRICKS = decaysTo("stone_bricks");
	public static final TagKey<Block> DECAYS_TO_STONE_BRICK_SLAB = decaysTo("stone_brick_slab");
	public static final TagKey<Block> DECAYS_TO_STONE_BRICK_STAIRS = decaysTo("stone_brick_stairs");
	public static final TagKey<Block> DECAYS_TO_STONE_BRICK_WALL = decaysTo("stone_brick_wall");
	public static final TagKey<Block> DECAYS_TO_DRIFTWOOD_LOG = decaysTo("driftwood_log");
	public static final TagKey<Block> DECAYS_TO_UNRAVELED_FABRIC = decaysTo("unraveled_fabric");
    public static final TagKey<Block> DECAYS_TO_MOSS_CARPET = decaysTo("moss_carpet");

    private static TagKey<Block> decaysTo(String id) {
        return of("decays_to/" + id);
    }

	private static TagKey<Block> of(String id) {
		return TagKey.create(Registries.BLOCK, DimensionalDoors.id(id));
	}
}
