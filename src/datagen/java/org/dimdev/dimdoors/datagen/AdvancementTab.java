package org.dimdev.dimdoors.datagen;

import java.util.function.Consumer;

import org.dimdev.dimdoors.criteria.PocketSpawnPointSetCondition;
import org.dimdev.dimdoors.criteria.RiftTrackedCriterion;
import org.dimdev.dimdoors.item.ModItems;
import org.dimdev.dimdoors.world.ModDimensions;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.ChangedDimensionCriterion;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.ItemUsedOnBlockCriterion;
import net.minecraft.advancement.criterion.PlacedBlockCriterion;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.predicate.FluidPredicate;
import net.minecraft.predicate.LightPredicate;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.tag.TagRegistry;

public class AdvancementTab implements Consumer<Consumer<Advancement>> {
	static AdvancementDisplay makeDisplay(ItemConvertible item, String titleKey) {
		return new AdvancementDisplay(item.asItem().getDefaultStack(),
				new TranslatableText("dimdoors.advancement." + titleKey),
				new TranslatableText("dimdoors.advancement." + titleKey + ".desc"),
				new Identifier("dimdoors:textures/block/unravelled_fabric.png"),
				AdvancementFrame.TASK,
				true,
				true,
				false
		);
	}

	static AdvancementDisplay makeDisplay(ItemConvertible item, String titleKey, AdvancementFrame advancementFrame) {
		return new AdvancementDisplay(item.asItem().getDefaultStack(),
				new TranslatableText("dimdoors.advancement." + titleKey),
				new TranslatableText("dimdoors.advancement." + titleKey + ".desc"),
				new Identifier("dimdoors:textures/block/unravelled_fabric.png"),
				advancementFrame,
				true,
				true,
				false
		);
	}

	@Override
	public void accept(Consumer<Advancement> advancementConsumer) {
		Advancement root = Advancement.Task.create()
				.display(makeDisplay(ModItems.RIFT_BLADE, "root"))
				.criterion("inventory_changed", InventoryChangedCriterion.Conditions.items(Items.ENDER_PEARL))
				.build(advancementConsumer, "dimdoors:dimdoors/root");
		Advancement.Task.create()
				.display(makeDisplay(ModItems.WORLD_THREAD, "string_theory"))
				.criterion("inventory_changed", InventoryChangedCriterion.Conditions.items(ModItems.WORLD_THREAD))
				.parent(root)
				.build(advancementConsumer, "dimdoors:dimdoors/string_theory");
		Advancement holeInTheSky = Advancement.Task.create()
				.display(makeDisplay(ModItems.RIFT_CONFIGURATION_TOOL, "hole_in_the_sky"))
				.criterion("encounter_rift", new RiftTrackedCriterion.Conditions(EntityPredicate.Extended.EMPTY))
				.parent(root)
				.build(advancementConsumer, "dimdoors:dimdoors/hole_in_the_sky");
		Advancement darkOstiology = Advancement.Task.create()
				.display(makeDisplay(Registry.BLOCK.get(new Identifier("dimdoors:oak_dimensional_door")), "dark_ostiology"))
				.criterion("place_door", PlacedBlockCriterion.Conditions.block(Registry.BLOCK.get(new Identifier("dimdoors:oak_dimensional_door"))))
				.parent(holeInTheSky)
				.build(advancementConsumer, "dimdoors:dimdoors/dark_ostiology");
		Advancement.Task.create()
				.display(makeDisplay(Registry.BLOCK.get(new Identifier("dimdoors:iron_dimensional_door")), "public_pocket"))
				.parent(darkOstiology)
				.criterion("public_pocket", ChangedDimensionCriterion.Conditions.to(ModDimensions.PUBLIC))
				.build(advancementConsumer, "dimdoors:dimdoors/public_pocket");
		Advancement.Task.create()
				.display(makeDisplay(Registry.BLOCK.get(new Identifier("dimdoors:iron_dimensional_door")), "home_away_from_home"))
				.parent(darkOstiology)
				.criterion("private_pocket", ChangedDimensionCriterion.Conditions.to(ModDimensions.PERSONAL))
				.build(advancementConsumer, "dimdoors:dimdoors/home_away_from_home");
		Advancement.Task.create()
				.display(makeDisplay(Blocks.RESPAWN_ANCHOR, "out_of_time"))
				.criterion("spawn", new PocketSpawnPointSetCondition.Conditions(EntityPredicate.Extended.EMPTY))
				.parent(darkOstiology)
				.build(advancementConsumer, "dimdoors:dimdoors/out_of_time");
		Advancement doorToAdventure = Advancement.Task.create()
				.display(makeDisplay(Registry.BLOCK.get(new Identifier("dimdoors:gold_dimensional_door")), "door_to_adventure"))
				.parent(holeInTheSky)
				.criterion("enter_dungeon", ChangedDimensionCriterion.Conditions.to(ModDimensions.DUNGEON))
				.build(advancementConsumer, "dimdoors:dimdoors/door_to_adventure");
		Advancement.Task.create()
				.display(makeDisplay(Items.CHEST, "lost_and_found"))
				.parent(doorToAdventure)
				.criterion("open_chest", new ItemUsedOnBlockCriterion.Conditions
						(
								EntityPredicate.Extended.EMPTY,
								new LocationPredicate(
										NumberRange.FloatRange.ANY,
										NumberRange.FloatRange.ANY,
										NumberRange.FloatRange.ANY,
										null,
										null,
										ModDimensions.DUNGEON,
										null,
										LightPredicate.ANY,
										BlockPredicate.Builder.create().blocks(Blocks.CHEST, Blocks.TRAPPED_CHEST).build(),
								FluidPredicate.ANY
								),
								new ItemPredicate(
										null,
										null,
										NumberRange.IntRange.ANY,
										NumberRange.IntRange.ANY,
										EnchantmentPredicate.ARRAY_OF_ANY,
										EnchantmentPredicate.ARRAY_OF_ANY,
										null,
										NbtPredicate.ANY
								)
						)
				)
				.build(advancementConsumer, "dimdoors:dimdoors/lost_and_found");
		Advancement.Task.create()
				.display(makeDisplay(ModItems.BLACK_FABRIC, "darklight"))
				.parent(doorToAdventure)
				.criterion("get_fabric", InventoryChangedCriterion.Conditions.items(ItemPredicate.Builder.create().tag(TagRegistry.item(new Identifier("dimdoors", "fabric"))).build()))
				.build(advancementConsumer, "dimdoors:dimdoors/darklight");
		Advancement enterLimbo = Advancement.Task.create()
				.display(makeDisplay(ModItems.MONOLITH_SPAWNER, "enter_limbo"))
				.parent(doorToAdventure)
				.criterion("enter_limbo", ChangedDimensionCriterion.Conditions.to(ModDimensions.LIMBO))
				.build(advancementConsumer, "dimdoors:dimdoors/enter_limbo");
		Advancement.Task.create()
				.display(makeDisplay(ModItems.UNRAVELLED_FABRIC, "world_unfurled"))
				.parent(enterLimbo)
				.criterion("get_fabric", InventoryChangedCriterion.Conditions.items(ModItems.UNRAVELLED_FABRIC))
				.build(advancementConsumer, "dimdoors:dimdoors/world_unfurled");

//		Advancement.Task.create()
//				.display(makeDisplay(ModItems.IRON_DIMENSIONAL_DOOR, "public_pocket"))
//				.criterion("changed_dimension", ChangedDimensionCriterion.Conditions.to(ModDimensions.PUBLIC))
//				.parent(root)
//				.build(advancementConsumer, "dimdoors:dimdoors/public_pocket");
//		Advancement.Task.create()
//				.display(makeDisplay(ModItems.QUARTZ_DIMENSIONAL_DOOR, "private_pocket"))
//				.criterion("changed_dimension", ChangedDimensionCriterion.Conditions.to(ModDimensions.PERSONAL))
//				.parent(root)
//				.build(advancementConsumer, "dimdoors:dimdoors/private_pocket");
//		Advancement.Task.create()
//				.display(makeDisplay(ModItems.GOLD_DIMENSIONAL_DOOR, "dungeon"))
//				.criterion("changed_dimension", ChangedDimensionCriterion.Conditions.to(ModDimensions.DUNGEON))
//				.parent(root)
//				.build(advancementConsumer, "dimdoors:dimdoors/dungeon");
//		Advancement limbo = Advancement.Task.create()
//				.display(makeDisplay(ModItems.UNRAVELLED_FABRIC, "limbo"))
//				.criterion("changed_dimension", ChangedDimensionCriterion.Conditions.to(ModDimensions.LIMBO))
//				.parent(root)
//				.build(advancementConsumer, "dimdoors:dimdoors/limbo");
//		Advancement.Task.create()
//				.display(makeDisplay(ModItems.ETERNAL_FLUID_BUCKET, "escape_limbo"))
//				.criterion("changed_dimension", new ChangedDimensionCriterion.Conditions(EntityPredicate.Extended.EMPTY, ModDimensions.LIMBO, null))
//				.parent(limbo)
//				.build(advancementConsumer, "dimdoors:dimdoors/escape_limbo");
	}
}
