package org.dimdev.dimcore.client

import com.chocohead.mm.api.ClassTinkerers
import net.fabricmc.api.EnvType
import net.fabricmc.loader.api.FabricLoader
import org.dimdev.dimcore.EnumExtender
import org.lwjgl.opengl.GL32

class DimcoreEarlyRiser : Runnable {
    override fun run() {
        val remapper = FabricLoader.getInstance().mappingResolver
        val mc = { name: String -> remapper.mapClassName("intermediary", "net.minecraft.$name") }
        FabricLoader.getInstance()
            .getEntrypoints("dimcore:enum_extensions", EnumExtender::class.java)
            .forEach { it.extend(mc) }

        if (FabricLoader.getInstance().environmentType == EnvType.CLIENT) {
            ClassTinkerers.enumBuilder(mc("class_281\$class_282"), String::class.java, String::class.java, Int::class.java)
                .addEnum("DIMCORE_GEOMETRY", "dimcore:geometry", ".gsh", GL32.GL_GEOMETRY_SHADER)
                .build()

        }
    }
}
