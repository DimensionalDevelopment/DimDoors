package org.dimdev.dimdoors.item.door;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.utils.Env;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.function.TriFunction;
import org.dimdev.dimdoors.block.door.DimensionalDoorBlock;
import org.dimdev.dimdoors.block.door.DimensionalTrapdoorBlock;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.client.UnderlaidChildItemRenderer;
import org.dimdev.dimdoors.item.ItemExtensions;
import org.dimdev.dimdoors.item.ModItems;
import org.dimdev.dimdoors.item.door.data.RiftDataList;
import org.dimdev.dimdoors.rift.targets.EscapeTarget;
import org.dimdev.dimdoors.rift.targets.PublicPocketTarget;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class DimensionalDoorItemRegistrar {
	public static final String PREFIX = "item_ag_dim_";

	private final Registrar<Item> registry;

	private final Map<Block, Block> blocksAlreadyNotifiedAbout = new HashMap<>();
	private final Map<Block, Triple<ResourceLocation, Item, Function<Block, BlockItem>>> toBeMapped = new HashMap<>();

	private final Map<Item, Function<BlockPlaceContext, InteractionResult>> placementFunctions = new HashMap<>();

	public DimensionalDoorItemRegistrar() {
		this.registry = RegistrarManager.get(DimensionalDoors.MOD_ID).get(Registries.ITEM);

		init();
		RegistrarManager.get(DimensionalDoors.MOD_ID).forRegistry(Registries.ITEM, registrar -> registrar.entrySet().forEach((entry) ->  handleEntry(entry.getKey().location(), entry.getValue())));
	}

	public boolean isRegistered(Item item) {
		return placementFunctions.containsKey(item);
	}

	public InteractionResult place(Item item, BlockPlaceContext context) {
		return placementFunctions.get(item).apply(context);
	}

	private void init() {
		new ArrayList<>(registry.entrySet())
				.forEach(entry -> handleEntry(entry.getKey().location(), entry.getValue()));
	}

	public void handleEntry(ResourceLocation identifier, Item original) {
		if (DimensionalDoors.getConfig().getDoorsConfig().isAllowed(identifier)) {
			if (original instanceof DoubleHighBlockItem doubleHighBlockItem) {
				Block block = doubleHighBlockItem.getBlock();
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

	private void handleEntry(ResourceLocation identifier, Item original, Block originalBlock, TriFunction<Block, Item.Properties, Item, ? extends BlockItem> constructor) {

		if (!(originalBlock instanceof DimensionalDoorBlock)
				&& !(originalBlock instanceof DimensionalTrapdoorBlock)
				&& (originalBlock instanceof DoorBlock || originalBlock instanceof TrapDoorBlock)) {
			Item.Properties settings = ItemExtensions.getSettings(original).arch$tab(ModItems.DIMENSIONAL_DOORS)/*.group(DoorData.PARENT_ITEMS.contains(original) || DoorData.PARENT_BLOCKS.contains(originalBlock) ? null : ModItems.DIMENSIONAL_DOORS)*/; //TODO: Redo with the new way Itemgroups work.

			Function<Block, BlockItem> dimItemConstructor = (dimBlock) -> constructor.apply(dimBlock, settings, original);

			if (!blocksAlreadyNotifiedAbout.containsKey(originalBlock)) {
				toBeMapped.put(originalBlock, new ImmutableTriple<>(identifier, original, dimItemConstructor));
				return;
			}

			register(identifier, original, blocksAlreadyNotifiedAbout.get(originalBlock), dimItemConstructor);
		}
	}

	public void notifyBlockMapped(Block original, Block dimBlock) {
		if (!toBeMapped.containsKey(original)) {
			blocksAlreadyNotifiedAbout.put(original, dimBlock);
			return;
		}
		Triple<ResourceLocation, Item, Function<Block, BlockItem>> triple = toBeMapped.get(original);
		register(triple.getLeft(), triple.getMiddle(), dimBlock, triple.getRight());
	}

	private void register(ResourceLocation identifier, Item original, Block block, Function<Block, BlockItem> dimItem) {
		ResourceLocation gennedId = DimensionalDoors.id(PREFIX + identifier.getNamespace() + "_" + identifier.getPath());
		BlockItem item = registry.register(gennedId, () -> dimItem.apply(block)).get();
		placementFunctions.put(original, item::place);
		if (Platform.getEnvironment() == Env.CLIENT) {
			registerItemRenderer(item);
		}
	}

	@Environment(EnvType.CLIENT)
	private void registerItemRenderer(BlockItem dimItem) {
//		BuiltinItemRendererRegistry.INSTANCE.register(dimItem, Renderer.RENDERER); TODO: Enable
	}

	// extract renderer to inner interface so it can be removed in server environment via annotation
	@Environment(EnvType.CLIENT)
	private interface Renderer {
		UnderlaidChildItemRenderer RENDERER = new UnderlaidChildItemRenderer(Items.ENDER_PEARL);
	}

	private static class AutoGenDimensionalDoorItem extends DimensionalDoorItem implements ChildItem {
		private final Item originalItem;

		public AutoGenDimensionalDoorItem(Block block, Properties settings, Item originalItem) {
			super(block, settings, null);
			this.originalItem = originalItem;
		}

		@Override
		public void setupRift(EntranceRiftBlockEntity entranceRift) {
			RiftDataList data = DoorRiftDataLoader.getInstance().getRiftData(originalItem);
			if (data != null) {
				RiftDataList.OptRiftData riftData = data.getRiftData(entranceRift);
				entranceRift.setDestination(riftData.getDestination());
				riftData.getProperties().ifPresent(entranceRift::setProperties);
			} else {
				entranceRift.setDestination(new PublicPocketTarget());
			}
		}

		@Override
		public MutableComponent getName(ItemStack stack) {
			return Component.translatable("dimdoors.autogen_item_prefix", I18n.get(originalItem.getDescriptionId()));
		}

		@Override
		public Item getOriginalItem() {
			return originalItem;
		}

		@Environment(EnvType.CLIENT)
		@Override
		public void transform(PoseStack matrices) {
			matrices.scale(0.769f, 0.769f, 1);
			matrices.translate(-0.06, 0.125, 0);
		}
	}

	private static class AutoGenDimensionalTrapdoorItem extends DimensionalTrapdoorItem implements ChildItem {
		private final Item originalItem;

		public AutoGenDimensionalTrapdoorItem(Block block, Properties settings, Item originalItem) {
			super(block, settings, null);
			this.originalItem = originalItem;
		}

		@Override
		protected void setupRift(EntranceRiftBlockEntity entranceRift) {
			RiftDataList data = DoorRiftDataLoader.getInstance().getRiftData(originalItem);
			if (data != null) {
				RiftDataList.OptRiftData riftData = data.getRiftData(entranceRift);
				entranceRift.setDestination(riftData.getDestination());
				riftData.getProperties().ifPresent(entranceRift::setProperties);
			} else {
				entranceRift.setDestination(new EscapeTarget(true));
			}
		}

		@Override
		public MutableComponent getName(ItemStack stack) {
			return Component.translatable("dimdoors.autogen_item_prefix", I18n.get(originalItem.getDescriptionId()));
		}

		@Override
		public Item getOriginalItem() {
			return originalItem;
		}

		@Environment(EnvType.CLIENT)
		@Override
		public void transform(PoseStack matrices) {
			matrices.scale(0.55f, 0.55f, 0.6f);
			matrices.translate(0.05, -0.05, 0.41);
			matrices.mulPose(new Quaternionf().rotateXYZ(90, 0, 0));
		}
	}

	public interface ChildItem {
		Item getOriginalItem();

		default void transform(PoseStack matrices) {
		}
	}
}