package org.dimdev.dimdoors.client;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ModelEvent;

public final class GeneratedDoorModelEvents {
    public static void init(IEventBus bus) {
        bus.addListener(GeneratedDoorModelEvents::registerAdditional);
        bus.addListener(GeneratedDoorModelEvents::modifyBakingResult);
    }

    private static void registerAdditional(ModelEvent.RegisterAdditional event) {
        for (var portal : GeneratedDoorModelMappings.PORTAL_ITEM_MODELS) {
            event.register(ModelResourceLocation.standalone(portal));
        }
    }

    private static void modifyBakingResult(ModelEvent.ModifyBakingResult event) {
        var mappings = GeneratedDoorModelMappings.create();
        var models = event.getModels();

        for (var entry : mappings.blockModels().entrySet()) {
            var source = models.get(entry.getValue());

            if (source != null) {
                models.put(entry.getKey(), source);
            }
        }

        for (var entry : mappings.itemModels().entrySet()) {
            var source = models.get(entry.getValue().source());
            var portal = models.get(
                    ModelResourceLocation.standalone(entry.getValue().portal())
            );

            if (source == null || portal == null) {
                continue;
            }

            models.put(
                    entry.getKey(),
                    new GeneratedDoorItemModel(source, portal)
            );
        }
    }

    private GeneratedDoorModelEvents() {
    }
}
