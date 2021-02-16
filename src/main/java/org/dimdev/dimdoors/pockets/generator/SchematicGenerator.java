package org.dimdev.dimdoors.pockets.generator;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3i;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.pockets.PocketTemplateV2;
import org.dimdev.dimdoors.pockets.SchematicV2Handler;
import org.dimdev.dimdoors.util.PocketGenerationParameters;
import org.dimdev.dimdoors.util.math.Equation;
import org.dimdev.dimdoors.util.schematic.v2.Schematic;
import org.dimdev.dimdoors.world.level.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.HashMap;
import java.util.Map;

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

	private String offsetX;
	private Equation offsetXEquation;
	private String offsetY;
	private Equation offsetYEquation;
	private String offsetZ;
	private Equation offsetZEquation;

	public SchematicGenerator() {
	}

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

		try {
			offsetX = tag.contains("offset_x") ? tag.getString("offset_x") : "0";
			offsetXEquation = Equation.parse(offsetX);
			offsetY = tag.contains("offset_y") ? tag.getString("offset_y") : "0";
			offsetYEquation = Equation.parse(offsetY);
			offsetZ = tag.contains("offset_z") ? tag.getString("offset_z") : "0";
			offsetZEquation = Equation.parse(offsetZ);
		} catch (Equation.EquationParseException e) {
			LOGGER.error(e);
		}

		return this;
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);

		tag.putString("id", this.id);

		if (!offsetX.equals("0")) tag.putString("offset_x", offsetX);
		if (!offsetY.equals("0")) tag.putString("offset_y", offsetY);
		if (!offsetZ.equals("0")) tag.putString("offset_z", offsetZ);

		return tag;
	}

	@Override
	public Pocket prepareAndPlacePocket(PocketGenerationParameters parameters, Pocket.PocketBuilder<?, ?> builder) {
		ServerWorld world = parameters.getWorld();
		Map<String, Double> variableMap = parameters.toVariableMap(new HashMap<>());

		PocketTemplateV2 template = SchematicV2Handler.getInstance().getTemplates().get(templateID);
		if (template == null) throw new RuntimeException("Pocket template of id " + templateID + " not found!");

		Pocket pocket = DimensionalRegistry.getPocketDirectory(world.getRegistryKey()).newPocket(builder);
		LOGGER.info("Generating pocket from template " + template.getId() + " at location " + pocket.getOrigin());

		pocket.offsetOrigin((int) offsetXEquation.apply(variableMap), (int) offsetYEquation.apply(variableMap), (int) offsetZEquation.apply(variableMap));

		template.place(pocket);

		pocket.virtualLocation = parameters.getSourceVirtualLocation(); // TODO: this makes very little sense

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

	@Override
	public Vec3i getSize(PocketGenerationParameters parameters) {
		PocketTemplateV2 template = SchematicV2Handler.getInstance().getTemplates().get(templateID);
		if (template == null) throw new RuntimeException("Pocket template of id " + templateID + " not found!");
		Schematic schem = template.getSchematic();
		return new Vec3i(schem.getWidth(), schem.getHeight(), schem.getLength());
	}
}
