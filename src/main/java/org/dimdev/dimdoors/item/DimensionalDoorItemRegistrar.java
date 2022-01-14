package org.dimdev.dimdoors.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.*;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.Registry;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.api.util.function.QuadFunction;
import org.dimdev.dimdoors.block.door.DimensionalDoorBlock;
import org.dimdev.dimdoors.block.door.DimensionalTrapdoorBlock;
import org.dimdev.dimdoors.block.door.data.DoorData;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.client.UnderlaidChildItemRenderer;
import org.dimdev.dimdoors.listener.ItemRegistryEntryAddedListener;
import org.dimdev.dimdoors.rift.targets.PublicPocketTarget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class DimensionalDoorItemRegistrar {
	public static final String PREFIX = "item_ag_dim_";

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

	public void handleEntry(Identifier identifier, Item original) {
		if (DimensionalDoorsInitializer.getConfig().getDoorsConfig().isAllowed(identifier)) {
			if (original instanceof TallBlockItem) {
				Block block = ((TallBlockItem) original).getBlock();
				handleEntry(identifier, original, block, AutoGenDimensionalDoorItem::new);
			} else if (original instanceof BlockItem) {
				Block originalBlock = ((BlockItem) original).getBlock();
				if (originalBlock instanceof DoorBlock) {
					handleEntry(identifier, original, originalBlock, AutoGenDimensionalDoorItem::new);
				} else {
					handleEntry(identifier, original, originalBlock, AutoGenDimensionalTrapdoorItem::new);
				}
			}
		}
	}

	private void handleEntry(Identifier identifier, Item original, Block originalBlock, QuadFunction<Block, Item.Settings, Consumer<? super EntranceRiftBlockEntity>, Item, ? extends BlockItem> constructor) {

		if (!(originalBlock instanceof DimensionalDoorBlock)
				&& !(originalBlock instanceof DimensionalTrapdoorBlock)
				&& (originalBlock instanceof DoorBlock || originalBlock instanceof TrapdoorBlock)) {
			Item.Settings settings = ItemExtensions.getSettings(original).group(DoorData.PARENT_ITEMS.contains(original) || DoorData.PARENT_BLOCKS.contains(originalBlock) ? null : ModItems.DIMENSIONAL_DOORS);

			Function<Block, BlockItem> dimItemConstructor = (dimBlock) -> constructor.apply(dimBlock, settings, rift -> rift.setDestination(new PublicPocketTarget()), original);

			if (!blocksAlreadyNotifiedAbout.containsKey(originalBlock)) {
				toBeMapped.put(originalBlock, new ImmutableTriple<>(identifier, original, dimItemConstructor));
				return;
			}

			register(identifier, original, dimItemConstructor.apply(blocksAlreadyNotifiedAbout.get(originalBlock)));
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
		Identifier gennedId = new Identifier("dimdoors", PREFIX + identifier.getNamespace() + "_" + identifier.getPath());
		if (!DoorData.PARENT_ITEMS.contains(original)) {
			Registry.register(registry, gennedId, dimItem);
		}
		placementFunctions.put(original, dimItem::place);
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			registerItemRenderer(dimItem);
		}
	}

	@Environment(EnvType.CLIENT)
	private void registerItemRenderer(BlockItem dimItem) {
		BuiltinItemRendererRegistry.INSTANCE.register(dimItem, Renderer.RENDERER);
	}

	// extract renderer to inner interface so it can be removed in server environment via annotation
	@Environment(EnvType.CLIENT)
	private interface Renderer {
		UnderlaidChildItemRenderer RENDERER = new UnderlaidChildItemRenderer(Items.ENDER_PEARL);
	}

	private static class AutoGenDimensionalDoorItem extends DimensionalDoorItem implements ChildItem {
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

		@Override
		public Item getOriginalItem() {
			return originalItem;
		}

		@Override
		public void transform(MatrixStack matrices) {
			matrices.scale(0.68f, 0.68f, 1);
			matrices.translate(0.05, 0.02, 0);
		}
	}

	private static class AutoGenDimensionalTrapdoorItem extends DimensionalTrapdoorItem implements ChildItem {
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

		@Override
		public Item getOriginalItem() {
			return originalItem;
		}

		@Override
		public void transform(MatrixStack matrices) {
			matrices.scale(0.55f, 0.55f, 0.6f);
			matrices.translate(0.05, -0.05, 0.41);
			matrices.multiply(Quaternion.fromEulerXyzDegrees(new Vec3f(90, 0, 0)));
		}
	}

	public interface ChildItem {
		Item getOriginalItem();

		default void transform(MatrixStack matrices) {
		}
	}
}
