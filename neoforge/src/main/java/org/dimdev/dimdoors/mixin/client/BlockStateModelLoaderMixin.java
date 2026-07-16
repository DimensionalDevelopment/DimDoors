package org.dimdev.dimdoors.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockStateModelLoader.class)
public class BlockStateModelLoaderMixin {
    private static final String LOGGER_WARN_STRING_OBJECT = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;)V";
    private static final String LOGGER_WARN_STRING_OBJECT_OBJECT = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V";
    private static final String LOGGER_WARN_STRING_OBJECT_ARRAY = "Lorg/slf4j/Logger;warn(Ljava/lang/String;[Ljava/lang/Object;)V";

    /*
     * Actual Logger.warn method entries:
     * - loadBlockStateDefinitions(...) line 168: warn(String, Object)
     * - loadBlockStateDefinitions(...) line 170: warn(String, Object, Object)
     * - lambda$loadBlockStateDefinitions$8(...) line 155: warn(String, Object[])
     * - lambda$loadBlockStateDefinitions$10(...) line 176: warn(String, Object, Object), ordinal 0
     * - lambda$loadBlockStateDefinitions$10(...) line 186: warn(String, Object, Object), ordinal 1
     */

    @WrapOperation(method = "loadBlockStateDefinitions", at = @At(value = "INVOKE", target = LOGGER_WARN_STRING_OBJECT))
    private static void dimdoors$loadBlockStateDefinitionsBlockStateDefinitionException(
            Logger instance,
            String message,
            Object arg,
            Operation<Void> original
    ) {
        original.call(instance, message, arg);
    }

    @WrapOperation(method = "loadBlockStateDefinitions", at = @At(value = "INVOKE", target = LOGGER_WARN_STRING_OBJECT_OBJECT))
    private static void dimdoors$loadBlockStateDefinitionsGenericException(
            Logger instance,
            String message,
            Object arg0,
            Object arg1,
            Operation<Void> original
    ) {
        original.call(instance, message, arg0, arg1);
    }

    @WrapOperation(method = "lambda$loadBlockStateDefinitions$8", at = @At(value = "INVOKE", target = LOGGER_WARN_STRING_OBJECT_ARRAY))
    private static void dimdoors$lambdaLoadBlockStateDefinitions8VariantException(
            Logger instance,
            String message,
            Object[] args,
            Operation<Void> original
    ) {
        if (!dimdoors$isGeneratedBlockState(args[0])) {
            original.call(instance, message, args);
        }
    }

    @WrapOperation(method = "lambda$loadBlockStateDefinitions$10", at = @At(value = "INVOKE", target = LOGGER_WARN_STRING_OBJECT_OBJECT, ordinal = 0))
    private static void dimdoors$lambdaLoadBlockStateDefinitions10MissingModel(
            Logger instance,
            String message,
            Object arg0,
            Object arg1,
            Operation<Void> original
    ) {
        if (!dimdoors$isGeneratedBlockState(arg0)) {
            original.call(instance, message, arg0, arg1);
        }
    }

    @WrapOperation(method = "lambda$loadBlockStateDefinitions$10", at = @At(value = "INVOKE", target = LOGGER_WARN_STRING_OBJECT_OBJECT, ordinal = 1))
    private static void dimdoors$lambdaLoadBlockStateDefinitions10ModelDefinitionException(
            Logger instance,
            String message,
            Object arg0,
            Object arg1,
            Operation<Void> original
    ) {
        original.call(instance, message, arg0, arg1);
    }

    private static boolean dimdoors$isGeneratedBlockState(Object value) {
        return value instanceof ResourceLocation location
                && location.getNamespace().equals("dimdoors")
                && location.getPath().startsWith("blockstates/block_ag_dim_");
    }
}
