package org.dimdev.dimdoors.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.dimdev.dimdoors.api.util.ResourceUtil;
import org.dimdev.dimdoors.item.RiftKeyItem;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

public record RiftKeyIds(Set<UUID> ids) {
    public static final Codec<RiftKeyIds> CODEC = UUIDUtil.CODEC_SET.xmap(RiftKeyIds::new, RiftKeyIds::ids);
    public static final StreamCodec<RegistryFriendlyByteBuf, RiftKeyIds> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.collection(HashSet::new, UUIDUtil.STREAM_CODEC), RiftKeyIds::ids, RiftKeyIds::new);
}
