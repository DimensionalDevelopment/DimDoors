package org.dimdev.dimdoors.pockets.modifier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.StreamUtils;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.Map;
import java.util.stream.Collectors;

public class AbsoluteRiftBlockEntityModifier extends AbstractModifier {
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
    public Modifier fromNbt(CompoundTag nbt, HolderLookup.Provider provider, ResourceManager manager) {
    // TODO: rifts from resource
    serializedRifts = StreamUtils.execute(() -> nbt.getList("rifts", Tag.TAG_COMPOUND).parallelStream().unordered().map(CompoundTag.class::cast)
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
        }, compound -> compound)));

    return this;
    }

    @Override
    public CompoundTag toNbtInternal(CompoundTag nbt, HolderLookup.Provider provider, boolean allowResource) {
    super.toNbtInternal(nbt, provider, allowResource);

    ListTag riftsNbt;
    if (rifts != null) {
        riftsNbt = StreamUtils.execute(() -> rifts.values().parallelStream().unordered().map(rift -> {
        CompoundTag e = new CompoundTag();
        rift.saveAdditional(e, provider);
        return e;
        }).collect(Collectors.toCollection(ListTag::new)));
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
        ServerLevel world = DimensionalDoors.getWorld(manager.getPocket().getWorld());
        rifts.values().forEach(world::setBlockEntity);
    }

    @Override
    public void apply(PocketGenerationContext parameters, Pocket.PocketBuilder<?, ?> builder) {

    }

}