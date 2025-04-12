package org.dimdev.dimdoors.rift.registry;

import com.mojang.datafixers.util.Function4;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import org.dimdev.dimdoors.api.util.Location;

import java.util.Optional;
import java.util.UUID;

public class RiftPlaceholder extends Rift {
	public final static MapCodec<RiftPlaceholder> CODEC = RecordCodecBuilder.mapCodec(instance -> commonRiftFields(instance).apply(instance, (id, location, isDetached, properties) -> {
        var placeholder = new RiftPlaceholder();
        placeholder.setId(id);
        placeholder.setProperties(properties.orElse(null));
        placeholder.setDetached(isDetached);
        placeholder.setLocation(location.orElse(null));
        return placeholder;
    }));

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