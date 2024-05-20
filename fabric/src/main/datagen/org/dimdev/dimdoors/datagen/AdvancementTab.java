package org.dimdev.dimdoors.datagen;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.criteria.PocketSpawnPointSetCondition;
import org.dimdev.dimdoors.criteria.RiftTrackedCriterion;
import org.dimdev.dimdoors.item.ModItems;
import org.dimdev.dimdoors.world.ModDimensions;

import java.util.function.Consumer;

public class AdvancementTab implements Consumer<Consumer<Advancement>> {
	static DisplayInfo makeDisplay(ItemLike item, String titleKey) {
		return new DisplayInfo(item.asItem().getDefaultInstance(),
				Component.translatable("dimdoors.advancement." + titleKey),
				Component.translatable("dimdoors.advancement." + titleKey + ".desc"),
				DimensionalDoors.id("textures/block/unravelled_fabric.png"),
				FrameType.TASK,
				true,
				true,
				false
		);
	}

	static DisplayInfo makeDisplay(ItemLike item, String titleKey, FrameType advancementFrame) {
		return new DisplayInfo(item.asItem().getDefaultInstance(),
				Component.translatable("dimdoors.advancement." + titleKey),
				Component.translatable("dimdoors.advancement." + titleKey + ".desc"),
				DimensionalDoors.id("textures/block/unravelled_fabric.png"),
				advancementFrame,
				true,
				true,
				false
		);
	}

	@Override
	public void accept(Consumer<Advancement> advancementConsumer) {
		Advancement root = Advancement.Builder.advancement()
				.display(makeDisplay(ModItems.RIFT_BLADE.get(), "root"))
				.addCriterion("inventory_changed", InventoryChangeTrigger.TriggerInstance.hasItems(Items.ENDER_PEARL))
				.save(advancementConsumer, "dimdoors:dimdoors/root");
		Advancement.Builder.advancement()
				.display(makeDisplay(ModItems.WORLD_THREAD.get(), "string_theory"))
				.addCriterion("inventory_changed", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.WORLD_THREAD.get()))
				.parent(root)
				.save(advancementConsumer, "dimdoors:dimdoors/string_theory");
		Advancement holeInTheSky = Advancement.Builder.advancement()
				.display(makeDisplay(ModItems.RIFT_CONFIGURATION_TOOL.get(), "hole_in_the_sky"))
				.addCriterion("encounter_rift", new RiftTrackedCriterion.Conditions(ContextAwarePredicate.ANY))
				.parent(root)
				.save(advancementConsumer, "dimdoors:dimdoors/hole_in_the_sky");
		Advancement darkOstiology = Advancement.Builder.advancement()
				.display(makeDisplay(BuiltInRegistries.BLOCK.get(new ResourceLocation("dimdoors:block_ag_dim_minecraft_oak_door")), "dark_ostiology"))
				.addCriterion("place_door", EnterBlockTrigger.TriggerInstance.entersBlock(BuiltInRegistries.BLOCK.get(new ResourceLocation("dimdoors:block_ag_dim_minecraft_oak_door"))))
				.parent(holeInTheSky)
				.save(advancementConsumer, "dimdoors:dimdoors/dark_ostiology");
		Advancement.Builder.advancement()
				.display(makeDisplay(BuiltInRegistries.BLOCK.get(new ResourceLocation("dimdoors:block_ag_dim_minecraft_iron_door")), "public_pocket"))
				.parent(darkOstiology)
				.addCriterion("public_pocket", ChangeDimensionTrigger.TriggerInstance.changedDimensionTo(ModDimensions.PUBLIC))
				.save(advancementConsumer, "dimdoors:dimdoors/public_pocket");
		Advancement.Builder.advancement()
				.display(makeDisplay(BuiltInRegistries.BLOCK.get(new ResourceLocation("dimdoors:block_ag_dim_minecraft_iron_door")), "home_away_from_home"))
				.parent(darkOstiology)
				.addCriterion("private_pocket", ChangeDimensionTrigger.TriggerInstance.changedDimensionTo(ModDimensions.PERSONAL))
				.save(advancementConsumer, "dimdoors:dimdoors/home_away_from_home");
		Advancement.Builder.advancement()
				.display(makeDisplay(Blocks.RESPAWN_ANCHOR, "out_of_time"))
				.addCriterion("spawn", new PocketSpawnPointSetCondition.Conditions(ContextAwarePredicate.ANY))
				.parent(darkOstiology)
				.save(advancementConsumer, "dimdoors:dimdoors/out_of_time");
		Advancement doorToAdventure = Advancement.Builder.advancement()
				.display(makeDisplay(BuiltInRegistries.BLOCK.get(new ResourceLocation("dimdoors:block_ag_dim_dimdoors_gold_door")), "door_to_adventure"))
				.parent(holeInTheSky)
				.addCriterion("enter_dungeon", ChangeDimensionTrigger.TriggerInstance.changedDimensionTo((ModDimensions.DUNGEON)))
				.save(advancementConsumer, "dimdoors:dimdoors/door_to_adventure");
//		Advancement.Builder.advancement() TODO: Figure out what the heck ItemUsedOnBlockCriterion
//				.display(makeDisplay(Items.CHEST, "lost_and_found"))
//				.parent(doorToAdventure)
//				.addCriterion("open_chest", new ItemUsedOnBlockCriterion.Conditions
//						(
//								EntityPredicate.Extended.EMPTY,
//								new LocationPredicate(
//										NumberRange.FloatRange.ANY,
//										NumberRange.FloatRange.ANY,
//										NumberRange.FloatRange.ANY,
//										null,
//										null,
//										ModDimensions.DUNGEON,
//										null,
//										LightPredicate.ANY,
//										BlockPredicate.Builder.create().blocks(Blocks.CHEST, Blocks.TRAPPED_CHEST).save(),
//								FluidPredicate.ANY
//								),
//								new ItemPredicate(
//										null,
//										null,
//										NumberRange.IntRange.ANY,
//										NumberRange.IntRange.ANY,
//										EnchantmentPredicate.ARRAY_OF_ANY,
//										EnchantmentPredicate.ARRAY_OF_ANY,
//										null,
//										NbtPredicate.ANY
//								)
//						)
//				)
//				.save(advancementConsumer, "dimdoors:dimdoors/lost_and_found");
		Advancement.Builder.advancement()
				.display(makeDisplay(ModBlocks.BLACK_FABRIC.get(), "darklight"))
				.parent(doorToAdventure)
				.addCriterion("get_fabric", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(TagKey.create(Registries.ITEM, DimensionalDoors.id("fabric"))).build()))
				.save(advancementConsumer, "dimdoors:dimdoors/darklight");
		Advancement enterLimbo = Advancement.Builder.advancement()
				.display(makeDisplay(ModItems.MONOLITH_SPAWNER.get(), "enter_limbo"))
				.parent(doorToAdventure)
				.addCriterion("enter_limbo", ChangeDimensionTrigger.TriggerInstance.changedDimensionTo(ModDimensions.LIMBO))
				.save(advancementConsumer, "dimdoors:dimdoors/enter_limbo");
		Advancement.Builder.advancement()
				.display(makeDisplay(ModBlocks.UNRAVELLED_FABRIC.get(), "world_unfurled"))
				.parent(enterLimbo)
				.addCriterion("get_the_unravelled", InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.UNRAVELLED_FABRIC.get()))
				.save(advancementConsumer, "dimdoors:dimdoors/world_unfurled");

		Advancement.Builder.advancement()
				.display(makeDisplay(ModItems.INFRANGIBLE_FIBER.get(), "unravelled_but_immutable"))
				.parent(enterLimbo)
				.addCriterion("get_the_immutable", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.INFRANGIBLE_FIBER.get()))
				.save(advancementConsumer, "dimdoors:dimdoors/unravelled_but_immutable");

		Advancement.Builder.advancement()
				.display(makeDisplay(ModItems.FRAYED_FILAMENTS.get(), "fuzzy_unreality"))
				.parent(enterLimbo)
				.addCriterion("get_the_immutable", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.FRAYED_FILAMENTS.get()))
				.save(advancementConsumer, "dimdoors:dimdoors/fuzzy_unreality");

//		Advancement.Task.create()
//				.display(makeDisplay(ModItems.IRON_DIMENSIONAL_DOOR, "public_pocket"))
//				.addCriterion("changed_dimension", ChangedDimensionCriterion.Conditions.to(ModDimensions.PUBLIC))
//				.parent(root)
//				.save(advancementConsumer, "dimdoors:dimdoors/public_pocket");
//		Advancement.Task.create()
//				.display(makeDisplay(ModItems.QUARTZ_DIMENSIONAL_DOOR, "private_pocket"))
//				.addCriterion("changed_dimension", ChangedDimensionCriterion.Conditions.to(ModDimensions.PERSONAL))
//				.parent(root)
//				.save(advancementConsumer, "dimdoors:dimdoors/private_pocket");
//		Advancement.Task.create()
//				.display(makeDisplay(ModItems.GOLD_DIMENSIONAL_DOOR, "dungeon"))
//				.addCriterion("changed_dimension", ChangedDimensionCriterion.Conditions.to(ModDimensions.DUNGEON))
//				.parent(root)
//				.save(advancementConsumer, "dimdoors:dimdoors/dungeon");
//		Advancement limbo = Advancement.Task.create()
//				.display(makeDisplay(ModItems.UNRAVELLED_FABRIC, "limbo"))
//				.addCriterion("changed_dimension", ChangedDimensionCriterion.Conditions.to(ModDimensions.LIMBO))
//				.parent(root)
//				.save(advancementConsumer, "dimdoors:dimdoors/limbo");
//		Advancement.Task.create()
//				.display(makeDisplay(ModItems.ETERNAL_FLUID_BUCKET, "escape_limbo"))
//				.addCriterion("changed_dimension", new ChangedDimensionCriterion.Conditions(EntityPredicate.Extended.EMPTY, ModDimensions.LIMBO, null))
//				.parent(limbo)
//				.save(advancementConsumer, "dimdoors:dimdoors/escape_limbo");
	}
}
