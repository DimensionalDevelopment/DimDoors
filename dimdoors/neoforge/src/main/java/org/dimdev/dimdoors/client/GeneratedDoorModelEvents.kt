package org.dimdev.dimdoors.client

import net.minecraft.client.resources.model.ModelResourceLocation
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.client.event.ModelEvent

object GeneratedDoorModelEvents {
    fun init(bus: IEventBus) {
        bus.addListener(::registerAdditional)
        bus.addListener { obj: ModelEvent.ModifyBakingResult -> modifyBakingResult(obj) }
    }

    private fun registerAdditional(event: ModelEvent.RegisterAdditional) {
        for (portal in GeneratedDoorModelMappings.PORTAL_ITEM_MODELS) {
            event.register(ModelResourceLocation.standalone(portal))
        }
    }

    private fun modifyBakingResult(event: ModelEvent.ModifyBakingResult) {
        val mappings = GeneratedDoorModelMappings.create()
        val models = event.models

        for (entry in mappings.blockModels.entries)
            models[entry.value]?.let { models[entry.key] = it }

        for (entry in mappings.itemModels.entries) {
            val source = models[entry.value.source]
            val portal = models[ModelResourceLocation.standalone(entry.value.portal)]

            if (source == null || portal == null) {
                continue
            }

            models[entry.key] = GeneratedDoorItemModel(source, portal)
        }
    }
}
