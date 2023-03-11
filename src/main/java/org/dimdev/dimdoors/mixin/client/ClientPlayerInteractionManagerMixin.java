package org.dimdev.dimdoors.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.multiplayer.prediction.PredictiveAction;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;
import org.dimdev.dimdoors.api.event.UseItemOnBlockCallback;

@Mixin(MultiPlayerGameMode.class)
public abstract class ClientPlayerInteractionManagerMixin {

	@Shadow
	@Final
	private Minecraft client;

	@Shadow
	protected abstract void sendSequencedPacket(ClientLevel world, PredictiveAction packetCreator);

	@Inject(method = "interactBlock", cancellable = true, at = @At(value = "NEW", target = "org/apache/commons/lang3/mutable/MutableObject", remap = false))
	public void useItemOnBlock(LocalPlayer player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> info) {
		InteractionResult result = UseItemOnBlockCallback.EVENT.invoker().useItemOnBlock(player, client.level, hand, hitResult);
		if (result == InteractionResult.PASS) {
			return;
		}
		info.setReturnValue(result);
		info.cancel();
		if (result == InteractionResult.SUCCESS) {
			this.sendSequencedPacket(this.client.level, sequence -> new ServerboundUseItemOnPacket(hand, hitResult, sequence));
		}
	}
}
