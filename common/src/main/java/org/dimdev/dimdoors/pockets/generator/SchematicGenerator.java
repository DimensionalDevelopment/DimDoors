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
import org.dimdev.dimdoors.rift.registry.PocketRegistry;
import org.dimdev.dimdoors.util.schematic.Schematic;
import org.dimdev.dimdoors.world.pocket.type.AbstractPocket;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SchematicGenerator extends PocketGenerator<SchematicGenerator> {

    public static final MapCodec<SchematicGenerator> CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(instance)
            .and(ResourceLocation.CODEC.fieldOf("id").forGetter(schematicGenerator -> schematicGenerator.templateID))
            .apply(instance, SchematicGenerator::new));

    private static final Logger LOGGER = LogManager.getLogger();
    public static final String KEY = "schematic";

    private final ResourceLocation templateID;

    public SchematicGenerator(Optional<AbstractPocket.AbstractPocketBuilder<?, ?>> builder, Equation weight, Optional<Boolean> setupLoot, List<Holder<Modifier>> modifiers, List<String> tags, ResourceLocation id) {
        super(builder, weight, setupLoot, modifiers, tags);
        this.templateID = id;
    }
    
    @Override
    public Pocket<?, ?> prepareAndPlacePocket(PocketGenerationContext parameters, Pocket.PocketBuilder<?, ?> builder) {
        ServerLevel world = parameters.world();
        Map<String, Double> variableMap = parameters.toVariableMap(new HashMap<>());

        PocketTemplate template = PocketLoader.getTemplates().get(Path.stringPath(templateID));

        if (template == null) throw new RuntimeException("Pocket template of id " + templateID + " not found!");

        Pocket<?, ?> pocket = PocketRegistry.getInstance().createPocket(world.dimension(), builder);
        BlockPos origin = pocket.getOrigin();
        LOGGER.info("Generating pocket from template {} at location {}", templateID, origin);

        template.place(pocket);

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
        return template.getSize();
    }
}