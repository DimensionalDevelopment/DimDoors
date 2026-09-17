package org.dimdev.dimdoors.mixin;

import com.mojang.serialization.MapDecoder;
import com.mojang.serialization.MapEncoder;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.dimdev.dimdoors.util.RecordCodecBuilderExt;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Function;

@Mixin(value = RecordCodecBuilder.class, remap = false)
public abstract class RecordCodecBuilderMixin<O, F> implements RecordCodecBuilderExt<O, F> {
    @Shadow @Final private MapDecoder<F> decoder;
    @Shadow @Final private Function<O, MapEncoder<F>> encoder;
    @Shadow @Final private Function<O, F> getter;

    @Override
    public MapDecoder<F> dimdoors$getDecoder() {
        return this.decoder;
    }

    @Override
    public Function<O, MapEncoder<F>> dimdoors$getEncoder() {
        return encoder;
    }

    @Override
    public Function<O, F> dimdoors$getGetter() {
        return getter;
    }
}
