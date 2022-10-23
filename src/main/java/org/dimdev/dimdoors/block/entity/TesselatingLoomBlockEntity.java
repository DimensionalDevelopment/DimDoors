package org.dimdev.dimdoors.block.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.dimdev.dimdoors.recipe.ModRecipeTypes;
import org.dimdev.dimdoors.recipe.TesselatingRecipe;
import org.dimdev.dimdoors.screen.TesselatingScreenHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class TesselatingLoomBlockEntity extends BlockEntity implements NamedScreenHandlerFactory {
	protected static final int INPUT_SLOT_1 = 0;
	protected static final int INPUT_SLOT_2 = 1;
	protected static final int INPUT_SLOT_3 = 2;
	protected static final int OUTPUT_SLOT = 3;

	public static final int DATA_WEAVING_TIME = 0;
	public static final int DATA_WEAVING_TIME_TOTAL = 1;
	public static final int NUM_DATA_VALUES = 2;

	private static final int[] TOP_SLOTS = new int[]{0};
	private static final int[] BOTTOM_SLOTS = new int[]{2, 1};
	private static final int[] SIDE_SLOTS = new int[]{1};
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

	public SimpleInventory inventory = new SimpleInventory(4) {
		@Override
		public void markDirty() {
			super.markDirty();
			TesselatingLoomBlockEntity.this.markDirty();
		}
	};

	InventoryStorage inventoryWrapper = InventoryStorage.of(inventory, null);

	private final Map<Identifier, Integer> recipe2xp_map = Maps.newHashMap();

	protected TesselatingRecipe cachedRecipe;
	protected ItemStack failedMatch1 = ItemStack.EMPTY;
	protected ItemStack failedMatch2 = ItemStack.EMPTY;
	protected ItemStack failedMatch3 = ItemStack.EMPTY;

	public TesselatingLoomBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntityTypes.TESSELATING_LOOM, pos, state);
	}

	protected boolean isInput(final ItemStack stack) {
		return stack.isEmpty();
	}

	protected boolean isOutput(final ItemStack stack) {
		final Optional<ItemStack> result = getResult(
				inventory.getStack(INPUT_SLOT_1),
				inventory.getStack(INPUT_SLOT_2),
				inventory.getStack(INPUT_SLOT_3));
		return result.isPresent() && ItemStack.areEqual(result.get(), stack);
	}

	private Optional<TesselatingRecipe> getRecipe(final ItemStack input1, final ItemStack input2, final ItemStack input3) {
		if (ItemStack.areEqual(input1, failedMatch1) && ItemStack.areEqual(input2, failedMatch2) && ItemStack.areEqual(input3, failedMatch3)) {
			return Optional.empty();
		}
		// Due to vanilla's code we need to pass an IInventory into
		// RecipeManager#getRecipe so we make one here.
		return getRecipe(new SimpleInventory(input1, input2, input3));
	}

	private Optional<TesselatingRecipe> getRecipe(final Inventory inv) {
		if (cachedRecipe != null && cachedRecipe.matches(inv, world)) {
			return Optional.of(cachedRecipe);
		} else {
			TesselatingRecipe rec = world.getRecipeManager().getFirstMatch(ModRecipeTypes.TESSELATING, inv, world).orElse(null);
			if (rec == null) {
				failedMatch1 = inv.getStack(INPUT_SLOT_1);
				failedMatch2 = inv.getStack(INPUT_SLOT_2);
				failedMatch3 = inv.getStack(INPUT_SLOT_3);
			} else {
				failedMatch1 = ItemStack.EMPTY;
				failedMatch2 = ItemStack.EMPTY;
				failedMatch3 = ItemStack.EMPTY;
			}
			cachedRecipe = rec;
			return Optional.ofNullable(rec);
		}
	}

	private Optional<ItemStack> getResult(final ItemStack input1, final ItemStack input2, final ItemStack input3) {
		SimpleInventory inv0 = new SimpleInventory(input1, input2, input3);
		Optional<ItemStack> maybe_result = getRecipe(input1, input2, input3).map(recipe -> recipe.craft(inv0));

		return Optional.of(maybe_result.orElse(ItemStack.EMPTY));
	}

	public void setRecipeUsed(@Nullable Recipe<?> recipe) {
		if (recipe != null) {
			this.recipe2xp_map.compute(recipe.getId(), (p_214004_0_, p_214004_1_) -> 1 + (p_214004_1_ == null ? 0 : p_214004_1_));
		}
	}

	protected boolean canSmelt(ItemStack result, TesselatingRecipe recipe) {
		if (recipe.matches(this.inventory, null)) {
			ItemStack outstack = inventory.getStack(OUTPUT_SLOT);
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

	private int getWeavingTime(final ItemStack input1, final ItemStack input2, final ItemStack input3) {
		return getRecipe(input1, input2, input3).map(TesselatingRecipe::weavingTime).orElse(DEFAULT_WEAVE_TIME);
	}

	protected void smelt(ItemStack result, TesselatingRecipe recipe) {
		if(recipe.isEmpty()) return;

		if (!result.isEmpty() && this.canSmelt(result, recipe)) {
			final ItemStack input1 = inventory.getStack(INPUT_SLOT_1).copy();
			final ItemStack input2 = inventory.getStack(INPUT_SLOT_2).copy();
			final ItemStack input3 = inventory.getStack(INPUT_SLOT_3).copy();
			ItemStack outstack = inventory.getStack(OUTPUT_SLOT).copy();

			if (outstack.isEmpty()) {
				inventory.setStack(OUTPUT_SLOT, result.copy());
			} else if (outstack.getItem() == result.getItem()) {
				outstack.increment(result.getCount());
				inventory.setStack(OUTPUT_SLOT, outstack);
			}
			if (!this.world.isClient()) {
				this.setRecipeUsed(getRecipe(input1, input2, input3).orElse(null));
			}

			DefaultedList<TesselatingRecipe.Input> ingredients = recipe.input();

			Function<ItemStack, Integer> function = itemStack -> {
				for (TesselatingRecipe.Input input : ingredients) {
					if (input.test(itemStack)) {
						return input.getRight();
					}
				}

				return -1;
			};

			int count = function.apply(input1);
			if(count != -1) {
				input1.decrement(count);
				inventory.setStack(INPUT_SLOT_1, input1);
			}

			count = function.apply(input2);
			if(count != -1) {
				input2.decrement(count);
				inventory.setStack(INPUT_SLOT_2, input2);
			}

			count = function.apply(input3);
			if(count != -1) {
				input3.decrement(count);
				inventory.setStack(INPUT_SLOT_3, input3);
			}
		}
	}

	public static void serverTick(World level, BlockPos blockpos, BlockState blockstate, TesselatingLoomBlockEntity tile) {
		boolean flag1 = false;

		if (!level.isClient()) {
			ItemStack input1 = tile.inventory.getStack(INPUT_SLOT_1).copy();
			ItemStack input2 = tile.inventory.getStack(INPUT_SLOT_2).copy();
			ItemStack catalyst = tile.inventory.getStack(INPUT_SLOT_3).copy();
			ItemStack result = tile.getResult(input1, input2, catalyst).orElse(ItemStack.EMPTY);

			Optional<TesselatingRecipe> recipe = tile.getRecipe(tile.inventory);

			if (recipe.isPresent() && (!input1.isEmpty() || !input2.isEmpty() || !catalyst.isEmpty())) {
				if (tile.canSmelt(result, recipe.get())) {
					if (tile.weaveTime <= 0) {
						tile.weaveTimeTotal = tile.getWeavingTime(input1, input2, catalyst);
						tile.weaveTime = 0;
					}
				}
				++tile.weaveTime;
				if (tile.weaveTime >= tile.weaveTimeTotal) {
					tile.smelt(result, recipe.get());
					tile.weaveTime = 0;

					tile.weaveTimeTotal = !tile.inventory.getStack(INPUT_SLOT_1).isEmpty() || !tile.inventory.getStack(INPUT_SLOT_2).isEmpty() || !tile.inventory.getStack(INPUT_SLOT_3).isEmpty() ? tile.getWeavingTime(tile.inventory.getStack(INPUT_SLOT_1), tile.inventory.getStack(INPUT_SLOT_2), tile.inventory.getStack(INPUT_SLOT_3)) : 0;

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

	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		this.inventory.readNbtList(nbt.getList(INVENTORY_TAG, NbtElement.COMPOUND_TYPE));
		this.weaveTime = nbt.getInt(WEAVE_TIME_TAG);
		this.weaveTimeTotal = nbt.getInt(WEAVE_TIME_TOTAL_TAG);

		int i = nbt.getShort("RecipesUsedSize");
		for(int jj = 0; jj < i; ++jj) {
			Identifier resourcelocation = new Identifier(nbt.getString("RecipeLocation" + jj));
			int kk = nbt.getInt("RecipeAmount" + jj);
			this.recipe2xp_map.put(resourcelocation, kk);
		}
	}

	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		nbt.put(INVENTORY_TAG, this.inventory.toNbtList());
		nbt.putInt(WEAVE_TIME_TAG, this.weaveTime);
		nbt.putInt(WEAVE_TIME_TOTAL_TAG, this.weaveTimeTotal);

		nbt.putShort("RecipesUsedSize", (short)this.recipe2xp_map.size());
		int i = 0;
		for(Map.Entry<Identifier, Integer> entry : this.recipe2xp_map.entrySet())
		{
			nbt.putString("RecipeLocation" + i, entry.getKey().toString());
			nbt.putInt("RecipeAmount" + i, entry.getValue());
			++i;
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
				spawnExpOrbs(player, entry.getValue(), ((TesselatingRecipe) p_213993_3_).experience());
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

	@Override
	public Text getDisplayName() {
		return Text.translatable(getCachedState().getBlock().getTranslationKey());
	}

	@Nullable
	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
		return new TesselatingScreenHandler(syncId, inventory, inv, dataAccess);
	}

//	public record WeavingResult()
}
