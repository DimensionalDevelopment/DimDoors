package org.dimdev.dimdoors.world.decay.conditions;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.dimdev.dimdoors.world.decay.DecayCondition;
import org.dimdev.dimdoors.world.decay.DecayConditionType;
import org.dimdev.dimdoors.world.decay.DecaySource;

import java.util.Set;

public class SimpleDecayCondition extends GenericDecayCondition<Block> {
    public static final Codec<SimpleDecayCondition> CODEC = createCodec(SimpleDecayCondition::new, Registries.BLOCK);

    public static final String KEY = "block";

	public SimpleDecayCondition(TagOrElementLocation<Block> tagOrElementLocation) {
        super(tagOrElementLocation);
	}

    public static SimpleDecayCondition of(TagKey<Block> tag) {
        return new SimpleDecayCondition(TagOrElementLocation.of(tag, Registries.BLOCK));
    }

    public static SimpleDecayCondition of(ResourceKey<Block> key) {
        return new SimpleDecayCondition(TagOrElementLocation.of(key, Registries.BLOCK));
    }

    @Override
    public DecayConditionType<? extends DecayCondition> getType() {
        return DecayConditionType.SIMPLE_CONDITION_TYPE.get();
    }

    @Override
    public Holder<Block> getHolder(Level world, BlockPos pos, BlockState origin, BlockState targetBlock, FluidState targetFluid, DecaySource source) {
        return targetBlock.getBlockHolder();
    }

    @Override
	public Set<ResourceKey<Block>> constructApplicableBlocks() {
        return getTagOrElementLocation().getValues(BuiltInRegistries.BLOCK);
	}
}
