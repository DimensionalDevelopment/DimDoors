package org.dimdev.dimdoors.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.SequencedPacketCreator;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;

import org.dimdev.dimdoors.api.event.UseItemOnBlockCallback;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin {

	@Shadow
	@Final
	private MinecraftClient client;

	@Shadow
	protected abstract void sendSequencedPacket(ClientWorld world, SequencedPacketCreator packetCreator);

	@Inject(method = "interactBlock", cancellable = true, at = @At(value = "NEW", target = "org/apache/commons/lang3/mutable/MutableObject", remap = false))
	public void useItemOnBlock(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> info) {
		ActionResult result = UseItemOnBlockCallback.EVENT.invoker().useItemOnBlock(player, client.world, hand, hitResult);
		if (result == ActionResult.PASS) {
			return;
		}
		info.setReturnValue(result);
		info.cancel();
		if (result == ActionResult.SUCCESS) {
			this.sendSequencedPacket(this.client.world, sequence -> new PlayerInteractBlockC2SPacket(hand, hitResult, sequence));
		}
	}
}
