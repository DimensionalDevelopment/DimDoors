package org.dimdev.dimdoors.util.schematic

import com.mojang.datafixers.DSL
import com.mojang.datafixers.schemas.Schema
import net.minecraft.util.datafix.fixes.References

object SchematicDataFixer {
    @JvmField
    val SCHEMATIC: DSL.TypeReference = DSL.TypeReference { "sponge_schematic" }

    @JvmStatic
    fun registerSpongeSchematicV2Type(schema: Schema) {
        schema.registerType(false, References.BLOCK_STATE) { DSL.remainder() }
        schema.registerType(false, References.FLAT_BLOCK_STATE) { DSL.remainder() }
        schema.registerType(false, SCHEMATIC) { SpongeSchematicV2FixTypes.schematic(schema) }
    }
}
