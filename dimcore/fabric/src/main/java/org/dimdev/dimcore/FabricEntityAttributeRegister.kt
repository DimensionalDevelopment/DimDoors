package org.dimdev.dimcore

import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricDefaultAttributeRegistry
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import org.dimdev.dimcore.api.EntityAttributeRegister

object FabricEntityAttributeRegister : EntityAttributeRegister {
    override fun register(
        type: EntityType<out LivingEntity>,
        attributes: () -> AttributeSupplier.Builder
    ) = FabricDefaultAttributeRegistry.register(type, attributes.invoke())
}