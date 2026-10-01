package org.dimdev.dimdoors

import com.chocohead.mm.api.ClassTinkerers
import net.fabricmc.api.EnvType
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import org.dimdev.dimdoors.item.ModItems

class EarlyRiser : Runnable {
    override fun run() {
        val remapper = FabricLoader.getInstance().mappingResolver
        val recipeBookType = remapper.mapClassName("intermediary", "net.minecraft.class_5421")
        ClassTinkerers.enumBuilder(recipeBookType).addEnum("TESSELLATING").build()

        if (FabricLoader.getInstance().environmentType == EnvType.CLIENT) {
            val recipeBookGroup = remapper.mapClassName("intermediary", "net.minecraft.class_314")
            val itemStackArray = "[L" + remapper.mapClassName("intermediary", "net.minecraft.class_1799") + ";"
            ClassTinkerers.enumBuilder(recipeBookGroup, itemStackArray)
                .addEnum("TESSELATING_GENERAL") { arrayOf<Any>(arrayOf<ItemStack>(ModItems.WORLD_THREAD.value().defaultInstance)) }
                .addEnum("TESSELATING_SEARCH") { arrayOf<Any>(arrayOf<ItemStack>(Items.COMPASS.defaultInstance)) }
                .build()
        }
    }
}
