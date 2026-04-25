package org.dimdev.dimdoors.screen;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.dimdev.dimdoors.block.entity.TesselatingLoomBlockEntity;
import org.dimdev.dimdoors.client.ModRecipeBookTypes;
import org.dimdev.dimdoors.recipe.TesselatingRecipe;

import java.util.List;

public class TessellatingContainer extends RecipeBookMenu<CraftingInput, TesselatingRecipe> {
    public static final int DATA_WEAVE_TIME = 0;
    public static final int DATA_WEAVE_TIME_TOAL = 1;

    protected final Inventory playerInventory;
    protected final Container recipeInv;
    protected final ContainerData data;

    public TessellatingContainer(int id, Inventory playerInventory) {
    this(id, new SimpleContainer(10), playerInventory, new SimpleContainerData(2));
    }

    public TessellatingContainer(int id, Container inventory, Inventory playerInventory, ContainerData propertyDelegate) {
    super(ModScreenHandlerTypes.TESSELATING_LOOM.get(), id);
    checkContainerSize(inventory, 10);
    checkContainerDataCount(propertyDelegate, 2);
    this.playerInventory = playerInventory;
    this.recipeInv = inventory;
    this.data = propertyDelegate;

    if (inventory instanceof TesselatingLoomBlockEntity loom) {
        loom.addOpenContainer(this);
    }

    this.addSlot(new ResultSlot(playerInventory.player, inventory, 9, 124, 35));

    for (int y = 0; y < 3; ++y) {
        for (int x = 0; x < 3; ++x) {
        this.addSlot(new Slot(this.recipeInv, x + y * 3, 30 + x * 18, 17 + y * 18));
        }
    }

    for (int y = 0; y < 3; ++y) {
        for (int x = 0; x < 9; ++x) {
        this.addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
        }
    }

    for (int i = 0; i < 9; ++i) {
        this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
    }

    this.addDataSlots(data);
    }

    @Override
    public void fillCraftSlotsStackedContents(StackedContents stackedContents) {
    for (int slot = 0; slot < 9; slot++) {
        stackedContents.accountSimpleStack(this.recipeInv.getItem(slot));
    }
    }

    @Override
    public void clearCraftingContent() {
    for (int slot = 0; slot < 9; slot++) {
        this.recipeInv.setItem(slot, ItemStack.EMPTY);
    }
    }

    @Override
    public boolean recipeMatches(RecipeHolder<TesselatingRecipe> recipeHolder) {
    return recipeHolder.value().matches(this.asCraftInput(), this.playerInventory.player.level());
    }

    @Override
    public int getResultSlotIndex() {
    return 0;
    }

    @Override
    public int getGridWidth() {
    return 3;
    }

    @Override
    public int getGridHeight() {
    return 3;
    }

    @Override
    public int getSize() {
    return 10;
    }

    @Override
    public RecipeBookType getRecipeBookType() {
    return ModRecipeBookTypes.TESSELLATING;
    }

    @Override
    public boolean shouldMoveToInventory(int index) {
    return index != this.getResultSlotIndex();
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
        } else if (index >= 10 && index < 46) {
        if (!this.moveItemStackTo(itemStack2, 1, 10, false)) {
            if (index < 37) {
            if (!this.moveItemStackTo(itemStack2, 37, 46, false)) {
                return ItemStack.EMPTY;
            }
            } else if (!this.moveItemStackTo(itemStack2, 10, 37, false)) {
            return ItemStack.EMPTY;
            }
        }
        } else if (!this.moveItemStackTo(itemStack2, 10, 46, false)) {
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
    return this.recipeInv.stillValid(player);
    }

    @Override
    public void removed(Player player) {
    super.removed(player);
    if (this.recipeInv instanceof TesselatingLoomBlockEntity loom) {
        loom.removeOpenContainer(this);
    }
    }

    private CraftingInput asCraftInput() {
    return CraftingInput.of(3, 3, List.of(
        this.recipeInv.getItem(0),
        this.recipeInv.getItem(1),
        this.recipeInv.getItem(2),
        this.recipeInv.getItem(3),
        this.recipeInv.getItem(4),
        this.recipeInv.getItem(5),
        this.recipeInv.getItem(6),
        this.recipeInv.getItem(7),
        this.recipeInv.getItem(8)
    ));
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
    private final Player player;
    private int removeCount;

    public ResultSlot(Player player, Container container, int slot, int x, int y) {
        super(container, slot, x, y);
        this.player = player;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }

    @Override
    public ItemStack remove(int amount) {
        if (this.hasItem()) {
        this.removeCount += Math.min(amount, this.getItem().getCount());
        }

        return super.remove(amount);
    }

    @Override
    protected void onQuickCraft(ItemStack stack, int amount) {
        this.removeCount += amount;
        this.checkTakeAchievements(stack);
    }

    @Override
    public void onTake(Player player, ItemStack stack) {
        this.checkTakeAchievements(stack);
        super.onTake(player, stack);
    }

    @Override
    protected void checkTakeAchievements(ItemStack stack) {
        if (this.removeCount > 0) {
        stack.onCraftedBy(this.player.level(), this.player, this.removeCount);
        }
        this.removeCount = 0;
    }
    }
}
