package org.dimdev.dimdoors.forge.client;

//public class ModRecipeBookGroups {
//    public static final Supplier<RecipeBookCategories> TESSELATING_GENERAL = Suppliers.memoize(() -> getRecipBookCategories("TESSELATING_GENERAL", () -> ModItems.WORLD_THREAD.get().getDefaultInstance()).get());
//    public static final Supplier<RecipeBookCategories> TESSELATING_SEARCH = Suppliers.memoize(() -> getRecipBookCategories("TESSELATING_SEARCH", Items.COMPASS::getDefaultInstance).get());

//    @ExpectPlatform
//    private static Supplier<RecipeBookCategories> getRecipBookCategories(String name, Supplier<ItemStack> itemStack) {
//        throw new RuntimeException();
//    }

//    public static void init() {
//        RegisterRecipeBookCategoriesEvent.EVENT.register(event -> {
//            event.registerBookCategories(ModRecipeBookTypes.TESSELLATING, List.of(TESSELATING_GENERAL.get(), TESSELATING_SEARCH.get()));
//            event.registerAggregateCategory(ModRecipeBookGroups.TESSELATING_SEARCH.get(), List.of(ModRecipeBookGroups.TESSELATING_GENERAL.get()));
//            event.registerRecipeCategoryFinder(ModRecipeTypes.TESSELATING.get(), recipe -> ModRecipeBookGroups.TESSELATING_GENERAL.get());
//        });
//    }
//}
