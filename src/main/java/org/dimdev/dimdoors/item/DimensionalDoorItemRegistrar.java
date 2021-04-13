package org.dimdev.dimdoors.item;

import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;

import net.minecraft.block.Block;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TallBlockItem;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;

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
	private final Map<Block, Pair<Identifier, Function<Block, Item>>> toBeMapped = new HashMap<>();

	public DimensionalDoorItemRegistrar(Registry<Item> registry) {
		this.registry = registry;

		init();
		RegistryEntryAddedCallback.event(registry).register(new ItemRegistryEntryAddedListener(this));
	}

	private void init() {
		new ArrayList<>(registry.getEntries())
				.forEach(entry -> handleEntry(entry.getKey().getValue(), entry.getValue()));
	}

	public void handleEntry(Identifier identifier, Item item) {
		if (DimensionalDoorsInitializer.getConfig().getDoorsConfig().isAllowed(identifier) && !DoorData.PARENT_ITEMS.contains(item)) {
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

	private void handleEntry(Identifier identifier, Item item, Block block, QuadFunction<Block, Item.Settings, Consumer<? super EntranceRiftBlockEntity>, Item, ? extends Item> constructor) {

		if (!(block instanceof DimensionalDoorBlock)
				&& !(block instanceof DimensionalTrapdoorBlock)
				&& (block instanceof DoorBlock || block instanceof TrapdoorBlock)) {
			Item.Settings settings = ItemExtensions.getSettings(item).group(ModItems.DIMENSIONAL_DOORS);

			Function<Block, Item> dimItemConstructor = (dimBlock) -> constructor.apply(dimBlock, settings, rift -> rift.setDestination(new PublicPocketTarget()), item);

			if (!blocksAlreadyNotifiedAbout.containsKey(block)) {
				toBeMapped.put(block, new Pair<>(identifier, dimItemConstructor));
				return;
			}

			register(identifier, dimItemConstructor.apply(blocksAlreadyNotifiedAbout.get(block)));
		}
	}

	public void notifyBlockMapped(Block original, Block dimBlock) {
		if (!toBeMapped.containsKey(original)) {
			blocksAlreadyNotifiedAbout.put(original, dimBlock);
			return;
		}
		Pair<Identifier, Function<Block, Item>> pair = toBeMapped.get(original);
		register(pair.getLeft(), pair.getRight().apply(dimBlock));
	}

	private void register(Identifier identifier, Item dimItem) {
		Identifier gennedId = new Identifier("dimdoors", PREFIX + identifier.getPath());
		Registry.register(registry, gennedId, dimItem);

		//if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			//BlockRenderLayerMap.INSTANCE.putBlock(newBlock, RenderLayer.getCutout());
		//}
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
