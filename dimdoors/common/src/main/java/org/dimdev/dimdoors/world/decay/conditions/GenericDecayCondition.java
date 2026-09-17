package org.dimdev.dimdoors.world.decay.conditions;

import net.minecraft.core.*;
import net.minecraft.resources.ResourceKey;
import org.dimdev.dimdoors.util.CodecUtils;
import org.dimdev.dimdoors.world.decay.Decay;

import java.util.Collection;
import java.util.stream.Stream;

public abstract class GenericDecayCondition<T> implements DecayCondition, Applicator<T> {

    private final CodecUtils.TagOrElementLocation<T> tagOrElementLocation;
    private final boolean invert;


    public GenericDecayCondition(CodecUtils.TagOrElementLocation<T> tagOrElementLocation, boolean invert) {
        this.tagOrElementLocation = tagOrElementLocation;
        this.invert = invert;
    }

    public CodecUtils.TagOrElementLocation<T> getTagOrElementLocation() {
        return tagOrElementLocation;
    }

    @Override
    public boolean test(Decay.DecayContext context) {
        return invert != tagOrElementLocation.test(getHolder(context));
    }

    public abstract Holder<T> getHolder(Decay.DecayContext context);

    public boolean invert() {
        return invert;
    }

    @Override
    public Stream<ResourceKey<T>> constructApplicable(RegistryAccess access) {
        return access.lookup(registry()).map(tagOrElementLocation::getValues).stream().flatMap(Collection::stream);
    }

}

