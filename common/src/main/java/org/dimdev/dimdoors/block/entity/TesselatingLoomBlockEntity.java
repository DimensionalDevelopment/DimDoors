package org.dimdev.dimdoors.block.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimdoors.mixin.accessor.CraftingInventoryAccessor;
import org.dimdev.dimdoors.recipe.ModRecipeTypes;
import org.dimdev.dimdoors.recipe.TesselatingRecipe;
import org.dimdev.dimdoors.screen.TesselatingScreenHandler;
import org.dimdev.dimdoors.sound.ModSoundEvents;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TesselatingLoomBlockEntity extends BlockEntity implements MenuProvider, WorldlyContainer, RecipeHolder, StackedContentsCompatible {
	public static final int DATA_WEAVING_TIME = 0;
	public static final int DATA_WEAVING_TIME_TOTAL = 1;
	public static final int NUM_DATA_VALUES = 2;

	private static final int[] OUTPUT_SLOTS = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
	private static final int[] INPUT_SLOTS = {1, 2, 3, 4, 5, 6, 7, 8, 9};

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

	public CraftingContainer craftingInventory = new CraftingContainer(null, 3, 3) {
		@Override
		public void setChanged() {
			super.setChanged();
			TesselatingLoomBlockEntity.this.setChanged();
		}
	};

	public NonNullList<ItemStack> inventory;
	public ItemStack output = ItemStack.EMPTY;
	private Recipe<?> lastRecipe;
	private final List<TesselatingScreenHandler> openContainers = new ArrayList<>();

	private final Map<ResourceLocation, Integer> recipe2xp_map = Maps.newHashMap();

	public TesselatingLoomBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntityTypes.TESSELATING_LOOM, pos, state);
		this.inventory = NonNullList.withSize(9, ItemStack.EMPTY);
		((CraftingInventoryAccessor) craftingInventory).setInventory(inventory);
	}

	@Override
	protected void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		CompoundTag inventoryTag = new CompoundTag();
		ContainerHelper.saveAllItems(inventoryTag, inventory);
		inventoryTag.put("Output", output.save(new CompoundTag()));
		nbt.put(INVENTORY_TAG, inventoryTag);
		nbt.putInt(WEAVE_TIME_TAG, this.weaveTime);
		nbt.putInt(WEAVE_TIME_TOTAL_TAG, this.weaveTimeTotal);
	}

	public void load(CompoundTag nbt) {
		super.load(nbt);
		CompoundTag inventoryTag = nbt.getCompound(INVENTORY_TAG);
		ContainerHelper.saveAllItems(inventoryTag, this.inventory);
		this.output = ItemStack.of(inventoryTag.getCompound("Output"));
		this.weaveTime = nbt.getInt(WEAVE_TIME_TAG);
		this.weaveTimeTotal = nbt.getInt(WEAVE_TIME_TOTAL_TAG);
	}

	@Override
	public Component getDisplayName() {
		return Component.translatable(getBlockState().getBlock().getDescriptionId());
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
		return new TesselatingScreenHandler(syncId, this, inv, dataAccess);
	}

	@Override
	public int[] getSlotsForFace(Direction dir) {
		return (dir == Direction.DOWN && (!output.isEmpty() || getCurrentRecipe().isPresent())) ? OUTPUT_SLOTS : INPUT_SLOTS;
	}

	@Override
	public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction dir) {
		return slot > 0 && getItem(slot).isEmpty();
	}

	@Override
	public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
		return slot != 0 || !output.isEmpty() || getCurrentRecipe().isPresent();
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		return slot != 0 && slot <= getContainerSize();
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

	public boolean isInputEmpty() {
		for (ItemStack stack : this.inventory) {
			if (!stack.isEmpty()) return false;
		}
		return true;
	}


	@Override
	public ItemStack getItem(int slot) {
		if (slot > 0) return this.inventory.get(slot - 1);
		if (!output.isEmpty()) return output;
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeItem(int slot, int amount) {
		if (slot == 0) {
			return output.split(amount);
		}
		return ContainerHelper.removeItem(this.inventory, slot - 1, amount);
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		if (slot == 0) {
			ItemStack output = this.output;
			this.output = ItemStack.EMPTY;
			return output;
		}
		return ContainerHelper.takeItem(this.inventory, slot - 1);
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		if (slot == 0) {
			output = stack;
			return;
		}
		inventory.set(slot - 1, stack);
		setChanged();
	}

	@Override
	public void setChanged() {
		super.setChanged();
		for (TesselatingScreenHandler c : openContainers) c.slotsChanged(this);
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
	public void setRecipeUsed(Recipe<?> recipe) {
		lastRecipe = recipe;
	}

	@Override
	public Recipe<?> getRecipeUsed() {
		return lastRecipe;
	}

	@Override
	public void clearContent() {
		this.inventory.clear();
	}

	private Optional<TesselatingRecipe> getCurrentRecipe() {
		// No need to find recipes if the inventory is empty. Cannot craft anything.
		if (this.level == null || this.isEmpty()) return Optional.empty();

		TesselatingRecipe lastRecipe = (TesselatingRecipe) getRecipeUsed();
		RecipeManager manager = this.level.getRecipeManager();

		if (lastRecipe != null) {
			TesselatingRecipe mapRecipe = manager.byType(ModRecipeTypes.TESSELATING.get()).get(lastRecipe);
			if (mapRecipe != null && mapRecipe.matches(craftingInventory, level)) {
				return Optional.of(lastRecipe);
			}
		}
		return manager.getRecipeFor(ModRecipeTypes.TESSELATING.get(), craftingInventory, level);
	}


	private Optional<ItemStack> getResult() {
		Optional<ItemStack> maybe_result = getCurrentRecipe().map(recipe -> recipe.assemble(craftingInventory, null));

		return Optional.of(maybe_result.orElse(ItemStack.EMPTY));
	}

	protected boolean canSmelt(ItemStack result, TesselatingRecipe recipe) {
		if (recipe.matches(this.craftingInventory, null)) {
			ItemStack outstack = output;
			if (outstack.isEmpty()) {
				return true;
			} else if (!outstack.sameItem(result)) {
				return false;
			} else {
				return (outstack.getCount() + result.getCount() <= outstack.getMaxStackSize());
			}
		} else {
			return false;
		}
	}

	private int getWeavingTime() {
		return getCurrentRecipe().map(a -> a.weavingTime).orElse(DEFAULT_WEAVE_TIME);
	}

	protected void smelt(ItemStack result, TesselatingRecipe recipe) {
		if(recipe.isIncomplete()) return;

		if (!result.isEmpty() && this.canSmelt(result, recipe)) {
			ItemStack outstack = output.copy();

			if (outstack.isEmpty()) {
				output = result.copy();
			} else if (outstack.getItem() == result.getItem()) {
				output.grow(result.getCount());
			}

			if (!this.level.isClientSide()) {
				this.setRecipeUsed(recipe);
			}

			NonNullList<ItemStack> remaining = recipe.getRemainingItems(craftingInventory);
			NonNullList<ItemStack> drops = NonNullList.create();

			for (int i = 0; i < 9; i++) {
				ItemStack current = inventory.get(i);
				ItemStack remainingStack = remaining.get(i);
				if (!current.isEmpty()) current.shrink(1);
				if (!remainingStack.isEmpty()) {
					if (current.isEmpty()) {
						inventory.set(i, remainingStack);
					} else if (ItemStack.isSame(current, remainingStack) && ItemStack.matches(current, remainingStack)) {
						current.grow(remainingStack.getCount());
					} else {
						drops.add(remainingStack);
					}
				}
			}

			Containers.dropContents(level, worldPosition, drops);
		}
	}

	public static void serverTick(Level level, BlockPos blockpos, BlockState blockstate, TesselatingLoomBlockEntity tile) {
		boolean flag1 = false;

		if (!level.isClientSide()) {
			ItemStack result = tile.getResult().orElse(ItemStack.EMPTY);

			Optional<TesselatingRecipe> recipe = tile.getCurrentRecipe();

			if (recipe.isPresent() && (!tile.isInputEmpty())) {
				if (tile.canSmelt(result, recipe.get())) {
					if (tile.weaveTime <= 0) {
						tile.weaveTimeTotal = tile.getWeavingTime();
						tile.weaveTime = 0;
					}
				}
				++tile.weaveTime;

				if(tile.weaveTime % 60 == 0) {
					level.playSound(null, blockpos, ModSoundEvents.TESSELATING_WEAVE.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
				}

				if (tile.weaveTime >= tile.weaveTimeTotal) {
					tile.smelt(result, recipe.get());
					tile.weaveTime = 0;

					tile.weaveTimeTotal = !tile.isInputEmpty() ? tile.getWeavingTime() : 0;

					flag1 = true;
				}
			}
			else {
				tile.weaveTime = 0;
			}
		} else if (tile.weaveTime > 0) {
			tile.weaveTime = Mth.clamp(tile.weaveTime - 2, 0, tile.weaveTimeTotal);
		}


		if (flag1) {
			tile.setChanged();
		}
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag() {
		return this.saveMetadata();
	}

	public void grantExperience(Player player) {
		List<Recipe<?>> list = Lists.newArrayList();

		for (Map.Entry<ResourceLocation, Integer> entry : this.recipe2xp_map.entrySet()) {
			player.getLevel().getRecipeManager().byKey(entry.getKey()).ifPresent((p_213993_3_) -> {
				list.add(p_213993_3_);
				spawnExpOrbs(player, entry.getValue(), ((TesselatingRecipe) p_213993_3_).experience);
			});
		}
		player.awardRecipes(list);
		this.recipe2xp_map.clear();
	}

	private static void spawnExpOrbs(Player player, int pCount, float experience)
	{
		if (experience == 0.0F) {
			pCount = 0;
		}
		else if (experience < 1.0F)
		{
			int i = Mth.floor((float) pCount * experience);
			if (i < Mth.ceil((float) pCount * experience)
					&& Math.random() < (double) ((float) pCount * experience - (float) i))
			{
				++i;
			}
			pCount = i;
		}

		while (pCount > 0)
		{
			int j = ExperienceOrb.getExperienceValue(pCount);
			pCount -= j;
			player.level.addFreshEntity(new ExperienceOrb(player.level, player.getX(), player.getY() + 0.5D, player.getZ() + 0.5D, j));
		}
	}

//	public record WeavingResult()
}
