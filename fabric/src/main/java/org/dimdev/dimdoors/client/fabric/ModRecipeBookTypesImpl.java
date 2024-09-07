package org.dimdev.dimdoors.client.fabric;

import com.chocohead.mm.api.ClassTinkerers;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.impl.content.registry.util.ImmutableCollectionUtils;
import net.minecraft.world.inventory.RecipeBookType;
import org.dimdev.dimdoors.mixin.RecipeBookSettingsAccessor;

public class ModRecipeBookTypesImpl {
    public static RecipeBookType getRecipeBookType(String name) {
        var type = ClassTinkerers.getEnum(RecipeBookType.class, "TESSELLATING");
        ImmutableCollectionUtils.getAsMutableMap(RecipeBookSettingsAccessor::getTagFields, RecipeBookSettingsAccessor::setTagFields).put(type, Pair.of("isTessellatingGui", "isTessellatingFilteringCraftable"));
        return type;
    }
}
