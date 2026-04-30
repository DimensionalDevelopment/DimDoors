package org.dimdev.dimdoors.world.decay.conditions;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import org.dimdev.dimdoors.util.CodecUtils;
import org.dimdev.dimdoors.world.decay.Decay;

import java.util.function.Supplier;

public class SimpleDecayCondition extends GenericDecayCondition<Block> {
    public static final MapCodec<SimpleDecayCondition> CODEC = CodecUtils.createCodec(SimpleDecayCondition::new, Registries.BLOCK);

    public static final String KEY = "block";

    public SimpleDecayCondition(CodecUtils.TagOrElementLocation<Block> tagOrElementLocation, boolean invert) {
        super(tagOrElementLocation, invert);
    }

    public static SimpleDecayCondition of(TagKey<Block> tag, boolean invert) {
        return new SimpleDecayCondition(CodecUtils.TagOrElementLocation.of(tag, Registries.BLOCK), invert);
    }

    public static SimpleDecayCondition of(TagKey<Block> tag) {
        return new SimpleDecayCondition(CodecUtils.TagOrElementLocation.of(tag, Registries.BLOCK), false);
    }

    public static SimpleDecayCondition of(ResourceKey<Block> key, boolean invert) {
        return new SimpleDecayCondition(CodecUtils.TagOrElementLocation.of(key, Registries.BLOCK), invert);
    }

    public static SimpleDecayCondition of(ResourceKey<Block> key) {
        return of(key, false);
    }

    public static SimpleDecayCondition of(Block block, boolean invert) {
        return of(block.builtInRegistryHolder().key(), invert);
    }

    public static SimpleDecayCondition of(Block block) {
        return of(block, false);
    }

    public static SimpleDecayCondition of(Supplier<Block> block, boolean invert) {
        return of(block.get(), invert);
    }

    public static SimpleDecayCondition of(Supplier<Block> block) {
        return of(block, false);
    }




    @Override
    public DecayConditionType<? extends DecayCondition> getType() {
        return DecayConditionType.SIMPLE_CONDITION_TYPE;
    }

    @Override
    public Holder<Block> getHolder(Decay.DecayContext context) {
        return context.targetBlockState().getBlockHolder();
    }

    @Override
    public ResourceKey<Registry<Block>> registry() {
        return Registries.BLOCK;
    }
}
