package org.dimdev.dimdoors.rift.registry;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import org.dimdev.dimdoors.api.util.Location;

import java.util.UUID;

public class RiftPlaceholder extends Rift {
    public static final MapCodec<RiftPlaceholder> CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(instance).apply(instance, RiftPlaceholder::new));

    public RiftPlaceholder() {
        super();
    }

    public RiftPlaceholder(Location location) {
        super(location);
    }


    public RiftPlaceholder(UUID id) {
        super(id);
    }

	@Override
	public void sourceGone(RegistryVertex source) {
	}

	@Override
	public void targetGone(RegistryVertex target) {
	}

	@Override
	public void sourceAdded(RegistryVertex source) {
	}

	@Override
	public void targetAdded(RegistryVertex target) {
	}

	@Override
	public RegistryVertexType<? extends RegistryVertex> getType() {
		return RegistryVertexType.RIFT_PLACEHOLDER.get();
	}
}