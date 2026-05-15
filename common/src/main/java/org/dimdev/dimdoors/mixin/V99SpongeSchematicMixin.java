package org.dimdev.dimdoors.mixin;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import net.minecraft.util.datafix.schemas.V99;
import org.dimdev.dimdoors.util.schematic.SchematicDataFixer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.function.Supplier;

@Mixin(V99.class)
public abstract class V99SpongeSchematicMixin {
    @Inject(method = "registerTypes", at = @At("TAIL"))
    private void registerSpongeSchematicV2Type(Schema schema, Map<String, Supplier<TypeTemplate>> entityTypes, Map<String, Supplier<TypeTemplate>> blockEntityTypes, CallbackInfo ci) {
        SchematicDataFixer.registerSpongeSchematicV2Type(schema);
    }
}
