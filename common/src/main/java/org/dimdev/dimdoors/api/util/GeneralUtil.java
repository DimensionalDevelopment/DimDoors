package org.dimdev.dimdoors.api.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import org.dimdev.dimdoors.block.door.DimensionalDoorBlock;
import org.dimdev.dimdoors.block.entity.RiftData;

import java.util.function.Function;

public class GeneralUtil {
    public static final Codec<Direction> HORIZONTAL_DIRECTION_CODEC = Direction.CODEC.validate(GeneralUtil::checkIfVertical);
    public static final Codec<DimensionalDoorBlock> DIMENSIONAL_DOOR_BLOCK_CODEC = BuiltInRegistries.BLOCK.byNameCodec().validate(GeneralUtil::verifyIfDimensionalDoo).xmap(DimensionalDoorBlock.class::cast, Function.identity());
    public static final StreamCodec<RegistryFriendlyByteBuf, Identifier> RESOURCE_LOCATION_STREAM_CODEC = StreamCodec.<RegistryFriendlyByteBuf, Identifier>ofMember((value, buf) -> buf.writeIdentifier(value), FriendlyByteBuf::readIdentifier);

    private static DataResult<Direction> checkIfVertical(Direction direction) {
        return direction.getAxis().isHorizontal() ? DataResult.success(direction) : DataResult.error(() -> "Direction can not be vertical");
    }

    private static DataResult<Block> verifyIfDimensionalDoo(Block block) {
        return block instanceof DimensionalDoorBlock ? DataResult.success(block) : DataResult.error(() -> "Block is not a dimensional doors.");
    }
}
