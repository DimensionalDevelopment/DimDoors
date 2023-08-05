package org.dimdev.dimdoors.particle.client;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import org.dimdev.dimdoors.particle.ModParticleTypes;

import java.util.Locale;

public record RiftParticleOptions(float color, int averageAge) implements ParticleOptions {
   public static RiftParticleOptions of(boolean isOutsidePocket) {
      return isOutsidePocket ? OUTSIDE : INSIDE;
   }

   public static RiftParticleOptions of(boolean isOutsidePocket, boolean stablized) {
      if (isOutsidePocket) {
         if (stablized) {
            return OUTSIDE_STABLE;
         } else {
            return OUTSIDE_UNSTABLE;
         }
      } else {
         if (stablized) {
            return INSIDE_STABLE;
         } else {
            return INSIDE_UNSTABLE;
         }
      }
   }

   private static final RiftParticleOptions OUTSIDE = new RiftParticleOptions(0.4f, 2000);
   private static final RiftParticleOptions INSIDE = new RiftParticleOptions(0.8f, 2000);
   private static final RiftParticleOptions OUTSIDE_UNSTABLE = new RiftParticleOptions(0.0f, 2000);
   private static final RiftParticleOptions INSIDE_UNSTABLE = new RiftParticleOptions(0.7f, 2000);
   private static final RiftParticleOptions OUTSIDE_STABLE = new RiftParticleOptions(0.0f, 750);
   private static final RiftParticleOptions INSIDE_STABLE = new RiftParticleOptions(0.7f, 750);

   public static final Codec<RiftParticleOptions> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                   Codec.FLOAT.fieldOf("color").forGetter(riftParticleEffect -> riftParticleEffect.color),
                   Codec.INT.fieldOf("averageAge").forGetter((riftParticleEffect) -> riftParticleEffect.averageAge))
           .apply(instance, RiftParticleOptions::new));
   public static final Deserializer<RiftParticleOptions> PARAMETERS_FACTORY = new Deserializer<>() {
      @Override
      public RiftParticleOptions fromCommand(ParticleType<RiftParticleOptions> particleType, StringReader stringReader) throws CommandSyntaxException {
         stringReader.expect(' ');
         float f = stringReader.readFloat();
         stringReader.expect(' ');
         int g = stringReader.readInt();
         return new RiftParticleOptions(f, g);
      }

      @Override
      public RiftParticleOptions fromNetwork(ParticleType<RiftParticleOptions> particleType, FriendlyByteBuf packetByteBuf) {
         return new RiftParticleOptions(packetByteBuf.readFloat(), packetByteBuf.readInt());
      }
   };

   @Override
   public void writeToNetwork(FriendlyByteBuf buf) {
      buf.writeFloat(this.color);
      buf.writeInt(this.averageAge);
   }

   public String writeToString() {
      return String.format(Locale.ROOT, "%s %.2f %s", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.color, this.averageAge);
   }


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
