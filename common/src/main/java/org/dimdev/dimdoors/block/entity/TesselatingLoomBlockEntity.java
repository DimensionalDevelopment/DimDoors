package org.dimdev.dimdoors.block.entity;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.recipe.ModRecipeTypes;
import org.dimdev.dimdoors.recipe.TesselatingRecipe;
import org.dimdev.dimdoors.screen.TessellatingContainer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TesselatingLoomBlockEntity extends BlockEntity implements MenuProvider, WorldlyContainer, RecipeCraftingHolder, RecipeInput, StackedContentsCompatible {
	public static final int DATA_WEAVING_TIME = 0;
	public static final int DATA_WEAVING_TIME_TOTAL = 1;
	public static final int NUM_DATA_VALUES = 2;

	private static final int[] OUTPUT_SLOTS = {9};
	private static final int[] INPUT_SLOTS = {0, 1, 2, 3, 4, 5, 6, 7, 8};

	private static final int[] SLOTS = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};

	private static final int DEFAULT_WEAVE_TIME = 200;
	private static final String INVENTORY_TAG = "Inventory";
	private static final String WEAVE_TIME_TAG = "WeaveTime";
	private static final String WEAVE_TIME_TOTAL_TAG = "WeaveTimeTotal";

	public int weaveTime;
	public int weaveTimeTotal;

	public final ContainerData dataAccess = new ContainerData() {
		public int get(int index) {
			return switch (index) {
				case DATA_WEAVING_TIME -> TesselatingLoomBlockEntity.this.weaveTime;
				case DATA_WEAVING_TIME_TOTAL -> TesselatingLoomBlockEntity.this.weaveTimeTotal;
				default -> 0;
			};
		}

		public void set(int index, int value) {
			switch (index) {
				case DATA_WEAVING_TIME -> TesselatingLoomBlockEntity.this.weaveTime = value;
				case DATA_WEAVING_TIME_TOTAL -> TesselatingLoomBlockEntity.this.weaveTimeTotal = value;
			}

		}

		public int getCount() {
			return NUM_DATA_VALUES;
		}
	};

	public NonNullList<ItemStack> inventory;
	public ItemStack output = ItemStack.EMPTY;
	private RecipeHolder<TesselatingRecipe> cachedRecipe;
	private final List<TessellatingContainer> openContainers = new ArrayList<>();

	private final Object2IntOpenHashMap<ResourceLocation> recipesUsed = new Object2IntOpenHashMap<>();

	private CraftingInput craftingInput;

	public TesselatingLoomBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntityTypes.TESSELATING_LOOM.get(), pos, state);
		this.inventory = NonNullList.withSize(9, ItemStack.EMPTY);
	}

	@Override
	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
		super.saveAdditional(nbt, registries);
		CompoundTag inventoryTag = new CompoundTag();
		ContainerHelper.saveAllItems(inventoryTag, inventory, registries);
		inventoryTag.put("Output", output.save(registries));
		nbt.put(INVENTORY_TAG, inventoryTag);
		nbt.putInt(WEAVE_TIME_TAG, this.weaveTime);
		nbt.putInt(WEAVE_TIME_TOTAL_TAG, this.weaveTimeTotal);
	}

	@Override
	protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
		super.loadAdditional(nbt, registries);
		CompoundTag inventoryTag = nbt.getCompound(INVENTORY_TAG);

		inventory.clear();
		ContainerHelper.loadAllItems(inventoryTag, this.inventory, registries);

		this.output = ItemStack.parseOptional(registries, inventoryTag.getCompound("Output"));
		this.weaveTime = nbt.getInt(WEAVE_TIME_TAG);
		this.weaveTimeTotal = nbt.getInt(WEAVE_TIME_TOTAL_TAG);
	}

	@Override
	public Component getDisplayName() {
		return Component.translatable(getBlockState().getBlock().getDescriptionId());
	}

	@Nullable
	@Override
	public TessellatingContainer createMenu(int syncId, Inventory inv, Player player) {
		return createMenu(syncId, inv);
	}

	public TessellatingContainer createMenu(int syncId, Inventory inv) {
		return new TessellatingContainer(syncId, this, inv, dataAccess);
	}

	@Override
	public int[] getSlotsForFace(Direction dir) {
		return SLOTS;
	}

	@Override
	public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction dir) {
		return slot != 9;
	}

	@Override
	public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
		return slot == 9;
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		return slot != 9;
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack stack : this.inventory) {
			if (!stack.isEmpty()) return false;
		}
		return output.isEmpty();
	}

	@Override
	public int getContainerSize() {
		return 10;
	}

	@Override
	public ItemStack getItem(int slot) {
		if (slot < 9) return this.inventory.get(slot);
		if (!output.isEmpty()) return output;
		return ItemStack.EMPTY;
	}

	public ItemStack getItem(int row, int column) {
		return this.getItem(row + column * 3);
	}

	@Override
	public int size() {
		return 3;
	}

	@Override
	public ItemStack removeItem(int slot, int amount) {
		if (slot == 9) {
			return output.split(amount);
		}
		return ContainerHelper.removeItem(this.inventory, slot, amount);
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		if (slot == 9) {
			ItemStack output = this.output;
			this.output = ItemStack.EMPTY;
			return output;
		}
		return ContainerHelper.takeItem(this.inventory, slot);
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		if (slot == 9) {
			output = stack;
			return;
		}
		inventory.set(slot, stack);
		setChanged();
	}

	@Override
	public void setChanged() {
		super.setChanged();
		for (TessellatingContainer c : openContainers) {
			c.slotsChanged(this);
		}
	}

	@Override
	public boolean stillValid(Player player) {
		return player.getOnPos().distSqr(this.worldPosition) <= 64.0D;
	}

	@Override
	public void fillStackedContents(StackedContents finder) {
		for (ItemStack stack : this.inventory) finder.accountStack(stack);
	}

	@Override
	public void setRecipeUsed(@Nullable RecipeHolder<?> recipe) {
		if(recipe.value() instanceof TesselatingRecipe) cachedRecipe = (RecipeHolder<TesselatingRecipe>) recipe;
	}

	@Override
	public RecipeHolder<?> getRecipeUsed() {
		return cachedRecipe;
	}

	@Override
	public void clearContent() {
		this.inventory.clear();
	}

	private Optional<RecipeHolder<TesselatingRecipe>> getCurrentRecipe() {
		// No need to find recipes if the inventory is empty. Cannot craft anything.
		if (this.level == null || this.isEmpty()) return Optional.empty();

		if (cachedRecipe != null) {
			Optional<RecipeHolder<TesselatingRecipe>> mapRecipe = getRecipe(cachedRecipe.id());
			if (mapRecipe.isPresent() && mapRecipe.get().value().matches(craftingInput, level)) {
				return mapRecipe;
			}
		}
		return getRecipe();
	}

	public Optional<RecipeHolder<TesselatingRecipe>> getRecipe(ResourceLocation location) {
		var manager = this.level.getRecipeManager();

//		TesselatingRecipe recipe = manager..byType(ModRecipeTypes.SHAPED_TESSELATING.get()).get(location);
//
//		if(recipe == null) {
//			recipe = manager.byType(ModRecipeTypes.SHAPELESS_TESSELATING.get()).get(location);
//		}

//		return recipe;
		return manager.byKey(location).filter(a -> a.value() instanceof TesselatingRecipe).map(a -> (RecipeHolder<TesselatingRecipe>) a);

	}

	public Optional<RecipeHolder<TesselatingRecipe>> getRecipe() {
		var manager = this.level.getRecipeManager();

		return manager.getRecipeFor(ModRecipeTypes.TESSELATING.get(), craftingInput, level);
	}

	private int getWeavingTotalTime() {
		return getCurrentRecipe().map(RecipeHolder::value).map(TesselatingRecipe::weavingTime).orElse(DEFAULT_WEAVE_TIME);
	}

	public void serverTick() {
		var recipe = getRecipe().orElse(null);

		if(cachedRecipe == null || cachedRecipe != recipe) {
			cachedRecipe = recipe;
			weaveTimeTotal = getWeavingTotalTime();
		}

		if(cachedRecipe != null) {
			tryWeave();
		} else {
			tryDecrementCookTime();
		}
	}

	public void tryDecrementCookTime() {
		if (weaveTime > 0) {
			weaveTime = Mth.clamp(weaveTime - 2, 0, weaveTimeTotal);
			setChanged();
		}
	}


	private void tryWeave() {
		var recipe = (TesselatingRecipe) cachedRecipe.value();
		var output = recipe.assemble(craftingInput, level.registryAccess());

		if(canAcceptOutput(output)) {
			weaveTime++;

			if(weaveTime >= weaveTimeTotal) {
				weaveTime = 0;
				cachedRecipe = null;

				takeInputs();
				insertOutput(output);
			}

			setChanged();
		} else {
			tryDecrementCookTime();
		}
	}

	private void insertOutput(ItemStack output) {
		if(output.isEmpty()) {
			return;
		}

		if(output.isStackable()) {
			for (int slot : OUTPUT_SLOTS) {
				var existing = getItem(slot);

				if(!existing.isEmpty() && ItemStack.isSameItemSameComponents(output, existing)) {
					var total = existing.getCount() + output.getCount();

					if(total <= existing.getMaxStackSize()) {
						output.setCount(0);
						existing.setCount(total);
					} else if(existing.getCount() < existing.getMaxStackSize()) {
						output.shrink(existing.getMaxStackSize() - existing.getCount());
						existing.setCount(existing.getMaxStackSize());
					}
				}

				if(output.isEmpty()) {
					return;
				}
			}
		}

		for (int slot : OUTPUT_SLOTS) {
			if(getItem(slot).isEmpty()) {
				setItem(slot, output.split(output.getCount()));
			}
		}

	}

	private void takeInputs() {
		for (var slot : INPUT_SLOTS) {
			var stack = getItem(slot);
			var item = stack.getItem();

			stack.shrink(1);

			if(stack.isEmpty()) {
				var newStack = item.getCraftingRemainingItem() != null ? new ItemStack(item.getCraftingRemainingItem()) : ItemStack.EMPTY;
				setItem(slot, newStack);
			}
		}

		setChanged();
	}

	private boolean canAcceptOutput(ItemStack output) {
		var remianingOutput = output.getCount();

		for (int slot : OUTPUT_SLOTS) {
			var existing = getItem(slot);

			if(existing.isEmpty()) return true;

			if(output.isStackable() && ItemStack.isSameItemSameComponents(existing, output)) {
				if(existing.getCount() + remianingOutput <= existing.getMaxStackSize()) {
					return true;
				} else if(existing.getCount() < existing.getMaxStackSize()) {
					remianingOutput -= existing.getMaxStackSize() - existing.getCount();
				}
			}

			if(remianingOutput == 0) {
				return true;
			}
		}

		return false;
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		return this.saveWithFullMetadata(registries);
	}

	@Override
	public void awardUsedRecipes(Player player, List<ItemStack> items) {
		List<RecipeHolder<?>> list = this.getRecipesToAwardAndPopExperience((ServerLevel) player.level(), player.position());
		player.awardRecipes(list);
		this.recipesUsed.clear();
	}

	public List<RecipeHolder<?>> getRecipesToAwardAndPopExperience(ServerLevel level, Vec3 popVec) {
		ArrayList<RecipeHolder<?>> list = Lists.newArrayList();
		for (Object2IntMap.Entry entry : this.recipesUsed.object2IntEntrySet()) {
			level.getRecipeManager().byKey((ResourceLocation)entry.getKey()).ifPresent(recipe -> {
				list.add(recipe);
//				createExperience(level, popVec, entry.getIntValue(), ((TesselatingRecipe) recipe.value()).getExperience());
			});
		}
		return list;
	}

	private static void createExperience(ServerLevel level, Vec3 popVec, int recipeIndex, float experience) {
		int i = Mth.floor((float)recipeIndex * experience);
		float f = Mth.frac((float)recipeIndex * experience);
		if (f != 0.0f && Math.random() < (double)f) {
			++i;
		}
		ExperienceOrb.award(level, popVec, i);
	}


//	public record WeavingResult()
}
