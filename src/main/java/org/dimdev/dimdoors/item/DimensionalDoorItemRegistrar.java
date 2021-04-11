package org.dimdev.dimdoors.item;

import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TallBlockItem;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;
import org.dimdev.dimdoors.api.util.QuadFunction;
import org.dimdev.dimdoors.block.door.DoorBlockClassCase;
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
		Block block;
		switch (DoorItemClassCase.getCase(item)) {
			case TALL_BLOCK_ITEM:
				block = ((TallBlockItem) item).getBlock();
				handleEntry(identifier, item, block, AutoGenDimensionalDoorItem::new);
				break;
			case BLOCK_ITEM:
				block = ((BlockItem) item).getBlock();
				handleEntry(identifier, item, block, AutoGenDimensionalTrapdoorItem::new);
				break;
			default:
				// do nothing
				break;
		}
	}

	private void handleEntry(Identifier identifier, Item item, Block block, QuadFunction<Block, Item.Settings, Consumer<? super EntranceRiftBlockEntity>, Item, ? extends Item> constructor) {
		switch (DoorBlockClassCase.getCase(block)) {
			case DOOR_BLOCK:
			case TRAPDOOR_BLOCK:
				Item.Settings settings = new FabricItemSettings()
						.maxCount(item.getMaxCount())
						.maxDamage(item.getMaxDamage())
						.recipeRemainder(item.getRecipeRemainder())
						.group(ModItems.DIMENSIONAL_DOORS);
				if (item.isFireproof()) settings.fireproof();

				Function<Block, Item> dimItemConstructor = (dimBlock) -> constructor.apply(dimBlock, settings, rift -> rift.setDestination(new PublicPocketTarget()), item);

				if (!blocksAlreadyNotifiedAbout.containsKey(block)) {
					toBeMapped.put(block, new Pair<>(identifier, dimItemConstructor));
					return;
				}

				register(identifier, dimItemConstructor.apply(blocksAlreadyNotifiedAbout.get(block)));
				break;
			default:
				// do nothing
				break;
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



	private enum DoorItemClassCase {
		NONE(null),
		TALL_BLOCK_ITEM(TallBlockItem.class),
		BLOCK_ITEM(BlockItem.class);

		private static final Map<Class<? extends Item>, DoorItemClassCase> CASE_MAP = new HashMap<>();

		static {
			for (DoorItemClassCase doorItemClassCase : DoorItemClassCase.values()) {
				CASE_MAP.put(doorItemClassCase.doorClazz, doorItemClassCase);
			}
		}

		public static DoorItemClassCase getCase(Item item) {
			DoorItemClassCase doorItemClassCase = CASE_MAP.get(item.getClass());
			return doorItemClassCase == null ? NONE : doorItemClassCase;
		}

		private final Class<? extends Item> doorClazz;

		DoorItemClassCase(Class<? extends Item> doorClazz) {
			this.doorClazz = doorClazz;
		}
	}

	private static class AutoGenDimensionalDoorItem extends DimensionalDoorItem {
		private final Item originalItem;

		public AutoGenDimensionalDoorItem(Block block, Settings settings, Consumer<? super EntranceRiftBlockEntity> setupFunction, Item originalItem) {
			super(block, settings, setupFunction);
			this.originalItem = originalItem;
		}

		@Override
		public Text getName(ItemStack stack) {
			return new TranslatableText("dimdoors.autogen_item_prefix", originalItem.getName().asString());
		}

		@Override
		public Text getName() {
			return new TranslatableText("dimdoors.autogen_item_prefix", originalItem.getName().asString());
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
			return new TranslatableText("dimdoors.autogen_item_prefix", originalItem.getName().asString());
		}

		@Override
		public Text getName() {
			return new TranslatableText("dimdoors.autogen_item_prefix", originalItem.getName().asString());
		}
	}
}
