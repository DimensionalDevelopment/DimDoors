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

public class RiftParticleEffect implements ParticleOptions {
   public static RiftParticleEffect of(boolean isOutsidePocket) {
      return isOutsidePocket ? OUTSIDE : INSIDE;
   }

   public static RiftParticleEffect of(boolean isOutsidePocket, boolean stablized) {
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

   private static final RiftParticleEffect OUTSIDE = new RiftParticleEffect(0.4f, 2000);
   private static final RiftParticleEffect INSIDE = new RiftParticleEffect(0.8f, 2000);
   private static final RiftParticleEffect OUTSIDE_UNSTABLE = new RiftParticleEffect(0.0f, 2000);
   private static final RiftParticleEffect INSIDE_UNSTABLE = new RiftParticleEffect( 0.7f, 2000);
   private static final RiftParticleEffect OUTSIDE_STABLE = new RiftParticleEffect(0.0f, 750);
   private static final RiftParticleEffect INSIDE_STABLE = new RiftParticleEffect( 0.7f, 750);

   public static final Codec<RiftParticleEffect> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
           Codec.FLOAT.fieldOf("color").forGetter(riftParticleEffect -> riftParticleEffect.color),
           Codec.INT.fieldOf("averageAge").forGetter((riftParticleEffect) -> riftParticleEffect.averageAge))
           .apply(instance, RiftParticleEffect::new));
   public static final ParticleOptions.Deserializer<RiftParticleEffect> PARAMETERS_FACTORY = new ParticleOptions.Deserializer<RiftParticleEffect>() {
      @Override
      public RiftParticleEffect fromCommand(ParticleType<RiftParticleEffect> particleType, StringReader stringReader) throws CommandSyntaxException {
         stringReader.expect(' ');
         float f = stringReader.readFloat();
         stringReader.expect(' ');
         int g = stringReader.readInt();
         return new RiftParticleEffect(f, g);
      }

      @Override
      public RiftParticleEffect fromNetwork(ParticleType<RiftParticleEffect> particleType, FriendlyByteBuf packetByteBuf) {
         return new RiftParticleEffect(packetByteBuf.readFloat(), packetByteBuf.readInt());
      }
   };

   private final float color;
   private final int averageAge;
   public RiftParticleEffect(float color, int averageAge) {
      this.color = color;
      this.averageAge = averageAge;
   }

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

   @Environment(EnvType.CLIENT)
   public float getColor() {
      return color;
   }

   @Environment(EnvType.CLIENT)
   public int getAverageAge() {
      return averageAge;
   }
}
