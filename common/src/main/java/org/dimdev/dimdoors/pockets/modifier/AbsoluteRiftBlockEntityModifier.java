package org.dimdev.dimdoors.pockets.modifier;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.BlockBoxUtil;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.world.pocket.type.LazyGenerationPocket;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.Map;
import java.util.stream.Collectors;

public class AbsoluteRiftBlockEntityModifier extends AbstractLazyModifier {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final String KEY = "block_entity";

	private Map<BlockPos, RiftBlockEntity> rifts;
	private Map<BlockPos, CompoundTag> serializedRifts;

	public AbsoluteRiftBlockEntityModifier() {
	}

	public AbsoluteRiftBlockEntityModifier(Map<BlockPos, RiftBlockEntity> rifts) {
		this.rifts = rifts;

	}

	@Override
	public Modifier fromNbt(CompoundTag nbt, ResourceManager manager) {
		// TODO: rifts from resource
		serializedRifts = nbt.getList("rifts", Tag.TAG_COMPOUND).parallelStream().unordered().map(CompoundTag.class::cast)
				.filter(compound -> {
					if (compound.contains("Pos")) {
						return true;
					}
					LOGGER.error("Discarding rift on deserialization since \"Pos\" tag was not set.");
					return false;
				})
				.collect(Collectors.toConcurrentMap(compound -> {
					int[] ints = compound.getIntArray("Pos");
					return new BlockPos(ints[0], ints[1], ints[2]);
				}, compound -> compound));

		return this;
	}

	@Override
	public CompoundTag toNbtInternal(CompoundTag nbt, boolean allowResource) {
		super.toNbtInternal(nbt, allowResource);

		ListTag riftsNbt;
		if (rifts != null) {
			riftsNbt = rifts.values().parallelStream().unordered().map(rift -> {
				CompoundTag e = new CompoundTag();
				rift.saveAdditional(e);
				return e;
			}).collect(Collectors.toCollection(ListTag::new));
		} else {
			riftsNbt = new ListTag();
			riftsNbt.addAll(serializedRifts.values());
		}
		nbt.put("rifts", riftsNbt);

		return nbt;
	}

	@Override
	public ModifierType<? extends Modifier> getType() {
		return ModifierType.ABSOLUTE_RIFT_BLOCK_ENTITY_MODIFIER_TYPE.get();
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public void apply(PocketGenerationContext parameters, RiftManager manager) {
		if (!manager.isPocketLazy()) { // rifts is guaranteed to exist at this stage since this modifier is not supposed to be loaded from json
			ServerLevel world = DimensionalDoors.getWorld(manager.getPocket().getWorld());
			rifts.values().forEach(world::setBlockEntity);
		}
	}

	@Override
	public void apply(PocketGenerationContext parameters, Pocket.PocketBuilder<?, ?> builder) {

	}

	@Override
	public void applyToChunk(LazyGenerationPocket pocket, ChunkAccess chunk) {
		BoundingBox chunkBox = BlockBoxUtil.getBox(chunk);

		if (rifts != null) {
			rifts.entrySet().stream().unordered().filter(entry -> chunkBox.isInside(entry.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
					.forEach((pos, rift) -> {
						rifts.remove(pos);
						chunk.setBlockEntity(rift);
					});
		} else {
			serializedRifts.entrySet().stream().unordered().filter(entry -> chunkBox.isInside(entry.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
					.forEach((pos, riftNbt) -> {
						rifts.remove(pos);
						chunk.setBlockEntity(BlockEntity.loadStatic(pos, chunk.getBlockState(pos), riftNbt));
					});
		}
	}
}
