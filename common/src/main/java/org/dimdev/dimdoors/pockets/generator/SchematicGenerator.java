package org.dimdev.dimdoors.pockets.generator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.BlockPlacementType;
import org.dimdev.dimdoors.api.util.Path;
import org.dimdev.dimdoors.api.util.math.Equation;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.command.PocketCommand;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.PocketLoader;
import org.dimdev.dimdoors.pockets.PocketTemplate;
import org.dimdev.dimdoors.pockets.modifier.Modifier;
import org.dimdev.dimdoors.pockets.modifier.RiftManager;
import org.dimdev.dimdoors.util.schematic.Schematic;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.type.LazyGenerationPocket;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchematicGenerator extends LazyPocketGenerator {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final String KEY = "schematic";

    private ResourceLocation id;
	private BlockPlacementType placementType = BlockPlacementType.SECTION_NO_UPDATE;

	private final List<RiftBlockEntity> rifts = new ArrayList<>();
	private BlockPos origin;

//	private AbsoluteRiftBlockEntityModifier queuedRiftBlockEntities; //TODO: Figure out if needed.


	public SchematicGenerator(CompoundTag builder, Equation weight, Boolean setupLoot, List<Modifier> modifierList, List<String> tags, ResourceLocation id, BlockPos pos, BlockPlacementType blockPlacementType) {
        super(builder, weight, setupLoot, modifierList, tags);
		this.id = id;
		this.origin = pos;
		placementType = blockPlacementType;
	}

	public ResourceLocation getId() {
		return this.id;
	}

	@Override
	public void generateChunk(LazyGenerationPocket pocket, LevelChunk chunk) {
		PocketTemplate template = PocketLoader.getInstance().getTemplates().get(Path.stringPath(id));
		if (template == null) throw new RuntimeException("Pocket template of id " + id + " not found!");
		template.place(pocket, chunk, origin, placementType);
		setupChunk(pocket, chunk, isSetupLoot());

		super.generateChunk(pocket, chunk);
	}

	public static final MapCodec<SchematicGenerator> CODEC = RecordCodecBuilder.<SchematicGenerator>mapCodec(instance -> commonFields(instance).and(
			Codec.STRING.xmap(DimensionalDoors::id, ResourceLocation::getPath).fieldOf("id").forGetter(a -> a.id)).and(
			BlockPos.CODEC.optionalFieldOf("origin", null).forGetter(a -> a.origin)).and(
			BlockPlacementType.CODEC.optionalFieldOf("placement_type", BlockPlacementType.SECTION_NO_UPDATE).forGetter(a -> a.placementType))
			.apply(instance, SchematicGenerator::new)
	);

	@Override
	public RiftManager getRiftManager(Pocket pocket) {
		RiftManager manager = super.getRiftManager(pocket);

		rifts.forEach(manager::add);

		return manager;
	}

	@Override
	public LazyPocketGenerator cloneWithLazyModifiers(BlockPos originalOrigin) {
		LazyPocketGenerator generator = super.cloneWithLazyModifiers(originalOrigin);
//		generator.lazyModifierList.add(0, queuedRiftBlockEntities);

		return generator;
	}

	@Override
	public LazyPocketGenerator cloneWithEmptyModifiers(BlockPos originalOrigin) {
		SchematicGenerator generator = (SchematicGenerator) super.cloneWithEmptyModifiers(originalOrigin);

		generator.id = id;
		generator.origin = originalOrigin;

		return generator;
	}

	@Override
	public LazyPocketGenerator getNewInstance() {
		return new SchematicGenerator(builder, weight, setupLoot, modifierList, tags, id, origin, placementType);
	}

	@Override
	public Pocket prepareAndPlacePocket(PocketGenerationContext parameters, Pocket.PocketBuilder<?, ?> builder) {
		ServerLevel world = parameters.world();
		Map<String, Double> variableMap = parameters.toVariableMap(new HashMap<>());

		PocketTemplate template = PocketLoader.getInstance().getTemplates().get(Path.stringPath(id));
		if (template == null) throw new RuntimeException("Pocket template of id " + id + " not found!");

		Pocket pocket = DimensionalRegistry.getPocketDirectory(world.dimension()).newPocket(builder);
		BlockPos origin = pocket.getOrigin();
		LOGGER.info("Generating pocket from template " + id + " at location " + origin);
		PocketCommand.logSetting.values().forEach(commandSource -> commandSource.sendSuccess(() -> Component.translatable("commands.pocket.log.creation.generating", id, origin.getX(), origin.getY(), origin.getZ()), false));


		if (pocket instanceof LazyGenerationPocket) {
			Map<BlockPos, RiftBlockEntity> absoluteRifts = template.getAbsoluteRifts(pocket);
			rifts.addAll(absoluteRifts.values());

//			queuedRiftBlockEntities = new AbsoluteRiftBlockEntityModifier(absoluteRifts); TODO: Marking so not forgetten when evauluating AbsoluteRifBlockEntityModifier

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
		PocketTemplate template = PocketLoader.getInstance().getTemplates().get(Path.stringPath(id));
		if (template == null) throw new RuntimeException("Pocket template of id " + id + " not found!");
		Schematic schem = template.getSchematic();
		return new Vec3i(schem.getWidth(), schem.getHeight(), schem.getLength());
	}
}
