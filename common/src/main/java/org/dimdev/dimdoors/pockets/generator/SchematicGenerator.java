package org.dimdev.dimdoors.pockets.generator;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.chunk.LevelChunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.BlockPlacementType;
import org.dimdev.dimdoors.api.util.Path;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.command.PocketCommand;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.PocketLoader;
import org.dimdev.dimdoors.pockets.PocketTemplate;
import org.dimdev.dimdoors.pockets.modifier.AbsoluteRiftBlockEntityModifier;
import org.dimdev.dimdoors.pockets.modifier.RiftManager;
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

	private String id;
	private ResourceLocation templateID;
	private BlockPlacementType placementType = BlockPlacementType.SECTION_NO_UPDATE;

	private final List<RiftBlockEntity> rifts = new ArrayList<>();
	private BlockPos origin;

	private AbsoluteRiftBlockEntityModifier queuedRiftBlockEntities;

	public SchematicGenerator() {
	}

	public SchematicGenerator(String id) {
		this.id = id;

		this.templateID = DimensionalDoors.id(id);
	}

	public String getId() {
		return this.id;
	}

	public ResourceLocation getTemplateID() {
		return templateID;
	}

	@Override
	public void generateChunk(LazyGenerationPocket pocket, LevelChunk chunk) {
		PocketTemplate template = PocketLoader.getInstance().getTemplates().get(Path.stringPath(templateID));
		if (template == null) throw new RuntimeException("Pocket template of id " + templateID + " not found!");
		template.place(pocket, chunk, origin, placementType);
		setupChunk(pocket, chunk, isSetupLoot());

		super.generateChunk(pocket, chunk);
	}

	@Override
	public PocketGenerator fromNbt(CompoundTag nbt, ResourceManager manager) {
		super.fromNbt(nbt, manager);

		this.id = nbt.getString("id"); // TODO: should we force having the "dimdoors:" in the json?
		this.templateID = DimensionalDoors.id(id);
		if (nbt.contains("origin", Tag.TAG_INT_ARRAY)) {
			int[] originInts = nbt.getIntArray("origin");
			this.origin = new BlockPos(originInts[0], originInts[1], originInts[2]);
		}
		if (nbt.contains("placement_type", Tag.TAG_STRING)) placementType = BlockPlacementType.getFromId(nbt.getString("placement_type"));

		return this;
	}

	@Override
	public CompoundTag toNbtInternal(CompoundTag nbt, boolean allowReference) {
		super.toNbtInternal(nbt, allowReference);

		nbt.putString("id", this.id);
		if (placementType != BlockPlacementType.SECTION_NO_UPDATE) nbt.putString("placement_type", placementType.getId());

		if (origin != null) nbt.putIntArray("origin", new int[]{origin.getX(), origin.getY(), origin.getZ()});

		return nbt;
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
	public Pocket prepareAndPlacePocket(PocketGenerationContext parameters, Pocket.PocketBuilder<?, ?> builder) {
		ServerLevel world = parameters.world();
		Map<String, Double> variableMap = parameters.toVariableMap(new HashMap<>());

		PocketTemplate template = PocketLoader.getInstance().getTemplates().get(Path.stringPath(templateID));
		if (template == null) throw new RuntimeException("Pocket template of id " + templateID + " not found!");

		Pocket pocket = DimensionalRegistry.getPocketDirectory(world.dimension()).newPocket(builder);
		BlockPos origin = pocket.getOrigin();
		LOGGER.info("Generating pocket from template " + templateID + " at location " + origin);
		PocketCommand.logSetting.values().forEach(commandSource -> commandSource.sendSuccess(() -> Component.translatable("commands.pocket.log.creation.generating", templateID, origin.getX(), origin.getY(), origin.getZ()), false));


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
		return PocketGeneratorType.SCHEMATIC.get();
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public Vec3i getSize(PocketGenerationContext parameters) {
		PocketTemplate template = PocketLoader.getInstance().getTemplates().get(Path.stringPath(templateID));
		if (template == null) throw new RuntimeException("Pocket template of id " + templateID + " not found!");
		Schematic schem = template.getSchematic();
		return new Vec3i(schem.getWidth(), schem.getHeight(), schem.getLength());
	}
}
