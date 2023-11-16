package org.dimdev.dimdoors.compat.rei;

import dev.architectury.platform.Platform;
import it.unimi.dsi.fastutil.ints.IntIntImmutablePair;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.SimpleGridMenuDisplay;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.InputIngredient;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.transfer.info.MenuInfo;
import me.shedaniel.rei.api.common.transfer.info.MenuSerializationContext;
import me.shedaniel.rei.api.common.transfer.info.simple.SimpleGridMenuInfo;
import me.shedaniel.rei.api.common.util.CollectionUtils;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.plugin.common.BuiltinPlugin;
import me.shedaniel.rei.plugin.common.displays.crafting.*;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.dimdev.dimdoors.recipe.ShapedTesselatingRecipe;
import org.dimdev.dimdoors.recipe.TesselatingShapelessRecipe;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

@SuppressWarnings("removal")
public abstract class DefaultTesselatingDisplay<C extends Recipe<?>> extends BasicDisplay implements SimpleGridMenuDisplay {
    protected Optional<C> recipe;
    private final int weavingTime;

    public DefaultTesselatingDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs, Optional<C> recipe, int weavingTime) {
        this(inputs, outputs, recipe.map(Recipe::getId), recipe, weavingTime);
    }

    public DefaultTesselatingDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs, Optional<ResourceLocation> location, Optional<C> recipe, int weavingTime) {
        super(inputs, outputs, location);
        this.recipe = recipe;
        this.weavingTime = weavingTime;
    }

//    private static final List<CraftingRecipeSizeProvider<?>> SIZE_PROVIDER = new ArrayList<>();

//    static {
//        try {
//            Class.forName("me.shedaniel.rei.plugin.common.displays.crafting.%s.DefaultCraftingDisplayImpl".formatted(Platform.isForge() ? "forge" : "fabric"))
//                    .getDeclaredMethod("registerPlatformSizeProvider")
//                    .invoke(null);
//        } catch (IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
//            throw new RuntimeException(e);
//        }
//    }

//    /**
//     * Registers a size provider for crafting recipes.
//     * This is not reloadable, please statically register your provider, and
//     * do not repeatedly register it.
//     *
//     * @param sizeProvider the provider to register
//     * @param <R>          the recipe type
//     */
//    public static <R extends Recipe<?>> void registerSizeProvider(CraftingRecipeSizeProvider<R> sizeProvider) {
//        SIZE_PROVIDER.add(0, sizeProvider);
//    }

    @Nullable
    public static DefaultTesselatingDisplay<?> of(Recipe<?> recipe) {
        if (recipe instanceof TesselatingShapelessRecipe) {
            return new DefaultTesselatingShapelessDisplay((TesselatingShapelessRecipe) recipe);
        } else if (recipe instanceof ShapedTesselatingRecipe) {
            return new DefaultTesselatingShapedDisplay((ShapedTesselatingRecipe) recipe);
        } /*else if (!recipe.isSpecial()) {
            NonNullList<Ingredient> ingredients = recipe.getIngredients();
            for (CraftingRecipeSizeProvider<?> pair : SIZE_PROVIDER) {
                CraftingRecipeSizeProvider.Size size = ((CraftingRecipeSizeProvider<Recipe<?>>) pair).getSize(recipe);

                if (size != null) {
                    return new DefaultCustomShapedDisplay(recipe, EntryIngredients.ofIngredients(recipe.getIngredients()),
                            Collections.singletonList(EntryIngredients.of(recipe.getResultItem(BasicDisplay.registryAccess()))),
                            size.getWidth(), size.getHeight());
                }
            }

            return new DefaultCustomDisplay(recipe, EntryIngredients.ofIngredients(recipe.getIngredients()),
                    Collections.singletonList(EntryIngredients.of(recipe.getResultItem(BasicDisplay.registryAccess()))));
        }*/

        return null;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return TesselatingReiCompatClient.TESSELATING;
    }

    public Optional<C> getOptionalRecipe() {
        return recipe;
    }

    @Override
    public Optional<ResourceLocation> getDisplayLocation() {
        return getOptionalRecipe().map(Recipe::getId);
    }

    public <T extends AbstractContainerMenu> List<List<ItemStack>> getOrganisedInputEntries(SimpleGridMenuInfo<T, DefaultTesselatingDisplay<?>> menuInfo, T container) {
        return CollectionUtils.map(getOrganisedInputEntries(menuInfo.getCraftingWidth(container), menuInfo.getCraftingHeight(container)), ingredient ->
                CollectionUtils.<EntryStack<?>, ItemStack>filterAndMap(ingredient, stack -> stack.getType() == VanillaEntryTypes.ITEM,
                        EntryStack::castValue));
    }

    public <T extends AbstractContainerMenu> List<EntryIngredient> getOrganisedInputEntries(int menuWidth, int menuHeight) {
        List<EntryIngredient> list = new ArrayList<>(menuWidth * menuHeight);
        for (int i = 0; i < menuWidth * menuHeight; i++) {
            list.add(EntryIngredient.empty());
        }
        for (int i = 0; i < getInputEntries().size(); i++) {
            list.set(getSlotWithSize(this, i, menuWidth), getInputEntries().get(i));
        }
        return list;
    }

    public boolean isShapeless() {
        return false;
    }

    public static int getSlotWithSize(DefaultTesselatingDisplay<?> display, int index, int craftingGridWidth) {
        return getSlotWithSize(display.getInputWidth(craftingGridWidth, 3), index, craftingGridWidth);
    }

    public static int getSlotWithSize(int recipeWidth, int index, int craftingGridWidth) {
        int x = index % recipeWidth;
        int y = (index - x) / recipeWidth;
        return craftingGridWidth * y + x;
    }

//    public static BasicDisplay.Serializer<DefaultTesselatingDisplay<?>> serializer() {
//        return BasicDisplay.Serializer.<DefaultTesselatingDisplay<?>>of((input, output, location, tag) -> {
//            if (tag.contains("REIRecipeType")) {
//                String type = tag.getString("REIRecipeType");
//                return switch (type) {
//                    case "Shapeless" -> DefaultCustomShapelessDisplay.simple(input, output, location);
//                    case "Shaped" -> DefaultCustomShapedDisplay.simple(input, output, tag.getInt("RecipeWidth"), tag.getInt("RecipeHeight"), location);
//                    default -> throw new IllegalArgumentException("Unknown recipe type: " + type);
//                };
//            } else {
//                return DefaultCustomDisplay.simple(input, output, location);
//            }
//        }, (display, tag) -> {
//            tag.putString("REIRecipeType", display.isShapeless() ? "Shapeless" : "Shaped");
//            if (!display.isShapeless()) {
//                tag.putInt("RecipeWidth", display.getInputWidth(3, 3));
//                tag.putInt("RecipeHeight", display.getInputHeight(3, 3));
//            }
//        });
//    }
//
//    @Override
    public List<InputIngredient<EntryStack<?>>> getInputIngredients(MenuSerializationContext<?, ?, ?> context, MenuInfo<?, ?> info, boolean fill) {
        int craftingWidth = 3, craftingHeight = 3;

        if (info instanceof SimpleGridMenuInfo && fill) {
            craftingWidth = ((SimpleGridMenuInfo<AbstractContainerMenu, ?>) info).getCraftingWidth(context.getMenu());
            craftingHeight = ((SimpleGridMenuInfo<AbstractContainerMenu, ?>) info).getCraftingHeight(context.getMenu());
        }

        return getInputIngredients(craftingWidth, craftingHeight);
    }

    @Override
    public List<InputIngredient<EntryStack<?>>> getInputIngredients(@Nullable AbstractContainerMenu menu, @Nullable Player player) {
        return getInputIngredients(3, 3);
    }

    public List<InputIngredient<EntryStack<?>>> getInputIngredients(int craftingWidth, int craftingHeight) {
        int inputWidth = getInputWidth(craftingWidth, craftingHeight);
        int inputHeight = getInputHeight(craftingWidth, craftingHeight);

        Map<IntIntPair, InputIngredient<EntryStack<?>>> grid = new HashMap<>();

        List<EntryIngredient> inputEntries = getInputEntries();
        for (int i = 0; i < inputEntries.size(); i++) {
            EntryIngredient stacks = inputEntries.get(i);
            if (stacks.isEmpty()) {
                continue;
            }
            int index = getSlotWithSize(inputWidth, i, craftingWidth);
            int x = i % inputWidth;
            int y = i / inputWidth;
            grid.put(new IntIntImmutablePair(x, y), InputIngredient.of(index, 3 * y + x, stacks));
        }

        List<InputIngredient<EntryStack<?>>> list = new ArrayList<>(craftingWidth * craftingHeight);
        for (int i = 0, n = craftingWidth * craftingHeight; i < n; i++) {
            list.add(InputIngredient.empty(i));
        }

        for (int x = 0; x < craftingWidth; x++) {
            for (int y = 0; y < craftingHeight; y++) {
                InputIngredient<EntryStack<?>> ingredient = grid.get(new IntIntImmutablePair(x, y));
                if (ingredient != null) {
                    int index = craftingWidth * y + x;
                    list.set(index, ingredient);
                }
            }
        }

        return list;
    }

    public int getWeavingTime() {
        return weavingTime;
    }
}