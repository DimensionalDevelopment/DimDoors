package org.dimdev.dimdoors.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeInputProvider;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.slot.Slot;

public class TesselatingScreenHandler extends AbstractRecipeScreenHandler<Inventory> {
	public static final int INPUT1_SLOT = 0;
	public static final int INPUT2_SLOT = 1;
	public static final int INPUT3_SLOT = 2;
	public static final int OUTPUT_SLOT = 3;

	public static final int DATA_WEAVE_TIME = 0;
	public static final int DATA_WEAVE_TIME_TOAL = 1;
	public static final int NUM_DATA_VALUES = 2;

	protected PlayerInventory playerInventory;
	protected Inventory recipeInv;
	protected PropertyDelegate data;

	public TesselatingScreenHandler(int id, PlayerInventory playerInventory) {
		this(id, new SimpleInventory(10), playerInventory, new ArrayPropertyDelegate(2));
	}
	public TesselatingScreenHandler(int id, Inventory inventory, PlayerInventory playerInventory, PropertyDelegate propertyDelegate) {
		super(ModScreenHandlerTypes.TESSELATING_LOOM, id);
		this.playerInventory = playerInventory;
		this.recipeInv = inventory;
		this.data = propertyDelegate;

		this.addSlot(new Slot(recipeInv, 0, 124, 35));

		for(int i = 0; i < 3; ++i) {
			for(int j = 0; j < 3; ++j) {
				this.addSlot(new Slot(this.recipeInv, j + i * 3 + 1, 30 + j * 18, 17 + i * 18));
			}
		}

		for(int i = 0; i < 3; ++i) {
			for(int j = 0; j < 9; ++j) {
				this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for(int i = 0; i < 9; ++i) {
			this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
		}

		this.addProperties(data);
	}

	@Override
	public void populateRecipeFinder(RecipeMatcher finder) {
		if(this.recipeInv instanceof RecipeInputProvider provider) {
			provider.provideRecipeInputs(finder);
		}
	}

	@Override
	public void clearCraftingSlots() {
		this.getSlot(1).setStack(ItemStack.EMPTY);
		this.getSlot(2).setStack(ItemStack.EMPTY);
		this.getSlot(3).setStack(ItemStack.EMPTY);
		this.getSlot(4).setStack(ItemStack.EMPTY);
		this.getSlot(5).setStack(ItemStack.EMPTY);
		this.getSlot(6).setStack(ItemStack.EMPTY);
		this.getSlot(7).setStack(ItemStack.EMPTY);
		this.getSlot(8).setStack(ItemStack.EMPTY);
		this.getSlot(9).setStack(ItemStack.EMPTY);
	}

	@Override
	public boolean matches(Recipe<? super Inventory> recipe) {
		return recipe.matches(recipeInv, playerInventory.player.world);
	}

	@Override
	public int getCraftingResultSlotIndex() {
		return 0;
	}

	@Override
	public int getCraftingWidth() {
		return 3;
	}

	@Override
	public int getCraftingHeight() {
		return 3;
	}

	@Override
	public int getCraftingSlotCount() {
		return 10;
	}

	@Override
	public RecipeBookCategory getCategory() {
		return RecipeBookCategory.CRAFTING;
	}

	@Override
	public boolean canInsertIntoSlot(int index) {
		return index != 0;
	}

	@Override
	public ItemStack transferSlot(PlayerEntity player, int index) {
		{
			ItemStack returnStack = ItemStack.EMPTY;
			final Slot slot = this.slots.get(index);
			if (slot != null && slot.hasStack()) {
				final ItemStack slotStack = slot.getStack();
				returnStack = slotStack.copy();

				final int containerSlots =
						this.slots.size() - player.getInventory().size();
				if (index < containerSlots) {
					if (!insertItem(slotStack, containerSlots, this.slots.size(), true)) {
						return ItemStack.EMPTY;
					}
				} else if (!insertItem(slotStack, 0, containerSlots, false)) {
					return ItemStack.EMPTY;
				}
				if (slotStack.getCount() == 0) {
					slot.setStack(ItemStack.EMPTY);
				} else {
					slot.markDirty();
				}
				if (slotStack.getCount() == returnStack.getCount()) {
					return ItemStack.EMPTY;
				}
				slot.onTakeItem(player, slotStack);
			}
			return returnStack;
		} // end transferStackInSlot()
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return recipeInv.canPlayerUse(player);
	}

	public int getBurnProgress(int pixels) {
		int i = this.data.get(DATA_WEAVE_TIME);
		int j = this.data.get(DATA_WEAVE_TIME_TOAL);
		return j != 0 && i != 0 ? i * pixels / j : 0;
	}

	public boolean isWeaving() {
		return this.data.get(DATA_WEAVE_TIME) > 0;
	}
}
