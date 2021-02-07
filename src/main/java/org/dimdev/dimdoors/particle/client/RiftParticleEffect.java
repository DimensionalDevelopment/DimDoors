package org.dimdev.dimdoors.particle.client;

import java.util.Locale;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.registry.Registry;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.dimdev.dimdoors.particle.ModParticleTypes;

public class RiftParticleEffect implements ParticleEffect {
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
   public static final ParticleEffect.Factory<RiftParticleEffect> PARAMETERS_FACTORY = new ParticleEffect.Factory<RiftParticleEffect>() {
      public RiftParticleEffect read(ParticleType<RiftParticleEffect> particleType, StringReader stringReader) throws CommandSyntaxException {
         stringReader.expect(' ');
         float f = stringReader.readFloat();
         stringReader.expect(' ');
         int g = stringReader.readInt();
         return new RiftParticleEffect(f, g);
      }

      public RiftParticleEffect read(ParticleType<RiftParticleEffect> particleType, PacketByteBuf packetByteBuf) {
         return new RiftParticleEffect(packetByteBuf.readFloat(), packetByteBuf.readInt());
      }
   };

   private final float color;
   private final int averageAge;
   public RiftParticleEffect(float color, int averageAge) {
      this.color = color;
      this.averageAge = averageAge;
   }

   public void write(PacketByteBuf buf) {
      buf.writeFloat(this.color);
      buf.writeInt(this.averageAge);
   }

   public String asString() {
      return String.format(Locale.ROOT, "%s %.2f %s", Registry.PARTICLE_TYPE.getId(this.getType()), this.color, this.averageAge);
   }

   public ParticleType<RiftParticleEffect> getType() {
      return ModParticleTypes.RIFT;
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
