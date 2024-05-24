package org.dimdev.dimdoors.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.block.entity.TesselatingLoomBlockEntity;

import static org.dimdev.dimdoors.recipe.ShapedTesselatingRecipe.itemStackFromJson;

public class TesselatingShapelessRecipe implements TesselatingRecipe {
    final NonNullList<Ingredient> ingredients;
    private final ResourceLocation id;
    final ItemStack result;
    final String group;
    final boolean showNotification;
    private final int weavingTime;

    public TesselatingShapelessRecipe(ResourceLocation id, String group, ItemStack result, NonNullList<Ingredient> ingredients, int weavingTime, boolean showNotification) {
        this.id = id;
        this.group = group;
        this.result = result;
        this.ingredients = ingredients;
        this.weavingTime = weavingTime;
        this.showNotification = showNotification;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.SHAPELESS_TESSELATING.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.SHAPELESS_TESSELATING.get();
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public ItemStack getResultItem() {
        return this.result;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.ingredients;
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    @Override
    public boolean matches(TesselatingLoomBlockEntity inv, Level level) {
        StackedContents stackedContents = new StackedContents();
        int i = 0;
        for (int j = 0; j < inv.getContainerSize(); ++j) {
            ItemStack itemStack = inv.getItem(j);
            if (itemStack.isEmpty()) continue;
            ++i;
            stackedContents.accountStack(itemStack, 1);
        }
        return i == this.ingredients.size() && stackedContents.canCraft(this, null);
    }

    @Override
    public int weavingTime() {
        return weavingTime;
    }


    @Override
    public ItemStack assemble(TesselatingLoomBlockEntity container) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= this.ingredients.size();
    }

    public static class Serializer
            implements RecipeSerializer<TesselatingShapelessRecipe> {
        @Override
        public TesselatingShapelessRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            String string = GsonHelper.getAsString(json, "group", "");
            NonNullList<Ingredient> nonNullList = itemsFromJson(GsonHelper.getAsJsonArray(json, "ingredients"));
            if (nonNullList.isEmpty()) {
                throw new JsonParseException("No ingredients for shapeless recipe");
            }
            if (nonNullList.size() > 9) {
                throw new JsonParseException("Too many ingredients for shapeless recipe");
            }
            ItemStack itemStack = itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));

            int weavingTime = GsonHelper.getAsInt(json, "weavingtime", 200);
            boolean bl = GsonHelper.getAsBoolean(json, "show_notification", true);

            return new TesselatingShapelessRecipe(recipeId, string, itemStack, nonNullList, weavingTime, bl);
        }

        private static NonNullList<Ingredient> itemsFromJson(JsonArray ingredientArray) {
            NonNullList<Ingredient> nonNullList = NonNullList.create();
            for (int i = 0; i < ingredientArray.size(); ++i) {
                Ingredient ingredient = Ingredient.fromJson(ingredientArray.get(i));
                if (ingredient.isEmpty()) continue;
                nonNullList.add(ingredient);
            }
            return nonNullList;
        }


        @Override
        public TesselatingShapelessRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            String string = buffer.readUtf();
            int i = buffer.readVarInt();
            NonNullList<Ingredient> nonNullList = NonNullList.withSize(i, Ingredient.EMPTY);
            nonNullList.replaceAll(ignored -> Ingredient.fromNetwork(buffer));
            ItemStack itemStack = buffer.readItem();

            int weavingTime = buffer.readInt();
            boolean bl = buffer.readBoolean();

            return new TesselatingShapelessRecipe(recipeId, string, itemStack, nonNullList, weavingTime, bl);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, TesselatingShapelessRecipe recipe) {
            buffer.writeUtf(recipe.group);
            buffer.writeVarInt(recipe.ingredients.size());
            for (Ingredient ingredient : recipe.ingredients) {
                ingredient.toNetwork(buffer);
            }
            buffer.writeItem(recipe.result);
            buffer.writeInt(recipe.weavingTime);
            buffer.writeBoolean(recipe.showNotification);
        }
    }
}

