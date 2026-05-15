package org.dimdev.dimdoors.pockets.virtual.reference;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import org.dimdev.dimdoors.ModRegistryKeys;
import org.dimdev.dimdoors.api.util.math.Equation;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.generator.PocketGenerator;

import java.util.Optional;

public class IdReference extends PocketGeneratorReference<IdReference> {
    public static final MapCodec<IdReference> CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(instance).and(ResourceKey.codec(ModRegistryKeys.POCKET_GENERATOR).fieldOf("id").forGetter(a -> a.id)).apply(instance, IdReference::new));
    public static final String KEY = "id";
    private final ResourceKey<PocketGenerator<?>> id;

    public IdReference(Optional<Equation> weight, ResourceKey<PocketGenerator<?>> id) {
        super(weight);
        this.id = id;
    }

    @Override
    public Holder<PocketGenerator<?>> peekReferencedPocketGenerator(PocketGenerationContext parameters) {
        return getReferencedPocketGenerator(parameters);
    }

    @Override
    public Holder<PocketGenerator<?>> getReferencedPocketGenerator(PocketGenerationContext parameters) {
        return parameters.lookupHolder(id);
    }

    @Override
    public VirtualPocketType<IdReference> getType() {
        return VirtualPocketType.ID_REFERENCE;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("weight", weight.asString())
                .toString();
    }
}