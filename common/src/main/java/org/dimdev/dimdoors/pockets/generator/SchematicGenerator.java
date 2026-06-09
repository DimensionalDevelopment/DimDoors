package org.dimdev.dimdoors.pockets.generator;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.api.util.BlockPlacementType;
import org.dimdev.dimdoors.api.util.Path;
import org.dimdev.dimdoors.api.util.math.Equation;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.PocketLoader;
import org.dimdev.dimdoors.pockets.PocketTemplate;
import org.dimdev.dimdoors.pockets.modifier.Modifier;
import org.dimdev.dimdoors.util.schematic.Schematic;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.type.AbstractPocket;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SchematicGenerator extends PocketGenerator<SchematicGenerator> {

    public static final MapCodec<SchematicGenerator> CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(instance)
            .and(ResourceLocation.CODEC.fieldOf("id").forGetter(schematicGenerator -> schematicGenerator.templateID))
            .and(BlockPlacementType.CODEC.optionalFieldOf("placement_type", BlockPlacementType.SECTION_NO_UPDATE).<SchematicGenerator>forGetter(a -> a.placementType)).apply(instance, SchematicGenerator::new));

    private static final Logger LOGGER = LogManager.getLogger();
    public static final String KEY = "schematic";

    private final ResourceLocation templateID;
    private final BlockPlacementType placementType;

    public SchematicGenerator(Optional<AbstractPocket.AbstractPocketBuilder<?, ?>> builder, Equation weight, Optional<Boolean> setupLoot, List<Holder<Modifier>> modifiers, List<String> tags, ResourceLocation id, BlockPlacementType placementType) {
        super(builder, weight, setupLoot, modifiers, tags);
        this.templateID = id;
        this.placementType = placementType;
    }

    public ResourceLocation getTemplateID() {
        return templateID;
    }

    @Override
    public Pocket<?, ?> prepareAndPlacePocket(PocketGenerationContext parameters, Pocket.PocketBuilder<?, ?> builder) {
        ServerLevel world = parameters.world();
        Map<String, Double> variableMap = parameters.toVariableMap(new HashMap<>());

        PocketTemplate template = PocketLoader.getTemplates().get(Path.stringPath(templateID));

        if (template == null) throw new RuntimeException("Pocket template of id " + templateID + " not found!");

        Pocket<?, ?> pocket = DimensionalRegistry.createPocket(world.dimension(), builder);
        BlockPos origin = pocket.getOrigin();
        LOGGER.info("Generating pocket from template {} at location {}", templateID, origin);
//        PocketCommand.logSetting.values().forEach(commandSource ->
//                commandSource.sendSuccess(() -> Component.translatable(
//                        "commands.pocket.log.creation.generating",
//                        templateID, origin.getX(), origin.getY(), origin.getZ()
//                ), false)
//        );

        // Get block entities directly from placement
        template.place(pocket, placementType);

        // Cache them in the pocket
//        pocket.cacheBlockEntities(placedEntities);

//        LOGGER.info("Cached {} block entities in pocket", placedEntities.size());

        return pocket;
    }

    @Override
    public PocketGeneratorType<SchematicGenerator> type() {
        return PocketGeneratorType.SCHEMATIC;
    }

    @Override
    public Vec3i getSize(PocketGenerationContext parameters) {
        PocketTemplate template = PocketLoader.getTemplates().get(Path.stringPath(templateID));
        if (template == null) throw new RuntimeException("Pocket template of id " + templateID + " not found!");
        Schematic schem = template.getSchematic();
        return new Vec3i(schem.getWidth(), schem.getHeight(), schem.getLength());
    }
}