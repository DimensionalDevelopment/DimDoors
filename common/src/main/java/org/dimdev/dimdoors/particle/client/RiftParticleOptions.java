package org.dimdev.dimdoors.particle.client;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.dimdev.dimdoors.particle.ModParticleTypes;

public record RiftParticleOptions(float color, int averageAge) implements ParticleOptions {
   public static RiftParticleOptions of(boolean isOutsidePocket) {
      return isOutsidePocket ? OUTSIDE : INSIDE;
   }

   public static RiftParticleOptions of(boolean isOutsidePocket, boolean stablized) {
      return isOutsidePocket ? stablized ? OUTSIDE_STABLE : OUTSIDE_UNSTABLE : stablized ? INSIDE_STABLE : INSIDE_UNSTABLE;
   }

   private static final RiftParticleOptions OUTSIDE = new RiftParticleOptions(0.4f, 2000);
   private static final RiftParticleOptions INSIDE = new RiftParticleOptions(0.8f, 2000);
   private static final RiftParticleOptions OUTSIDE_UNSTABLE = new RiftParticleOptions(0.0f, 2000);
   private static final RiftParticleOptions INSIDE_UNSTABLE = new RiftParticleOptions(0.7f, 2000);
   private static final RiftParticleOptions OUTSIDE_STABLE = new RiftParticleOptions(0.0f, 750);
   private static final RiftParticleOptions INSIDE_STABLE = new RiftParticleOptions(0.7f, 750);

   public static final MapCodec<RiftParticleOptions> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
                   Codec.FLOAT.fieldOf("color").forGetter(riftParticleEffect -> riftParticleEffect.color),
                   Codec.INT.fieldOf("averageAge").forGetter((riftParticleEffect) -> riftParticleEffect.averageAge))
           .apply(instance, RiftParticleOptions::new));
   public static final StreamCodec<RegistryFriendlyByteBuf, RiftParticleOptions> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.FLOAT, RiftParticleOptions::color, ByteBufCodecs.VAR_INT, RiftParticleOptions::averageAge, RiftParticleOptions::new);

   public ParticleType<?> getType() {
      return ModParticleTypes.RIFT.get();
   }

   @Override
   @Environment(EnvType.CLIENT)
   public float color() {
      return color;
   }

   @Override
   @Environment(EnvType.CLIENT)
   public int averageAge() {
      return averageAge;
   }
}
