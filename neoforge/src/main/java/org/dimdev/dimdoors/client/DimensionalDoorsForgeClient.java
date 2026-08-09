package org.dimdev.dimdoors.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.common.NeoForge;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.door.DimensionalDoorBlockRegistrar;
import org.dimdev.dimdoors.client.*;
import org.dimdev.dimdoors.client.config.ConfigScreen;
import org.dimdev.dimdoors.client.effect.DimensionEffect;
import org.dimdev.dimdoors.client.effect.VoidDimensionSpecialEffects;
import org.dimdev.dimdoors.compat.create.CreateCompatBlockEntityTypes;
import org.dimdev.dimdoors.compat.create.SlidingEntranceRiftBlockEntityRenderer;
import org.dimdev.dimdoors.item.ModItems;
import org.dimdev.limlib.client.NeoForgeClientSided;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import java.util.List;
import java.util.function.Consumer;
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
        container.registerExtensionPoint(IConfigScreenFactory.class, (modContainer, screen) -> ConfigScreen.createScreen(screen));
        DimensionalDoorsClient.INSTANCE.init(this);

        bus.addListener(DimensionalDoorsForgeClient::registerRecipeBookCategories);
        if (DimensionalDoors.getSided().isModLoaded("create")) {
            bus.addListener(DimensionalDoorsForgeClient::registerCreateBlockEntityRenderers);
        }

        GeneratedDoorModelEvents.init(bus);

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

    @Override
    public void onPreRender(PreRender onPrerender) {
        NeoForge.EVENT_BUS.<RenderFrameEvent.Pre>addListener(event -> {
            var minecraft = Minecraft.getInstance();

            if(minecraft.level == null) return;

            onPrerender.preRender(minecraft.level.getGameTime(), event.getPartialTick().getGameTimeDeltaPartialTick(false));
        });
    }
}
