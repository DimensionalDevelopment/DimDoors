package org.dimdev.dimdoors.world.pocket.type;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.PocketDirectory;

public class IdReferencePocket extends AbstractPocket<IdReferencePocket> {
    public static String KEY = "id_reference";

    protected int referencedId;

    @Override
    public IdReferencePocket fromNbt(CompoundTag nbt, HolderLookup.Provider provider) {
    super.fromNbt(nbt, provider);

    this.referencedId = nbt.getInt("referenced_id");

    return this;
    }

    @Override
    public CompoundTag toNbt(CompoundTag nbt, HolderLookup.Provider provider) {
    nbt = super.toNbt(nbt, provider);

    nbt.putInt("referenced_id", referencedId);

    return nbt;
    }

    @Override
    public AbstractPocketType<IdReferencePocket> getType() {
    return AbstractPocketType.ID_REFERENCE;
    }

    @Override
    public Pocket getReferencedPocket() {
    return getReferencedPocket(DimensionalRegistry.getPocketDirectory(getWorld()));
    }

    @Override
    public Pocket getReferencedPocket(PocketDirectory directory) {
    return directory.getPocket(referencedId);
    }

    public int getReferencedId() {
    return referencedId;
    }

    public static IdReferencePocketBuilder builder() {
    return new IdReferencePocketBuilder(AbstractPocketType.ID_REFERENCE);
    }

    public static class IdReferencePocketBuilder extends AbstractPocketBuilder<IdReferencePocketBuilder, IdReferencePocket> {


    private int referencedId = Integer.MIN_VALUE;

    protected IdReferencePocketBuilder(AbstractPocketType<IdReferencePocket> type) {
        super(type);
    }

    @Override
    public IdReferencePocket build() {
        IdReferencePocket pocket = super.build();
        pocket.referencedId = referencedId;
        return pocket;
    }

    @Override
    public IdReferencePocketBuilder fromNbt(CompoundTag nbt, HolderLookup.Provider provider) {
        if (nbt.contains("referenced_id", Tag.TAG_INT)) referencedId = nbt.getInt("referenced_id");
        return this;
    }

    @Override
    public CompoundTag toNbt(CompoundTag nbt, HolderLookup.Provider provider) {
        if (referencedId != Integer.MIN_VALUE) nbt.putInt("referenced_id", referencedId);
        return nbt;
    }

        @Override
        public AbstractPocketType<?> getType() {
            return AbstractPocketType.ID_REFERENCE;
        }

        public IdReferencePocketBuilder referencedId(int referencedId) {
        this.referencedId = referencedId;
        return this;
    }
    }
}
