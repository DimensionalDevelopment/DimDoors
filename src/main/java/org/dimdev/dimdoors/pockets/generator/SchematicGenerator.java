package org.dimdev.dimdoors.pockets.generator;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.pockets.PocketTemplateV2;
import org.dimdev.dimdoors.pockets.SchematicV2Handler;
import org.dimdev.dimdoors.util.PocketGenerationParameters;
import org.dimdev.dimdoors.world.level.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.Pocket;

public class SchematicGenerator extends PocketGenerator {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final String KEY = "schematic";

	/*
	public static final Codec<SchematicGenerator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.fieldOf("size").forGetter(SchematicGenerator::getSize),
			Codec.STRING.fieldOf("id").forGetter(SchematicGenerator::getName),
			Codec.INT.optionalFieldOf("weight", 5).forGetter(schematicGenerator -> schematicGenerator.getWeight(null))
	).apply(instance, SchematicGenerator::new));
	*/

	private String id;
	private Identifier templateID;

	public SchematicGenerator() {}

	public SchematicGenerator(String id) {
		this.id = id;

		this.templateID = new Identifier("dimdoors", id);
	}

	public String getId() {
		return this.id;
	}

	public Identifier getTemplateID() {
		return templateID;
	}

	@Override
	public PocketGenerator fromTag(CompoundTag tag) {
		super.fromTag(tag);

		this.id = tag.getString("id");
		this.templateID = new Identifier("dimdoors", id);

		SchematicV2Handler.getInstance().loadSchematic(templateID, id);

		return this;
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);

		tag.putString("id", this.id);
		return tag;
	}

	@Override
	public Pocket prepareAndPlacePocket(PocketGenerationParameters parameters) {
		ServerWorld world = parameters.getWorld();

		PocketTemplateV2 template = SchematicV2Handler.getInstance().getTemplates().get(templateID);
		if (template == null) throw new RuntimeException("Pocket template of id " + templateID + " not found!");

		Pocket pocket = DimensionalRegistry.getPocketDirectory(world.getRegistryKey()).newPocket();
		LOGGER.info("Generating pocket from template " + template.getId() + " at location " + pocket.getOrigin());

		template.place(pocket);

		return pocket;
	}

	@Override
	public PocketGeneratorType<? extends PocketGenerator> getType() {
		return PocketGeneratorType.SCHEMATIC;
	}

	@Override
	public String getKey() {
		return KEY;
	}
}
