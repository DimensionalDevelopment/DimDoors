package org.dimdev.dimdoors.pockets.virtual;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.packs.resources.ResourceManager;
import org.dimdev.dimdoors.api.util.ResourceUtil;
import org.dimdev.dimdoors.api.util.WeightedList;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.virtual.reference.PocketGeneratorReference;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.jetbrains.annotations.Nullable;

public class VirtualPocketList extends WeightedList<VirtualPocket, PocketGenerationContext> implements VirtualPocket {
    private String resourceKey = null;

    public static VirtualPocketList deserialize(Tag nbt, HolderLookup.Provider provider, @Nullable ResourceManager manager) {
    return switch (nbt.getId()) {
        case Tag.TAG_LIST -> deserialize((ListTag) nbt, provider, manager);
        case Tag.TAG_STRING ->
            ResourceUtil.loadReferencedResource(manager, RESOURCE_STARTING_PATH, nbt.getAsString(), ResourceUtil.NBT_READER.andThenComposable(nbtElement -> deserialize(nbtElement, provider, manager)));
        default -> throw new RuntimeException(String.format("Unexpected NbtType %d!", nbt.getId()));
    };
    }

    public static VirtualPocketList deserialize(Tag nbt, HolderLookup.Provider provider) {
    return deserialize(nbt, provider, null);
    }

    public static VirtualPocketList deserialize(ListTag nbt, HolderLookup.Provider provider, @Nullable ResourceManager manager) {
    return new VirtualPocketList().fromNbt(nbt, provider, manager);
    }

    public static VirtualPocketList deserialize(ListTag nbt, HolderLookup.Provider provider) {
    return deserialize(nbt, provider, null);
    }

    @Override
    public void setResourceKey(String resourceKey) {
    this.resourceKey = resourceKey;
    }

    @Override
    public String getResourceKey() {
    return this.resourceKey;
    }

    public static Tag serialize(VirtualPocketList virtualPocketList, HolderLookup.Provider provider, boolean allowReference) {
    return virtualPocketList.toNbt(new ListTag(), provider, allowReference);
    }

    public static Tag serialize(VirtualPocketList virtualPocket, HolderLookup.Provider provider) {
    return serialize(virtualPocket, provider, false);
    }

    public VirtualPocketList() {
    super();
    }

    public VirtualPocketList fromNbt(ListTag nbt, HolderLookup.Provider provider, ResourceManager manager) { // Keep in mind, this would add onto the list instead of overwriting it if called multiple times.
    for (Tag value : nbt) {
        this.add(VirtualPocket.deserialize(value, provider, manager));
    }
    return this;
    }

    public VirtualPocketList fromNbt(ListTag nbt, HolderLookup.Provider provider) {
    return fromNbt(nbt, provider, null);
    }

    public Tag toNbt(ListTag nbt, HolderLookup.Provider provider, boolean allowReference) {
    if (allowReference && resourceKey != null) {
        return StringTag.valueOf(resourceKey);
    }
    for(VirtualPocket virtualPocket : this) {
        nbt.add(VirtualPocket.serialize(virtualPocket, provider, allowReference));
    }
    return nbt;
    }

    public Tag toNbt(ListTag nbt, HolderLookup.Provider provider) {
    return toNbt(nbt, provider, false);
    }

    @Override
    public Pocket prepareAndPlacePocket(PocketGenerationContext context) {
    return getNextPocketGeneratorReference(context).prepareAndPlacePocket(context);
    }

    @Override
    public Pocket prepareAndPlacePocket(PocketGenerationContext context, Boolean setupLoot) {
        return getNextPocketGeneratorReference(context).prepareAndPlacePocket(context, setupLoot);
    }

    public PocketGeneratorReference getNextPocketGeneratorReference(PocketGenerationContext context) {
    return getNextRandomWeighted(context).getNextPocketGeneratorReference(context);
    }

    public PocketGeneratorReference peekNextPocketGeneratorReference(PocketGenerationContext context) {
    return peekNextRandomWeighted(context).peekNextPocketGeneratorReference(context);
    }

    @Override
    public void init() {
    this.forEach(VirtualPocket::init);
    }

    @Override
    public double getWeight(PocketGenerationContext context) {
    return getTotalWeight(context);
    }
}