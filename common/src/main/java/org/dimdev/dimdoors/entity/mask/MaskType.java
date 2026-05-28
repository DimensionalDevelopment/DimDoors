package org.dimdev.dimdoors.entity.mask;

import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum MaskType implements StringRepresentable {
    CYCLOP("cyclop"),
    ECHO("echo"),
    ENLIGHTENED("enlightened"),
    FORESIGHT("foresight"),
    SCULKING("sculking"),
    RANDOM("random"),
    BLACK("black");

    public static final Codec<MaskType> CODEC = StringRepresentable.fromValues(MaskType::values);
    public static final StreamCodec<RegistryFriendlyByteBuf, MaskType> STREAM_CODEC =
            StreamCodec.of(FriendlyByteBuf::writeEnum, byteBuf -> byteBuf.readEnum(MaskType.class));

    private final String name;

    MaskType(String name) {
        this.name = name;
    }

    public MaskType nextEditable() {
        return switch (this) {
            case CYCLOP -> ECHO;
            case ECHO -> ENLIGHTENED;
            case ENLIGHTENED -> FORESIGHT;
            case FORESIGHT -> SCULKING;
            case SCULKING -> RANDOM;
            case RANDOM, BLACK -> CYCLOP;
        };
    }

    public boolean isEditableSpawnType() {
        return this != BLACK;
    }

    public double detectionRange() {
        return switch (this) {
            case CYCLOP, RANDOM -> 6.0;
            case ECHO -> 4.0;
            case ENLIGHTENED -> 3.0;
            case FORESIGHT -> 2.0;
            case SCULKING -> 8.0;
            case BLACK -> MaskConstants.CHASE_RANGE;
        };
    }

    @Override
    public @NotNull String getSerializedName() {
        return name;
    }
}
