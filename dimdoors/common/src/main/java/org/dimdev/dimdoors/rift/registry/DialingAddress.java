package org.dimdev.dimdoors.rift.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record DialingAddress(byte dial1, byte dial2, byte dial3) {
    public static final DialingAddress DEFAULT = new DialingAddress((byte) 0, (byte) 0, (byte) 0);
    public static final Codec<DialingAddress> CODEC = Codec.INT.xmap(DialingAddress::from, DialingAddress::to);
    public static final Codec<DialingAddress> STRING_CODEC = Codec.STRING.xmap(Integer::decode, Object::toString).xmap(DialingAddress::from, DialingAddress::to);
    public static final MapCodec<DialingAddress> MAP_CODEC = CODEC.optionalFieldOf("address", DEFAULT);
    public static final StreamCodec<RegistryFriendlyByteBuf, DialingAddress> STREAM_CODEC = ByteBufCodecs.INT.map(DialingAddress::from, DialingAddress::to).cast();

    public int to() {
        return ((dial1 & 0xFF) << 16)
                | ((dial2 & 0xFF) << 8)
                |  (dial3 & 0xFF);
    }

    public static DialingAddress from(int value) {
        return new DialingAddress(
                (byte) (value >> 16),
                (byte) (value >> 8),
                (byte) value);
    }

    public DialingAddress turnDial(DialType dial) {
        return switch (dial) {
            case FIRST -> new DialingAddress(wrap((byte) (dial1 + 1), 10), dial2, dial3);
            case SECOND -> new DialingAddress(dial1, wrap((byte) (dial2 + 1), 10), dial3);
            case THIRD -> new DialingAddress(dial1, dial2, wrap((byte) (dial3 + 1), 10));
        };
    }

    private static byte wrap(byte value, int mod) {
        return (byte) (value % mod);
    }

    public enum DialType {
        FIRST, SECOND, THIRD;
    }
}
