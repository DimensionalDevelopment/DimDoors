package org.dimdev.dimdoors.client.neoforge;

import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import net.neoforged.neoforge.client.event.*;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.client.*;
import org.dimdev.dimdoors.client.effect.DimensionEffect;
import org.dimdev.dimdoors.client.effect.VoidDimensionSpecialEffects;
import org.dimdev.dimdoors.compat.create.CreateCompatBlockEntityTypes;
import org.dimdev.dimdoors.compat.create.SlidingEntranceRiftBlockEntityRenderer;
import org.dimdev.dimdoors.item.ModItems;

import java.util.List;
import java.util.function.Supplier;

@Mod(dist = Dist.CLIENT, value = DimensionalDoors.MOD_ID)
public class DimensionalDoorsForgeClient extends NeoForgeClientSided<DimensionalDoorsForgeClient, DimensionalDoorsClient> implements IDimDoorsClientSided<DimensionalDoorsForgeClient> {
    public static final EnumProxy<RecipeBookCategories> TESSELLATING_GENERAL = new EnumProxy<>(RecipeBookCategories.class, (Supplier<List<ItemStack>>) () -> List.of(ModItems.WORLD_THREAD.getDefaultInstance()));
    public static final EnumProxy<RecipeBookCategories> TESSELLATING_SEARCH = new EnumProxy<>(RecipeBookCategories.class, (Supplier<List<ItemStack>>) () -> List.of(Items.COMPASS.getDefaultInstance()));

    public Supplier<RecipeBookCategories> getRecipBookCategories(String name, Supplier<ItemStack> itemStack) {
        return switch (name) {
            case "TESSELATING_GENERAL" -> TESSELLATING_GENERAL::getValue;
            case "TESSELATING_SEARCH" -> TESSELLATING_SEARCH::getValue;
            default -> throw new IllegalArgumentException("Unknown recipe book category: " + name);
        };
    }

    public DimensionalDoorsForgeClient(IEventBus bus, ModContainer container) {
        super(bus, container, DimensionalDoorsClient.INSTANCE);
        DimensionalDoorsClient.INSTANCE.init(this);

        bus.addListener(DimensionalDoorsForgeClient::registerRecipeBookCategories);
        if (DimensionalDoors.getSided().isModLoaded("create")) {
            bus.addListener(DimensionalDoorsForgeClient::registerCreateBlockEntityRenderers);
        }

    }

    public static void registerRecipeBookCategories(RegisterRecipeBookCategoriesEvent event) {
        ModRecipeBookGroups.init();
        org.dimdev.dimdoors.api.util.RegisterRecipeBookCategoriesEvent.EVENT.invoker().accept(
                new org.dimdev.dimdoors.api.util.RegisterRecipeBookCategoriesEvent(
                        event::registerAggregateCategory,
                        event::registerBookCategories,
                        event::registerRecipeCategoryFinder
                )
        );
    }

    private static void registerCreateBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(CreateCompatBlockEntityTypes.SLIDING_ENTRANCE_RIFT, SlidingEntranceRiftBlockEntityRenderer::new);
    }

    @Override
    public VoidDimensionSpecialEffects createVoidEffect(DimensionEffect effect) {
        return new NfVoidDimensionEffects(effect);
    }
}
