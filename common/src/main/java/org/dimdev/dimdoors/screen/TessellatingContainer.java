package org.dimdev.dimdoors.screen;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.block.entity.TesselatingLoomBlockEntity;

public class TessellatingContainer extends AbstractContainerMenu /*RecipeBookMenu<Container>*/ {


	public static final int DATA_WEAVE_TIME = 0;
	public static final int DATA_WEAVE_TIME_TOAL = 1;

	protected Inventory playerInventory;
	protected Container recipeInv;
	protected ContainerData data;

	public TessellatingContainer(int id, TesselatingLoomBlockEntity inventory, Inventory playerInventory, ContainerData propertyDelegate) {
		super(ModScreenHandlerTypes.TESSELATING_LOOM.get(), id);
		this.playerInventory = playerInventory;
		this.recipeInv = inventory;
		this.data = propertyDelegate;

		this.addSlot(new Slot(inventory, 9, 124, 35));

		for(int x = 0; x < 3; ++x) {
			for(int y = 0; y < 3; ++y) {
				this.addSlot(new Slot(this.recipeInv, x + y * 3, 30 + x * 18, 17 + y * 18));
			}
		}


		for(int i = 0; i < 9; ++i) {
			this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
		}

		for(int y = 0; y < 3; ++y) {
			for(int x = 0; x < 9; ++x) {
				this.addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
			}
		}

		this.addDataSlots(data);
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
			ItemStack itemStack = ItemStack.EMPTY;
			Slot slot = this.slots.get(index);
			if (slot.hasItem()) {
				ItemStack itemStack2 = slot.getItem();
				itemStack = itemStack2.copy();
				if (index == 0) {
					if (!this.moveItemStackTo(itemStack2, 10, 46, true)) {
						return ItemStack.EMPTY;
					}
					slot.onQuickCraft(itemStack2, itemStack);
				} else if (index >= 10 && index < 46 ? !this.moveItemStackTo(itemStack2, 1, 10, false) && (index < 37 ? !this.moveItemStackTo(itemStack2, 37, 46, false) : !this.moveItemStackTo(itemStack2, 10, 37, false)) : !this.moveItemStackTo(itemStack2, 10, 46, false)) {
					return ItemStack.EMPTY;
				}
				if (itemStack2.isEmpty()) {
					slot.setByPlayer(ItemStack.EMPTY);
				} else {
					slot.setChanged();
				}
				if (itemStack2.getCount() == itemStack.getCount()) {
					return ItemStack.EMPTY;
				}
				slot.onTake(player, itemStack2);
			}
			return itemStack;

	}

	@Override
	public boolean stillValid(Player player) {
		return recipeInv.stillValid(player);
	}

	public int getWeavProgress(int pixels) {
		int i = this.data.get(DATA_WEAVE_TIME);
		int j = this.data.get(DATA_WEAVE_TIME_TOAL);
		return j != 0 && i != 0 ? i * pixels / j : 0;
	}

	public boolean isWeaving() {
		return this.data.get(DATA_WEAVE_TIME) > 0;
	}

	public static class ResultSlot extends Slot {
//		private final Player player;

		public ResultSlot(/*Player player, */Container container, int slot, int x, int y) {
			super(container, slot, x, y);
		}

		@Override
		public boolean mayPlace(ItemStack stack) {
			return false;
		}

		@Override
		public ItemStack remove(int amount) {
			return super.remove(amount);
		}

		@Override
		public void onTake(Player player, ItemStack stack) {
			this.checkTakeAchievements(stack);
			super.onTake(player, stack);
		}

//		@Override
//		protected void checkTakeAchievements(ItemStack stack) {
//			stack.onCraftedBy(this.player.level(), this.player, this.removeCount);
//			if(this.player instanceof ServerPlayer serverPlayer && this.container instanceof TesselatingLoomBlockEntity container) container.awardUsedRecipesAndPopExperience(serverPlayer);
//		}
	}
}
