package org.dimdev.dimdoors.datagen;

import java.util.function.Consumer;

import org.dimdev.dimdoors.item.ModItems;
import org.dimdev.dimdoors.world.ModDimensions;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.ChangedDimensionCriterion;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

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
		Advancement limbo = Advancement.Task.create()
				.display(makeDisplay(ModItems.UNRAVELLED_FABRIC, "limbo"))
				.criterion("changed_dimension", ChangedDimensionCriterion.Conditions.to(ModDimensions.LIMBO))
				.parent(root)
				.build(advancementConsumer, "dimdoors:dimdoors/limbo");
		Advancement.Task.create()
				.display(makeDisplay(ModItems.ETERNAL_FLUID_BUCKET, "escape_limbo"))
				.criterion("changed_dimension", new ChangedDimensionCriterion.Conditions(EntityPredicate.Extended.EMPTY, ModDimensions.LIMBO, null))
				.parent(limbo)
				.build(advancementConsumer, "dimdoors:dimdoors/escape_limbo");
	}
}
