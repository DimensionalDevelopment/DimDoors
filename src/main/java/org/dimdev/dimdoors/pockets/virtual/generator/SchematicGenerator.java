package org.dimdev.dimdoors.pockets.virtual.generator;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.pockets.PocketGroup;
import org.dimdev.dimdoors.pockets.PocketTemplateV2;
import org.dimdev.dimdoors.pockets.SchematicV2Handler;
import org.dimdev.dimdoors.pockets.virtual.VirtualGeneratorPocket;
import org.dimdev.dimdoors.pockets.virtual.VirtualSingularPocket;
import org.dimdev.dimdoors.util.PocketGenerationParameters;
import org.dimdev.dimdoors.world.level.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.Pocket;

public class SchematicGenerator extends VirtualGeneratorPocket {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final String KEY = "schematic";

	/*
	public static final Codec<SchematicGenerator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.fieldOf("size").forGetter(SchematicGenerator::getSize),
			Codec.STRING.fieldOf("id").forGetter(SchematicGenerator::getName),
			Codec.INT.optionalFieldOf("weight", 5).forGetter(schematicGenerator -> schematicGenerator.getWeight(null))
	).apply(instance, SchematicGenerator::new));
	*/

	private int size;
	private String name;
	private Identifier templateID;
	private int weight;

	public SchematicGenerator() {}

	public SchematicGenerator(int size, String name, int weight) {
		this.size = size;
		this.name = name;
		this.weight = weight;

		this.templateID = new Identifier("dimdoors", name);
	}

	public int getSize() {
		return this.size;
	}

	public String getName() {
		return this.name;
	}

	public Identifier getTemplateID() {
		return templateID;
	}

	@Override
	public int getWeight(PocketGenerationParameters parameters){
		return this.weight;
	}

	@Override
	public VirtualGeneratorPocket fromTag(CompoundTag tag) {
		super.fromTag(tag);

		this.name = tag.getString("id");
		this.size = tag.getInt("size");
		this.weight = tag.contains("weight") ? tag.getInt("weight") : 5;

		this.templateID = new Identifier("dimdoors", name);
		return this;
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);

		tag.putString("id", this.name);
		tag.putInt("size", this.size);
		tag.putInt("weight", this.weight);
		return tag;
	}

	@Override
	public void init(PocketGroup group) {
		SchematicV2Handler.getInstance().loadSchematic(templateID, group.getGroup(), size, name);
	}

	@Override
	public Pocket prepareAndPlacePocket(PocketGenerationParameters parameters) {
		ServerWorld world = parameters.getWorld();

		PocketTemplateV2 template = SchematicV2Handler.getInstance().getTemplates().get(templateID);
		if (template == null) throw new RuntimeException("Pocket template of id " + templateID + " not found!");

		Pocket pocket = DimensionalRegistry.getPocketDirectory(world.getRegistryKey()).newPocket();
		LOGGER.info("Generating pocket from template " + template.getId() + " at location " + pocket.getOrigin());

		template.place(pocket);
		applyModifiers(pocket, parameters);
		setup(pocket, parameters, true);
		return pocket;
	}

	@Override
	public String toString() {
		return "PocketEntry{" +
				"size=" + this.size +
				", name='" + this.name + '\'' +
				", weight=" + this.weight +
				'}';
	}

	@Override
	public VirtualSingularPocketType<? extends VirtualSingularPocket> getType() {
		return VirtualSingularPocketType.SCHEMATIC;
	}

	@Override
	public String getKey() {
		return KEY;
	}
}
