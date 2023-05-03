package org.dimdev.dimdoors.mixin;

import com.mojang.brigadier.arguments.ArgumentType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.registry.Registry;

import org.dimdev.dimdoors.command.arguments.BlockPlacementTypeArgumentType;
import org.dimdev.dimdoors.command.arguments.PocketTemplateArgumentType;

@Mixin(ArgumentTypes.class)
public abstract class ArgumentTypesMixin {
	@Shadow
	private static <A extends ArgumentType<?>, T extends ArgumentSerializer.ArgumentTypeProperties<A>> ArgumentSerializer<A, T> register(Registry<ArgumentSerializer<?, ?>> registry, String string, Class<? extends A> clazz, ArgumentSerializer<A, T> argumentSerializer) {
		throw new AssertionError("Nope.");
	}

	@Inject(method = "register(Lnet/minecraft/registry/Registry;)Lnet/minecraft/command/argument/serialize/ArgumentSerializer;", at = @At("RETURN"))
	private static void register(Registry<ArgumentSerializer<?, ?>> registry, CallbackInfoReturnable<ArgumentSerializer<?, ?>> ci) {
		register(registry, "pocket", PocketTemplateArgumentType.class, ConstantArgumentSerializer.of(PocketTemplateArgumentType::new));
		register(registry, "block_placement_type", BlockPlacementTypeArgumentType.class, ConstantArgumentSerializer.of(BlockPlacementTypeArgumentType::blockPlacementType));
	}
}
