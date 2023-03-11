package org.dimdev.dimdoors.mixin;

import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.dimdev.dimdoors.command.arguments.BlockPlacementTypeArgumentType;
import org.dimdev.dimdoors.command.arguments.PocketTemplateArgumentType;

@Mixin(ArgumentTypeInfos.class)
public abstract class ArgumentTypesMixin {
	@Shadow
	private static <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> ArgumentTypeInfo<A, T> register(Registry<ArgumentTypeInfo<?, ?>> registry, String string, Class<? extends A> clazz, ArgumentTypeInfo<A, T> argumentSerializer) {
		throw new AssertionError("Nope.");
	}

	@Inject(method = "register(Lnet/minecraft/registry/Registry;)Lnet/minecraft/command/argument/serialize/ArgumentSerializer;", at = @At("RETURN"))
	private static void register(Registry<ArgumentTypeInfo<?, ?>> registry, CallbackInfoReturnable<ArgumentTypeInfo<?, ?>> ci) {
		register(registry, "pocket", PocketTemplateArgumentType.class, SingletonArgumentInfo.contextFree(PocketTemplateArgumentType::new));
		register(registry, "block_placement_type", BlockPlacementTypeArgumentType.class, SingletonArgumentInfo.contextFree(BlockPlacementTypeArgumentType::blockPlacementType));
	}
}
