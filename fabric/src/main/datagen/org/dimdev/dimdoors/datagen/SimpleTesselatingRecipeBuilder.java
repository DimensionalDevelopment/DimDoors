package org.dimdev.dimdoors.datagen;

import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

public abstract class SimpleTesselatingRecipeBuilder extends TesselatingRecipeBuilder {
    private final ItemLike result;
    private final int count;

    public SimpleTesselatingRecipeBuilder(ItemLike output, int outputCount) {
        this.result = output.asItem();
        this.count = outputCount;
    }

    public Item getResult() {
        return this.result.asItem();
    }

    @Override
    protected <T extends TesselatingRecipeResult> T createResult(ResourceLocation id, Advancement.Builder builder) {
        return createResult(id, builder, result, count);
    }

    abstract protected <T extends TesselatingRecipeResult> T createResult(ResourceLocation id, Advancement.Builder builder, ItemLike itemLike, int count);

    protected static abstract class SimpleResult extends TesselatingRecipeResult {
        private final ItemLike result;
        private final int count;

        public SimpleResult(ResourceLocation id, ItemLike result, int count, String group, AdvancementHolder advancement, int weavingTime) {
            super(id, group, advancement, weavingTime);
            this.result = result;
            this.count = count;
        }

        public void serializeRecipeData(JsonObject json) {
            super.serializeRecipeData(json);

            JsonObject jsonObject2 = new JsonObject();
            jsonObject2.addProperty("item", BuiltInRegistries.ITEM.getKey(this.result.asItem()).toString());
            if (this.count > 1) {
                jsonObject2.addProperty("count", this.count);
            }

            json.add("result", jsonObject2);
        }
    }
}
