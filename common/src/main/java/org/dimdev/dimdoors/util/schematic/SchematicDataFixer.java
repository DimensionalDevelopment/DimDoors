package org.dimdev.dimdoors.util.schematic;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.util.datafix.fixes.References;

public final class SchematicDataFixer {
    public static final DSL.TypeReference SCHEMATIC = () -> "sponge_schematic";

    private SchematicDataFixer() {
    }

    public static void registerSpongeSchematicV2Type(Schema schema) {
        schema.registerType(false, References.BLOCK_STATE, DSL::remainder);
        schema.registerType(false, References.FLAT_BLOCK_STATE, DSL::remainder);
        schema.registerType(false, SCHEMATIC, () -> SpongeSchematicV2FixTypes.schematic(schema));
    }
}
