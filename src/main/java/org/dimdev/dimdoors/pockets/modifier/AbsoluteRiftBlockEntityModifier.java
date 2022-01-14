package org.dimdev.dimdoors.pockets.modifier;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.api.util.BlockBoxUtil;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.world.pocket.type.LazyGenerationPocket;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.Map;
import java.util.stream.Collectors;

public class AbsoluteRiftBlockEntityModifier implements LazyModifier {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final String KEY = "block_entity";

	private Map<BlockPos, RiftBlockEntity> rifts;
	private Map<BlockPos, NbtCompound> serializedRifts;

	public AbsoluteRiftBlockEntityModifier() {
	}

	public AbsoluteRiftBlockEntityModifier(Map<BlockPos, RiftBlockEntity> rifts) {
		this.rifts = rifts;

	}

	@Override
	public Modifier fromNbt(NbtCompound nbt) {
		serializedRifts = nbt.getList("rifts", NbtType.COMPOUND).parallelStream().unordered().map(NbtCompound.class::cast)
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
	public NbtCompound toNbt(NbtCompound nbt) {
		LazyModifier.super.toNbt(nbt);

		NbtList riftsNbt;
		if (rifts != null) {
			riftsNbt = rifts.values().parallelStream().unordered().map(rift -> {
				NbtCompound e = new NbtCompound();
				rift.writeNbt(e);
				return e;
			}).collect(Collectors.toCollection(NbtList::new));
		} else {
			riftsNbt = new NbtList();
			riftsNbt.addAll(serializedRifts.values());
		}
		nbt.put("rifts", riftsNbt);

		return nbt;
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
	public void apply(PocketGenerationContext parameters, RiftManager manager) {
		if (!manager.isPocketLazy()) { // rifts is guaranteed to exist at this stage since this modifier is not supposed to be loaded from json
			World world = DimensionalDoorsInitializer.getWorld(manager.getPocket().getWorld());
			rifts.values().forEach(world::addBlockEntity);
		}
	}

	@Override
	public void apply(PocketGenerationContext parameters, Pocket.PocketBuilder<?, ?> builder) {

	}

	@Override
	public void applyToChunk(LazyGenerationPocket pocket, Chunk chunk) {
		BlockBox chunkBox = BlockBoxUtil.getBox(chunk);

		if (rifts != null) {
			rifts.entrySet().stream().unordered().filter(entry -> chunkBox.contains(entry.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
					.forEach((pos, rift) -> {
						rifts.remove(pos);
						chunk.setBlockEntity(rift);
					});
		} else {
			serializedRifts.entrySet().stream().unordered().filter(entry -> chunkBox.contains(entry.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
					.forEach((pos, riftNbt) -> {
						rifts.remove(pos);
						chunk.setBlockEntity(BlockEntity.createFromNbt(pos, chunk.getBlockState(pos), riftNbt));
					});
		}
	}
}
