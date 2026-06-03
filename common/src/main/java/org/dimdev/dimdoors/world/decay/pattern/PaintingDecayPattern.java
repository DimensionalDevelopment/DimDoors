package org.dimdev.dimdoors.world.decay.pattern;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.decoration.painting.Painting;
import net.minecraft.world.entity.decoration.painting.PaintingVariant;
import org.dimdev.dimdoors.util.CodecUtils;
import org.dimdev.dimdoors.world.decay.Decay;

import java.util.Collection;
import java.util.stream.Stream;

public record PaintingDecayPattern(CodecUtils.TagOrElementLocation<PaintingVariant> from, ResourceKey<PaintingVariant> to) implements DecayPattern {
    public static final String KEY = "painting";

    public static final MapCodec<PaintingDecayPattern> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            CodecUtils.TagOrElementLocation.codec(Registries.PAINTING_VARIANT).fieldOf("from").forGetter(PaintingDecayPattern::from),
            ResourceKey.codec(Registries.PAINTING_VARIANT).fieldOf("to").forGetter(PaintingDecayPattern::to)
    ).apply(instance, PaintingDecayPattern::new));

    @Override
    public DecayPatternType<? extends DecayPattern> getType() {
        return DecayPatternType.PAINTING;
    }

    @Override
    public boolean test(Decay.DecayContext context) {
        return context.targetEntity() instanceof Painting painting && from.test(painting.getVariant());
    }

    @Override
    public int process(Decay.DecayContext context) {
        if(context.targetEntity() instanceof Painting painting) {
            painting.setVariant(context.world().registryAccess().lookupOrThrow(Registries.PAINTING_VARIANT).get(to).orElseThrow());
            return 1;
        }
        return 0;
    }

    @Override
    public Stream<ResourceKey<?>> constructApplicable(RegistryAccess access) {
        return access.lookup(Registries.PAINTING_VARIANT).map(from::getValues).stream().flatMap(Collection::stream);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder implements DecayPattern.Builder<PaintingDecayPattern> {
        private CodecUtils.TagOrElementLocation<PaintingVariant> from;
        private ResourceKey<PaintingVariant> to;

        public Builder from(TagKey<PaintingVariant> tag) {
            from = CodecUtils.TagOrElementLocation.of(tag, Registries.PAINTING_VARIANT);
            return this;
        }

        public Builder from(ResourceKey<PaintingVariant> key) {
            from = CodecUtils.TagOrElementLocation.of(key, Registries.PAINTING_VARIANT);
            return this;
        }

        public Builder to(ResourceKey<PaintingVariant> key) {
            to = key;
            return this;
        }

        public PaintingDecayPattern build(HolderLookup.Provider provider) {
            return new PaintingDecayPattern(from, to);
        }
    }
}
