package org.dimdev.dimdoors.mixin.client;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
//	@Shadow
//	private ClientWorld world;
//
//	@Inject(method = "onPlayerPositionLook", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
//	public void onPlayerLookPositionPacket(PlayerPositionLookS2CPacket packet, CallbackInfo ci, PlayerEntity playerEntity, Vec3d vec3d, boolean bl, boolean bl2, boolean bl3, double f, double g, double j, double k, double n, double o, float p, float q) {
//		if (ModDimensions.isLimboDimension(this.world)) {
//			this.world.addParticle(ModParticleTypes.MONOLITH, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), 0.0D, 0.0D, 0.0D);
//		}
//	}
}
