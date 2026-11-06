package org.dimdev.dimdoors.mixin;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.V3448;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.function.Supplier;

@Mixin(V3448.class)
public abstract class V3448TesselatingLoomMixin {
    @Inject(method = "registerBlockEntities", at = @At("RETURN"))
    private void registerTesselatingLoom(Schema schema, CallbackInfoReturnable<Map<String, Supplier<TypeTemplate>>> cir) {
        schema.register(cir.getReturnValue(), "dimdoors:tesselating_loom", () -> DSL.optionalFields(
            "Inventory", DSL.optionalFields(
                "Items", DSL.list(References.ITEM_STACK.in(schema)),
                "Output", References.ITEM_STACK.in(schema)
            )
        ));
    }
}
