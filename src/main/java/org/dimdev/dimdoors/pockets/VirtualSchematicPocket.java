package org.dimdev.dimdoors.pockets;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;
import org.dimdev.dimdoors.util.PocketGenerationParameters;
import org.dimdev.dimdoors.world.level.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.Pocket;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;

public class VirtualSchematicPocket extends VirtualPocket{
	private static final Logger LOGGER = LogManager.getLogger();

	public static final Codec<VirtualSchematicPocket> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.fieldOf("size").forGetter(VirtualSchematicPocket::getSize),
			Codec.STRING.fieldOf("id").forGetter(VirtualSchematicPocket::getName),
			Codec.INT.optionalFieldOf("weight", 5).forGetter(virtualSchematicPocket -> virtualSchematicPocket.getWeight(null))
	).apply(instance, VirtualSchematicPocket::new));

	private final int size;
	private final String name;
	private Identifier templateID;
	private final int weight;

	VirtualSchematicPocket(int size, String name, int weight) {
		this.size = size;
		this.name = name;
		this.weight = weight;
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

	public void setTemplateID(Identifier templateID) {
		this.templateID = templateID;
	}

	@Override
	public int getWeight(PocketGenerationParameters parameters){
		return this.weight;
	}

	@Override
	public Pocket prepareAndPlacePocket(PocketGenerationParameters parameters) {
		ServerWorld world = parameters.getWorld();
		VirtualLocation virtualLocation = parameters.getVirtualLocation();
		VirtualTarget linkTo = parameters.getLinkTo();
		LinkProperties linkProperties = parameters.getLinkProperties();

		PocketTemplateV2 template = SchematicV2Handler.getInstance().getTemplates().get(templateID);
		if (template == null) throw new RuntimeException("Pocket template of id " + templateID + " not found!");
		LOGGER.info("Generating pocket from template " + template.getId() + " at virtual location " + virtualLocation);

		Pocket pocket = DimensionalRegistry.getPocketDirectory(world.getRegistryKey()).newPocket();
		template.place(pocket);
		template.setup(pocket, linkTo, linkProperties);
		pocket.virtualLocation = virtualLocation;
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
	public VirtualPocketType<? extends VirtualPocket> getType() {
		return VirtualPocketType.SCHEMATIC;
	}
}
