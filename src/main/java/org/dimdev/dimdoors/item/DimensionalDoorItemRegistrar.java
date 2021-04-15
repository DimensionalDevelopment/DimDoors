package org.dimdev.dimdoors.item;

import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;

import net.minecraft.block.Block;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.*;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.api.util.function.QuadFunction;
import org.dimdev.dimdoors.block.door.DimensionalDoorBlock;
import org.dimdev.dimdoors.block.door.DimensionalTrapdoorBlock;
import org.dimdev.dimdoors.block.door.data.DoorData;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.listener.ItemRegistryEntryAddedListener;
import org.dimdev.dimdoors.rift.targets.PublicPocketTarget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class DimensionalDoorItemRegistrar {
	private static final String PREFIX = "autogen_dimensional_";

	private final Registry<Item> registry;

	private final Map<Block, Block> blocksAlreadyNotifiedAbout = new HashMap<>();
	private final Map<Block, Triple<Identifier, Item, Function<Block, BlockItem>>> toBeMapped = new HashMap<>();

	private final Map<Item, Function<ItemPlacementContext, ActionResult>> placementFunctions = new HashMap<>();

	public DimensionalDoorItemRegistrar(Registry<Item> registry) {
		this.registry = registry;

		init();
		RegistryEntryAddedCallback.event(registry).register(new ItemRegistryEntryAddedListener(this));
	}

	public boolean isRegistered(Item item) {
		return placementFunctions.containsKey(item);
	}

	public ActionResult place(Item item, ItemPlacementContext context) {
		return placementFunctions.get(item).apply(context);
	}

	private void init() {
		new ArrayList<>(registry.getEntries())
				.forEach(entry -> handleEntry(entry.getKey().getValue(), entry.getValue()));
	}

	public void handleEntry(Identifier identifier, Item item) {
		if (DimensionalDoorsInitializer.getConfig().getDoorsConfig().isAllowed(identifier)) {
			if (item instanceof TallBlockItem) {
				Block block = ((TallBlockItem) item).getBlock();
				handleEntry(identifier, item, block, AutoGenDimensionalDoorItem::new);
			} else if (item instanceof BlockItem) {
				Block block = ((BlockItem) item).getBlock();
				if (block instanceof DoorBlock) {
					handleEntry(identifier, item, block, AutoGenDimensionalDoorItem::new);
				} else {
					handleEntry(identifier, item, block, AutoGenDimensionalTrapdoorItem::new);
				}
			}
		}
	}

	private void handleEntry(Identifier identifier, Item item, Block block, QuadFunction<Block, Item.Settings, Consumer<? super EntranceRiftBlockEntity>, Item, ? extends BlockItem> constructor) {

		if (!(block instanceof DimensionalDoorBlock)
				&& !(block instanceof DimensionalTrapdoorBlock)
				&& (block instanceof DoorBlock || block instanceof TrapdoorBlock)) {
			Item.Settings settings = ItemExtensions.getSettings(item).group(DoorData.PARENT_ITEMS.contains(item) || DoorData.PARENT_BLOCKS.contains(block) ? null : ModItems.DIMENSIONAL_DOORS);

			Function<Block, BlockItem> dimItemConstructor = (dimBlock) -> constructor.apply(dimBlock, settings, rift -> rift.setDestination(new PublicPocketTarget()), item);

			if (!blocksAlreadyNotifiedAbout.containsKey(block)) {
				toBeMapped.put(block, new ImmutableTriple<>(identifier, item, dimItemConstructor));
				return;
			}

			register(identifier, item, dimItemConstructor.apply(blocksAlreadyNotifiedAbout.get(block)));
		}
	}

	public void notifyBlockMapped(Block original, Block dimBlock) {
		if (!toBeMapped.containsKey(original)) {
			blocksAlreadyNotifiedAbout.put(original, dimBlock);
			return;
		}
		Triple<Identifier, Item, Function<Block, BlockItem>> triple = toBeMapped.get(original);
		register(triple.getLeft(), triple.getMiddle(), triple.getRight().apply(dimBlock));
	}

	private void register(Identifier identifier, Item original, BlockItem dimItem) {
		Identifier gennedId = new Identifier("dimdoors", PREFIX + identifier.getPath());
		if (!DoorData.PARENT_ITEMS.contains(original)) {
			Registry.register(registry, gennedId, dimItem);
		}
		placementFunctions.put(original, dimItem::place);
	}

	private static class AutoGenDimensionalDoorItem extends DimensionalDoorItem {
		private final Item originalItem;

		public AutoGenDimensionalDoorItem(Block block, Settings settings, Consumer<? super EntranceRiftBlockEntity> setupFunction, Item originalItem) {
			super(block, settings, setupFunction);
			this.originalItem = originalItem;
		}

		@Override
		public Text getName(ItemStack stack) {
			return new TranslatableText("dimdoors.autogen_item_prefix", I18n.translate(originalItem.getTranslationKey()));
		}

		@Override
		public Text getName() {
			return new TranslatableText("dimdoors.autogen_item_prefix", I18n.translate(originalItem.getTranslationKey()));
		}
	}

	private static class AutoGenDimensionalTrapdoorItem extends DimensionalTrapdoorItem {
		private final Item originalItem;

		public AutoGenDimensionalTrapdoorItem(Block block, Settings settings, Consumer<? super EntranceRiftBlockEntity> setupFunction, Item originalItem) {
			super(block, settings, setupFunction);
			this.originalItem = originalItem;
		}

		@Override
		public Text getName(ItemStack stack) {
			return new TranslatableText("dimdoors.autogen_item_prefix", I18n.translate(originalItem.getTranslationKey()));
		}

		@Override
		public Text getName() {
			return new TranslatableText("dimdoors.autogen_item_prefix", I18n.translate(originalItem.getTranslationKey()));
		}
	}
}
