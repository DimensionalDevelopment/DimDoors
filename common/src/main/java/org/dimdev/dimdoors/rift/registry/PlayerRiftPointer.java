package org.dimdev.dimdoors.rift.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;

import java.util.UUID;

public class PlayerRiftPointer extends RegistryVertex {
	public static final MapCodec<PlayerRiftPointer> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            UUIDUtil.CODEC.fieldOf("id").forGetter(a -> a.id)
    ).apply(instance, PlayerRiftPointer::new));

    public static final Codec<PlayerRiftPointer> COMPRESSED_CODEC = UUIDUtil.CODEC.xmap(PlayerRiftPointer::new, PlayerRiftPointer::getId);

    public PlayerRiftPointer() {
        super();
    }

	public PlayerRiftPointer(UUID uuid) {
        super(uuid);
	}

	@Override
	public RegistryVertexType<? extends RegistryVertex> getType() {
		return RegistryVertexType.PLAYER.get();
	}
}