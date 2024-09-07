package org.dimdev.dimdoors.world.decay.conditions;

import com.google.common.collect.Streams;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.dimdev.dimdoors.world.decay.DecayCondition;
import org.dimdev.dimdoors.world.decay.DecaySource;

import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public abstract class GenericDecayCondition<T> implements DecayCondition {
    public static <T extends GenericDecayCondition<?>, V> Codec<T> createCodec(BiFunction<TagOrElementLocation<V>, Boolean, T> function, ResourceKey<? extends Registry<V>> key) {
        Codec<TagOrElementLocation<V>> codec = Codec.STRING.comapFlatMap(string -> string.startsWith("#") ? ResourceLocation.read(string.substring(1)).map(resourceLocation -> new TagOrElementLocation<>(resourceLocation, true, key)) : ResourceLocation.read(string).map(resourceLocation -> new TagOrElementLocation<V>(resourceLocation, false, key)), TagOrElementLocation::decoratedId);

        return RecordCodecBuilder.create(instance -> instance.group(codec.fieldOf("entry").forGetter(t -> (TagOrElementLocation<V>) t.getTagOrElementLocation()),
                Codec.BOOL.optionalFieldOf("invert", false).forGetter(GenericDecayCondition::invert)).apply(instance, function));
    }

    private final TagOrElementLocation<T> tagOrElementLocation;
    private final boolean invert;


    public GenericDecayCondition(TagOrElementLocation<T> tagOrElementLocation, boolean invert) {
        this.tagOrElementLocation = tagOrElementLocation;
        this.invert = invert;
    }

    public TagOrElementLocation<T> getTagOrElementLocation() {
        return tagOrElementLocation;
    }

    @Override
    public boolean test(Level world, BlockPos pos, BlockState origin, BlockState targetBlock, FluidState targetFluid, DecaySource source) {
        return tagOrElementLocation.test(getHolder(world, pos, origin, targetBlock, targetFluid, source));
    }

    public abstract Holder<T> getHolder(Level world, BlockPos pos, BlockState origin, BlockState targetBlock, FluidState targetFluid, DecaySource source);

    public boolean invert() {
        return invert;
    }

    public static final class TagOrElementLocation<T> {

        private TagKey<T> tag;
        private ResourceKey<T> key;

        public static <T> TagOrElementLocation<T> of(TagKey<T> tag, ResourceKey<? extends Registry<T>> registry) {
            return new TagOrElementLocation<>(tag.location(), true, registry);
        }

        public static <T> TagOrElementLocation<T> of(ResourceKey<T> tag, ResourceKey<? extends Registry<T>> registry) {
            return new TagOrElementLocation<>(tag.location(), false, registry);
        }

        public TagOrElementLocation(ResourceLocation id, boolean tag, ResourceKey<? extends Registry<T>> registryResourceKey) {
            if(tag) this.tag = TagKey.create(registryResourceKey, id);
            else this.key = ResourceKey.create(registryResourceKey, id);
        }

        @Override
            public String toString() {
                return this.decoratedId();
        }

        private String decoratedId() {
            return this.tag != null ? "#" + tag.location() : this.key.location().toString();
        }

        public boolean test(Holder<T> holder) {
            return tag != null && holder.is(tag) || holder.is(key);
        }

        public Set<ResourceKey<T>> getValues(Registry<T> registry) {
            return key != null ? Set.of(key) : Streams.stream(registry.getTagOrEmpty(tag)).map(Holder::unwrapKey).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toSet());
        }
    }
}

