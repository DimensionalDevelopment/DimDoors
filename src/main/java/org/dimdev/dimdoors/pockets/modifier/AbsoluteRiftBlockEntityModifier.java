package org.dimdev.dimdoors.pockets.modifier;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.util.PocketGenerationParameters;
import org.dimdev.dimdoors.world.pocket.type.LazyGenerationPocket;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.Map;
import java.util.stream.Collectors;

public class AbsoluteRiftBlockEntityModifier implements LazyModifier {
	public static final String KEY = "block_entity";

	private Map<BlockPos, RiftBlockEntity> rifts;
	private Map<BlockPos, CompoundTag> serializedRifts;

	public AbsoluteRiftBlockEntityModifier() {
	}

	public AbsoluteRiftBlockEntityModifier(Map<BlockPos, RiftBlockEntity> rifts) {
		this.rifts = rifts;

	}

	@Override
	public Modifier fromTag(CompoundTag tag) {
		serializedRifts = tag.getList("rifts", NbtType.COMPOUND).parallelStream().unordered().map(CompoundTag.class::cast)
				.collect(Collectors.toConcurrentMap(compound -> {
					int[] ints = compound.getIntArray("Pos");
					return new BlockPos(ints[0], ints[1], ints[2]);
				}, compound -> compound));

		return this;
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		LazyModifier.super.toTag(tag);

		ListTag riftsTag;
		if (rifts != null) {
			riftsTag = rifts.values().parallelStream().unordered().map(rift -> rift.writeNbt(new CompoundTag())).collect(Collectors.toCollection(ListTag::new));
		} else {
			riftsTag = new ListTag();
			riftsTag.addAll(serializedRifts.values());
		}
		tag.put("rifts", riftsTag);

		return tag;
	}

	@Override
	public ModifierType<? extends Modifier> getType() {
		return ModifierType.ABSOLUTE_RIFT_BLOCK_ENTITY_MODIFIER_TYPE;
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public void apply(PocketGenerationParameters parameters, RiftManager manager) {
		if (!manager.isPocketLazy()) { // rifts is guaranteed to exist at this stage since this modifier is not supposed to be loaded from json
			World world = DimensionalDoorsInitializer.getWorld(manager.getPocket().getWorld());
			rifts.values().forEach(world::addBlockEntity);
		}
	}

	@Override
	public void apply(PocketGenerationParameters parameters, Pocket.PocketBuilder<?, ?> builder) {

	}

	@Override
	public void applyToChunk(LazyGenerationPocket pocket, Chunk chunk) {
		ChunkPos chunkPos = chunk.getPos();
		BlockBox chunkBox = BlockBox.create(chunkPos.getStartX(), chunk.getBottomY(), chunkPos.getStartZ(), chunkPos.getEndX(), chunk.getTopY(), chunkPos.getEndZ());

		if (rifts != null) {
			rifts.entrySet().stream().unordered().filter(entry -> chunkBox.contains(entry.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
					.forEach((pos, rift) -> {
						rifts.remove(pos);
						chunk.setBlockEntity(rift);
					});
		} else {
			serializedRifts.entrySet().stream().unordered().filter(entry -> chunkBox.contains(entry.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
					.forEach((pos, riftTag) -> {
						rifts.remove(pos);
						chunk.setBlockEntity(BlockEntity.createFromNbt(pos, chunk.getBlockState(pos), riftTag));
					});
		}
	}
}
