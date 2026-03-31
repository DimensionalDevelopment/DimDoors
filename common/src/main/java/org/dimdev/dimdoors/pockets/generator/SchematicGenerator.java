package org.dimdev.dimdoors.pockets.generator;

import com.bedrockk.molang.Expression;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.api.util.Path;
import org.dimdev.dimdoors.command.PocketCommand;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.PocketLoader;
import org.dimdev.dimdoors.pockets.PocketTemplate;
import org.dimdev.dimdoors.pockets.modifier.Modifier;
import org.dimdev.dimdoors.util.schematic.Schematic;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.type.PocketBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SchematicGenerator extends PocketGenerator {
    public static final MapCodec<SchematicGenerator> CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(instance)
            .and(PocketTemplate.CODEC.fieldOf("template").forGetter(a -> a.template)).apply(instance, SchematicGenerator::new));

	private static final Logger LOGGER = LogManager.getLogger();
	public static final String KEY = "schematic";

    private final PocketTemplate template;


    public SchematicGenerator(PocketBuilder builder, Expression expression, List<Modifier> modifiers, Boolean setupLoot, List<String> tags, PocketTemplate template) {
        super(builder, expression, modifiers, setupLoot, tags);
        this.template = template;
    }

    //	@Override
//	public RiftManager getRiftManager(Pocket pocket) {
//		RiftManager manager = super.getRiftManager(pocket);
//
//		rifts.forEach(manager::add);
//
//		return manager;
//	}

    @Override
    public UUID prepareAndPlacePocket(PocketGenerationContext parameters, PocketBuilder builder) {
        ServerLevel world = parameters.world();
        Map<String, Double> variableMap = parameters.toVariableMap(new HashMap<>());

        PocketTemplate template = PocketLoader.getTemplates().get(Path.stringPath(templateID));
        if (template == null) throw new RuntimeException("Pocket template of id " + templateID + " not found!");

        Pocket pocket = DimensionalRegistry.getPocketDirectory(world.dimension()).newPocket(builder);
        BlockPos origin = pocket.getOrigin();
        LOGGER.info("Generating pocket from template {} at location {}", templateID, origin);
        PocketCommand.logSetting.values().forEach(commandSource ->
                commandSource.sendSuccess(() -> Component.translatable(
                        "commands.pocket.log.creation.generating",
                        templateID, origin.getX(), origin.getY(), origin.getZ()
                ), false)
        );

        // Get block entities directly from placement
        template.place(pocket, placementType);

        // Cache them in the pocket
//        pocket.cacheBlockEntities(placedEntities);

//        LOGGER.info("Cached {} block entities in pocket", placedEntities.size());

        return pocket;
    }

    @Override
    public Pocket prepareAndPlacePocket(PocketGenerationContext parameters, Pocket.PocketBuilder<?, ?> builder) {
        return null;
    }

    @Override
	public PocketGeneratorType<? extends PocketGenerator> getType() {
		return PocketGeneratorType.SCHEMATIC.get();
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public Vec3i getSize(PocketGenerationContext parameters) {
		Schematic schem = template.getSchematic();
		return new Vec3i(schem.getWidth(), schem.getHeight(), schem.getLength());
	}
}