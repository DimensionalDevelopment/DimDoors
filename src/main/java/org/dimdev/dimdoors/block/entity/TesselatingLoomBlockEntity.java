package org.dimdev.dimdoors.block.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.*;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
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

import static net.minecraft.util.math.Direction.DOWN;

public class TesselatingLoomBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, SidedInventory, RecipeUnlocker, RecipeInputProvider {
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

	public final PropertyDelegate dataAccess = new PropertyDelegate() {
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

		public int size() {
			return NUM_DATA_VALUES;
		}
	};

	public CraftingInventory craftingInventory = new CraftingInventory(null, 3, 3) {
		@Override
		public void markDirty() {
			super.markDirty();
			TesselatingLoomBlockEntity.this.markDirty();
		}
	};

	public DefaultedList<ItemStack> inventory;
	public ItemStack output = ItemStack.EMPTY;
	private Recipe<?> lastRecipe;
	private final List<TesselatingScreenHandler> openContainers = new ArrayList<>();

	private final Map<Identifier, Integer> recipe2xp_map = Maps.newHashMap();

	public TesselatingLoomBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntityTypes.TESSELATING_LOOM, pos, state);
		this.inventory = DefaultedList.ofSize(9, ItemStack.EMPTY);
		((CraftingInventoryAccessor) craftingInventory).setInventory(inventory);
	}

	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		NbtCompound inventoryTag = new NbtCompound();
		Inventories.writeNbt(inventoryTag, inventory);
		inventoryTag.put("Output", output.writeNbt(new NbtCompound()));
		nbt.put(INVENTORY_TAG, inventoryTag);
		nbt.putInt(WEAVE_TIME_TAG, this.weaveTime);
		nbt.putInt(WEAVE_TIME_TOTAL_TAG, this.weaveTimeTotal);
	}

	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		NbtCompound inventoryTag = nbt.getCompound(INVENTORY_TAG);
		Inventories.readNbt(inventoryTag, this.inventory);
		this.output = ItemStack.fromNbt(inventoryTag.getCompound("Output"));
		this.weaveTime = nbt.getInt(WEAVE_TIME_TAG);
		this.weaveTimeTotal = nbt.getInt(WEAVE_TIME_TOTAL_TAG);
	}

	@Override
	public Text getDisplayName() {
		return Text.translatable(getCachedState().getBlock().getTranslationKey());
	}

	@Nullable
	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
		return new TesselatingScreenHandler(syncId, this, inv, dataAccess);
	}

	@Override
	public int[] getAvailableSlots(Direction dir) {
		return (dir == DOWN && (!output.isEmpty() || getCurrentRecipe().isPresent())) ? OUTPUT_SLOTS : INPUT_SLOTS;
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack, Direction dir) {
		return slot > 0 && getStack(slot).isEmpty();
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir) {
		return slot != 0 || !output.isEmpty() || getCurrentRecipe().isPresent();
	}

	@Override
	public boolean isValid(int slot, ItemStack stack) {
		return slot != 0 && slot <= size();
	}

	@Override
	public int size() {
		return 10;
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack stack : this.inventory) {
			if (!stack.isEmpty()) return false;
		}
		return output.isEmpty();
	}

	public boolean isInputEmpty() {
		for (ItemStack stack : this.inventory) {
			if (!stack.isEmpty()) return false;
		}
		return true;
	}


	@Override
	public ItemStack getStack(int slot) {
		if (slot > 0) return this.inventory.get(slot - 1);
		if (!output.isEmpty()) return output;
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		if (slot == 0) {
			return output.split(amount);
		}
		return Inventories.splitStack(this.inventory, slot - 1, amount);
	}

	@Override
	public ItemStack removeStack(int slot) {
		if (slot == 0) {
			ItemStack output = this.output;
			this.output = ItemStack.EMPTY;
			return output;
		}
		return Inventories.removeStack(this.inventory, slot - 1);
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		if (slot == 0) {
			output = stack;
			return;
		}
		inventory.set(slot - 1, stack);
		markDirty();
	}

	@Override
	public void markDirty() {
		super.markDirty();
		for (TesselatingScreenHandler c : openContainers) c.onContentChanged(this);
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return player.getBlockPos().getSquaredDistance(this.pos) <= 64.0D;
	}

	@Override
	public void provideRecipeInputs(RecipeMatcher finder) {
		for (ItemStack stack : this.inventory) finder.addInput(stack);
	}

	@Override
	public void setLastRecipe(Recipe<?> recipe) {
		lastRecipe = recipe;
	}

	@Override
	public Recipe<?> getLastRecipe() {
		return lastRecipe;
	}

	@Override
	public void clear() {
		this.inventory.clear();
	}

	private Optional<TesselatingRecipe> getCurrentRecipe() {
		// No need to find recipes if the inventory is empty. Cannot craft anything.
		if (this.world == null || this.isEmpty()) return Optional.empty();

		TesselatingRecipe lastRecipe = (TesselatingRecipe) getLastRecipe();
		RecipeManager manager = this.world.getRecipeManager();

		if (lastRecipe != null) {
			TesselatingRecipe mapRecipe = manager.getAllOfType(ModRecipeTypes.TESSELATING).get(lastRecipe);
			if (mapRecipe != null && mapRecipe.matches(craftingInventory, world)) {
				return Optional.of(lastRecipe);
			}
		}
		return manager.getFirstMatch(ModRecipeTypes.TESSELATING, craftingInventory, world);
	}


	private Optional<ItemStack> getResult() {
		Optional<ItemStack> maybe_result = getCurrentRecipe().map(recipe -> recipe.craft(craftingInventory));

		return Optional.of(maybe_result.orElse(ItemStack.EMPTY));
	}

	protected boolean canSmelt(ItemStack result, TesselatingRecipe recipe) {
		if (recipe.matches(this.craftingInventory, null)) {
			ItemStack outstack = output;
			if (outstack.isEmpty()) {
				return true;
			} else if (!outstack.isItemEqual(result)) {
				return false;
			} else {
				return (outstack.getCount() + result.getCount() <= outstack.getMaxCount());
			}
		} else {
			return false;
		}
	}

	private int getWeavingTime() {
		return getCurrentRecipe().map(a -> a.weavingTime).orElse(DEFAULT_WEAVE_TIME);
	}

	protected void smelt(ItemStack result, TesselatingRecipe recipe) {
		if(recipe.isEmpty()) return;

		if (!result.isEmpty() && this.canSmelt(result, recipe)) {
			ItemStack outstack = output.copy();

			if (outstack.isEmpty()) {
				output = result.copy();
			} else if (outstack.getItem() == result.getItem()) {
				output.increment(result.getCount());
			}

			if (!this.world.isClient()) {
				this.setLastRecipe(recipe);
			}

			DefaultedList<ItemStack> remaining = recipe.getRemainder(craftingInventory);
			for (int i = 0; i < 9; i++) {
				ItemStack current = inventory.get(i);
				ItemStack remainingStack = remaining.get(i);
				if (!current.isEmpty()) current.decrement(1);
				if (!remainingStack.isEmpty()) {
					if (current.isEmpty()) {
						inventory.set(i, remainingStack);
					} else if (ItemStack.areItemsEqual(current, remainingStack) && ItemStack.areEqual(current, remainingStack)) {
						current.increment(remainingStack.getCount());
					} else {
						ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), remainingStack);
					}
				}
			}
		}
	}

	public static void serverTick(World level, BlockPos blockpos, BlockState blockstate, TesselatingLoomBlockEntity tile) {
		boolean flag1 = false;

		if (!level.isClient()) {
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
					level.playSound(null, blockpos, ModSoundEvents.TESSELATING_WEAVE, SoundCategory.BLOCKS, 1.0f, 1.0f);
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
			tile.weaveTime = MathHelper.clamp(tile.weaveTime - 2, 0, tile.weaveTimeTotal);
		}


		if (flag1) {
			tile.markDirty();
		}
	}

	@Nullable
	@Override
	public Packet<ClientPlayPacketListener> toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}

	@Override
	public NbtCompound toInitialChunkDataNbt() {
		return this.createNbtWithIdentifyingData();
	}

	public void grantExperience(PlayerEntity player) {
		List<Recipe<?>> list = Lists.newArrayList();

		for (Map.Entry<Identifier, Integer> entry : this.recipe2xp_map.entrySet()) {
			player.getWorld().getRecipeManager().get(entry.getKey()).ifPresent((p_213993_3_) -> {
				list.add(p_213993_3_);
				spawnExpOrbs(player, entry.getValue(), ((TesselatingRecipe) p_213993_3_).experience);
			});
		}
		player.unlockRecipes(list);
		this.recipe2xp_map.clear();
	}

	private static void spawnExpOrbs(PlayerEntity player, int pCount, float experience)
	{
		if (experience == 0.0F) {
			pCount = 0;
		}
		else if (experience < 1.0F)
		{
			int i = MathHelper.floor((float) pCount * experience);
			if (i < MathHelper.ceil((float) pCount * experience)
					&& Math.random() < (double) ((float) pCount * experience - (float) i))
			{
				++i;
			}
			pCount = i;
		}

		while (pCount > 0)
		{
			int j = ExperienceOrbEntity.roundToOrbSize(pCount);
			pCount -= j;
			player.world.spawnEntity(new ExperienceOrbEntity(player.world, player.getX(), player.getY() + 0.5D, player.getZ() + 0.5D, j));
		}
	}

//	public record WeavingResult()
}
