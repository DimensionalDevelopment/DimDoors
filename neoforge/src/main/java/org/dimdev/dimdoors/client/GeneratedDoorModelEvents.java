package org.dimdev.dimdoors.client;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ModelEvent;

public final class GeneratedDoorModelEvents {
    private static final ModelResourceLocation PORTAL_MODEL =
            ModelResourceLocation.standalone(
                    GeneratedDoorModelMappings.PORTAL_ITEM_MODEL
            );

    public static void init(IEventBus bus) {
        bus.addListener(GeneratedDoorModelEvents::registerAdditional);
        bus.addListener(GeneratedDoorModelEvents::modifyBakingResult);
    }

    private static void registerAdditional(ModelEvent.RegisterAdditional event) {
        event.register(PORTAL_MODEL);
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

        var portal = models.get(PORTAL_MODEL);
        if (portal == null) {
            return;
        }

        for (var entry : mappings.itemModels().entrySet()) {
            var source = models.get(entry.getValue());

            if (source != null) {
                var block = BuiltInRegistries.BLOCK.get(entry.getKey().id());

                models.put(
                        entry.getKey(),
                        new GeneratedDoorItemModel(
                                source,
                                portal,
                                block instanceof TrapDoorBlock
                        )
                );
            }
        }
    }

    private GeneratedDoorModelEvents() {
    }
}