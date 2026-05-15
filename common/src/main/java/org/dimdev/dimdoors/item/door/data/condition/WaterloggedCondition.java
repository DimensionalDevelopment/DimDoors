package org.dimdev.dimdoors.item.door.data.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;

public record WaterloggedCondition(boolean waterlogged) implements Condition {
    public static final MapCodec<WaterloggedCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(Codec.BOOL.fieldOf("waterlogged").forGetter(WaterloggedCondition::waterlogged)).apply(instance, WaterloggedCondition::new));

    @Override
    public boolean matches(EntranceRiftBlockEntity rift) {
        return rift.getLevel().getBlockState(rift.getBlockPos()).getValue(BlockStateProperties.WATERLOGGED) == waterlogged;
    }

    @Override
    public ConditionType<?> getType() {
        return ConditionType.BIOME;
    }
}