package org.dimdev.dimdoors.pockets.generator;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.chunk.Chunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.pockets.PocketLoader;
import org.dimdev.dimdoors.pockets.PocketTemplate;
import org.dimdev.dimdoors.pockets.modifier.AbsoluteRiftBlockEntityModifier;
import org.dimdev.dimdoors.pockets.modifier.RiftManager;
import org.dimdev.dimdoors.util.BlockPlacementType;
import org.dimdev.dimdoors.util.PocketGenerationParameters;
import org.dimdev.dimdoors.util.schematic.Schematic;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.type.LazyGenerationPocket;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchematicGenerator extends LazyPocketGenerator{
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
	private BlockPlacementType placementType = BlockPlacementType.SECTION_NO_UPDATE;

	private final List<RiftBlockEntity> rifts = new ArrayList<>();
	private BlockPos origin;

	private AbsoluteRiftBlockEntityModifier queuedRiftBlockEntities;

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
	public void generateChunk(LazyGenerationPocket pocket, Chunk chunk) {
		PocketTemplate template = PocketLoader.getInstance().getTemplates().get(templateID);
		if (template == null) throw new RuntimeException("Pocket template of id " + templateID + " not found!");
		template.place(pocket, chunk, origin, placementType);
		setupChunk(pocket, chunk, isSetupLoot());

		super.generateChunk(pocket, chunk);
	}

	@Override
	public PocketGenerator fromTag(CompoundTag tag) {
		super.fromTag(tag);

		this.id = tag.getString("id"); // TODO: should we force having the "dimdoors:" in the json?
		this.templateID = new Identifier("dimdoors", id);
		if (tag.contains("origin", NbtType.INT_ARRAY)) {
			int[] originInts = tag.getIntArray("origin");
			this.origin = new BlockPos(originInts[0], originInts[1], originInts[2]);
		}
		if (tag.contains("placement_type", NbtType.STRING)) placementType = BlockPlacementType.getFromId(tag.getString("placement_type"));

		return this;
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);

		tag.putString("id", this.id);
		if (placementType != BlockPlacementType.SECTION_NO_UPDATE) tag.putString("placement_type", placementType.getId());

		if (origin != null) tag.putIntArray("origin", new int[]{origin.getX(), origin.getY(), origin.getZ()});

		return tag;
	}

	@Override
	public RiftManager getRiftManager(Pocket pocket) {
		RiftManager manager = super.getRiftManager(pocket);

		rifts.forEach(manager::add);

		return manager;
	}

	@Override
	public LazyPocketGenerator cloneWithLazyModifiers(BlockPos originalOrigin) {
		LazyPocketGenerator generator = super.cloneWithLazyModifiers(originalOrigin);
		generator.lazyModifierList.add(0, queuedRiftBlockEntities);

		return generator;
	}

	@Override
	public LazyPocketGenerator cloneWithEmptyModifiers(BlockPos originalOrigin) {
		SchematicGenerator generator = (SchematicGenerator) super.cloneWithEmptyModifiers(originalOrigin);

		generator.id = id;
		generator.templateID = templateID;
		generator.origin = originalOrigin;

		return generator;
	}

	@Override
	public LazyPocketGenerator getNewInstance() {
		return new SchematicGenerator();
	}

	@Override
	public Pocket prepareAndPlacePocket(PocketGenerationParameters parameters, Pocket.PocketBuilder<?, ?> builder) {
		ServerWorld world = parameters.getWorld();
		Map<String, Double> variableMap = parameters.toVariableMap(new HashMap<>());

		PocketTemplate template = PocketLoader.getInstance().getTemplates().get(templateID);
		if (template == null) throw new RuntimeException("Pocket template of id " + templateID + " not found!");

		Pocket pocket = DimensionalRegistry.getPocketDirectory(world.getRegistryKey()).newPocket(builder);
		LOGGER.info("Generating pocket from template " + templateID + " at location " + pocket.getOrigin());

		if (pocket instanceof LazyGenerationPocket) {
			Map<BlockPos, RiftBlockEntity> absoluteRifts = template.getAbsoluteRifts(pocket);
			rifts.addAll(absoluteRifts.values());

			queuedRiftBlockEntities = new AbsoluteRiftBlockEntityModifier(absoluteRifts);
		} else {
			template.place(pocket, placementType);
		}

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
		PocketTemplate template = PocketLoader.getInstance().getTemplates().get(templateID);
		if (template == null) throw new RuntimeException("Pocket template of id " + templateID + " not found!");
		Schematic schem = template.getSchematic();
		return new Vec3i(schem.getWidth(), schem.getHeight(), schem.getLength());
	}
}
