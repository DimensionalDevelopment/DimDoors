package org.dimdev.dimdoors.pockets.virtual.reference;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import org.dimdev.dimdoors.ModRegistryKeys;
import org.dimdev.dimdoors.api.util.HolderWeightedList;
import org.dimdev.dimdoors.api.util.math.Equation;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.generator.PocketGenerator;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TagReference extends PocketGeneratorReference<TagReference> {
    public static final MapCodec<TagReference> CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(instance)
            .and(Codec.list(Codec.STRING).optionalFieldOf("required", List.of()).forGetter(a -> a.required))
            .and(Codec.list(Codec.STRING).optionalFieldOf("blackList", List.of()).forGetter(a -> a.blackList))
            .and(Codec.BOOL.optionalFieldOf("exact", false).forGetter(a -> a.exact)
            ).apply(instance, TagReference::new)
    );

    public static final String KEY = "tag";

    private final List<String> required;
    private final List<String> blackList;
    private final Boolean exact;

    private HolderWeightedList<PocketGenerator<?>, PocketGenerationContext> pockets;

    public TagReference(Equation weight, List<String> required, List<String> blackList, boolean exact) {
        super(weight);
        this.required = required;
        this.blackList = blackList;
        this.exact = exact;
    }


    @Override
    public VirtualPocketType<TagReference> getType() {
        return VirtualPocketType.TAG_REFERENCE;
    }

    // TODO: this will break if pockets change in between (which they could if we add a tool for creating pocket json config stuff ingame)
    @Override
    public Holder<PocketGenerator<?>> peekReferencedPocketGenerator(PocketGenerationContext parameters) {
        return selectPocket(parameters, true);
    }

    @Override
    public Holder<PocketGenerator<?>> getReferencedPocketGenerator(PocketGenerationContext parameters) {
        return selectPocket(parameters, false);
    }

    private Holder<PocketGenerator<?>> selectPocket(PocketGenerationContext parameters, boolean peek) {
        if (pockets == null) pockets = getPocketsMatchingTags(parameters.provider().lookupOrThrow(ModRegistryKeys.POCKET_GENERATOR).listElements(), required, blackList, exact != null && exact);
        return peek ? pockets.peekNextRandomWeighted(parameters) : pockets.getNextRandomWeighted(parameters);
    }

    public static HolderWeightedList<PocketGenerator<?>, PocketGenerationContext> getPocketsMatchingTags(Stream<Holder.Reference<PocketGenerator<?>>> references, List<String> required, List<String> blackList, boolean exact) {
        return new HolderWeightedList<>(references.filter(pocketGenerator -> pocketGenerator.value().checkTags(required, blackList, exact)).collect(Collectors.toList()));
    }


    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("weight", weight.asString())
                .add("required", required)
                .add("blackList", blackList)
                .add("exact", exact)
                .add("pockets", pockets)
                .toString();
    }
}