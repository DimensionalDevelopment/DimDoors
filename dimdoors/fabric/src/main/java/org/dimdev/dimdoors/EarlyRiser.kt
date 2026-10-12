package org.dimdev.dimdoors

import com.chocohead.mm.api.ClassTinkerers
import net.fabricmc.api.EnvType
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.world.item.Items
import org.dimdev.dimcore.EnumExtender
import org.dimdev.dimdoors.item.ModItems

class EarlyRiser : EnumExtender {
    override fun extend(mc: (String) -> String) {

        ClassTinkerers.enumBuilder(mc("class_5421")).addEnum("TESSELLATING").build()

        if (FabricLoader.getInstance().environmentType == EnvType.CLIENT) {
            ClassTinkerers.enumBuilder(mc("class_314"), "[L${mc("class_1799")};")
                .addEnum("TESSELATING_GENERAL") { arrayOf<Any>(arrayOf(ModItems.WORLD_THREAD.defaultInstance)) }
                .addEnum("TESSELATING_SEARCH") { arrayOf<Any>(arrayOf(Items.COMPASS.defaultInstance)) }
                .build()
        }
    }
}
