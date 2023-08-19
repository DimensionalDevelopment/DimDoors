package org.dimdev.dimdoors;

import com.chocohead.mm.api.ClassTinkerers;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.dimdev.dimdoors.item.ModItems;

public class EarlyRiser implements Runnable {
    @Override
    public void run() {
        String recipeBookType = FabricLoader.getInstance().getMappingResolver().mapClassName("intermediary", "net.minecraft.class_5421");
        ClassTinkerers.enumBuilder(recipeBookType).addEnum("TESSELLATING").build();

        if(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            MappingResolver remapper = FabricLoader.getInstance().getMappingResolver();
            String recipeBookGroup = remapper.mapClassName("intermediary", "net.minecraft.class_314");
            String itemStack1 = "[L" + remapper.mapClassName("intermediary", "net.minecraft.class_1799") + ";";
            ClassTinkerers.enumBuilder(recipeBookGroup, itemStack1)
                    .addEnum("TESSELATING_GENERAL", () -> new Object[] {new ItemStack[]{ModItems.WORLD_THREAD.get().getDefaultInstance()}})
                    .addEnum("TESSELATING_SEARCH", () -> new Object[] {new ItemStack[]{Items.COMPASS.getDefaultInstance()}}).build();
        }
    }
}
