package org.dimdev.dimdoors.mixin.client;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import org.dimdev.dimdoors.api.event.UseItemOnBlockCallback;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

	@Inject(method = "interactBlock", cancellable = true, at = @At(value = "INVOKE", ordinal = 2, target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V"))
	public void useItemOnBlock(ClientPlayerEntity player, ClientWorld world, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> info) {
		ActionResult result = UseItemOnBlockCallback.EVENT.invoker().useItemOnBlock(player, world, hand, hitResult);
		if (result == ActionResult.PASS) {
			return;
		}
		info.setReturnValue(result);
		info.cancel();
		if (result == ActionResult.SUCCESS) {
			player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(hand, hitResult));
		}
	}
}
