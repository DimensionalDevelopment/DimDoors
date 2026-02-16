package org.dimdev.dimdoors.particle;

import com.mojang.serialization.MapCodec;
import net.mehvahdjukaar.moonlight.api.MoonlightTags;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.mehvahdjukaar.moonlight.api.resources.ResType;
import net.mehvahdjukaar.moonlight.api.resources.pack.DynamicServerResourceProvider;
import net.mehvahdjukaar.moonlight.api.resources.pack.ResourceGenTask;
import net.mehvahdjukaar.moonlight.api.resources.pack.ResourceSink;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.packs.resources.ResourceManager;
import org.dimdev.dimdoors.particle.client.RiftParticleOptions;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class RiftParticleType extends ParticleType<RiftParticleOptions> {
	protected RiftParticleType() {
		super(true);
	}

	@Override
	public MapCodec<RiftParticleOptions> codec() {
		return RiftParticleOptions.CODEC;
	}

	@Override
	public StreamCodec<? super RegistryFriendlyByteBuf, RiftParticleOptions> streamCodec() {
		return RiftParticleOptions.STREAM_CODEC;
	}
}
