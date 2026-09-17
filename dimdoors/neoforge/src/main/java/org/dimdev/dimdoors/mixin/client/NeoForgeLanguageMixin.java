package org.dimdev.dimdoors.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import org.dimdev.dimdoors.client.language.AutoGenDoorTranslations;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.InputStream;
import java.util.function.BiConsumer;

@Mixin(Language.class)
public class NeoForgeLanguageMixin {
    @WrapOperation(method = "loadFromJson(Ljava/io/InputStream;Ljava/util/function/BiConsumer;Ljava/util/function/BiConsumer;)V", at = @At(value = "INVOKE", target = "Ljava/util/function/BiConsumer;accept(Ljava/lang/Object;Ljava/lang/Object;)V"), remap = false)
    private static void dimdoors$acceptTranslation(BiConsumer<Object, Object> instance, Object key, Object value, Operation<Void> original) {
        original.call(instance, key, value);
        if (key instanceof String translationKey && value instanceof String translationValue) {
            AutoGenDoorTranslations.recordTranslation(translationKey, translationValue);
        }
    }

    @Inject(method = "loadFromJson(Ljava/io/InputStream;Ljava/util/function/BiConsumer;Ljava/util/function/BiConsumer;)V", at = @At("TAIL"), remap = false)
    private static void dimdoors$loadFromJson(InputStream stream, BiConsumer<String, String> output, BiConsumer<String, Component> componentOutput, CallbackInfo ci) {
        AutoGenDoorTranslations.apply(output);
    }
}
