package org.dimdev.dimdoors.world.pocket.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.PocketDirectory;

public class IdReferencePocket extends AbstractPocket<IdReferencePocket, IdReferencePocket.IdReferencePocketBuilder> {
    public static final MapCodec<IdReferencePocket> CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(instance).and(Codec.INT.fieldOf("referenced_id").forGetter(IdReferencePocket::getReferencedId)).apply(instance, IdReferencePocket::new));

    public static String KEY = "id_reference";

    protected int referencedId;

    public IdReferencePocket(int id, ResourceKey<Level> world, int referencedId) {
        super(id, world);
        this.referencedId = referencedId;
    }

    public IdReferencePocket() {
        super();
    }

    @Override
    public AbstractPocketType<IdReferencePocket, IdReferencePocketBuilder> getType() {
        return AbstractPocketType.ID_REFERENCE;
    }

    @Override
    public Pocket<?, ?> getReferencedPocket() {
        return getReferencedPocket(DimensionalRegistry.getPocketDirectory(getWorld()));
    }

    @Override
    public Pocket<?, ?> getReferencedPocket(PocketDirectory directory) {
        return directory.getPocket(referencedId);
    }

    public int getReferencedId() {
        return referencedId;
    }

    public static IdReferencePocketBuilder builder() {
        return new IdReferencePocketBuilder();
    }

    public static class IdReferencePocketBuilder extends AbstractPocketBuilder<IdReferencePocket, IdReferencePocketBuilder> {
        public static final MapCodec<IdReferencePocketBuilder> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.INT.optionalFieldOf("referenced_id", Integer.MIN_VALUE).forGetter(a -> a.referencedId)
        ).apply(instance, IdReferencePocketBuilder::new));

        private int referencedId = Integer.MIN_VALUE;

        protected IdReferencePocketBuilder(int referenceId) {
            this.referencedId = referenceId;

        }

        protected IdReferencePocketBuilder() {
            super();
        }

        @Override
        public IdReferencePocket build() {
            IdReferencePocket pocket = super.build();
            pocket.referencedId = referencedId;
            return pocket;
        }

        @Override
        public AbstractPocketType<IdReferencePocket, IdReferencePocketBuilder> type() {
            return AbstractPocketType.ID_REFERENCE;
        }

        @Override
        public IdReferencePocketBuilder copy() {
            var copy = super.copy();
            copy.referencedId = referencedId;
            return copy;
        }

        @Override
        IdReferencePocketBuilder instance() {
            return new IdReferencePocketBuilder();
        }

        public IdReferencePocketBuilder referencedId(int referencedId) {
            this.referencedId = referencedId;
            return this;
        }
    }
}
