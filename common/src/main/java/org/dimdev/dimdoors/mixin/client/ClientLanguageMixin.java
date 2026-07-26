package org.dimdev.dimdoors.mixin.client;

import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.server.packs.resources.ResourceManager;
import org.dimdev.dimdoors.client.language.AutoGenDoorTranslations;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ClientLanguage.class)
public class ClientLanguageMixin {
    @Inject(method = "loadFrom", at = @At("HEAD"))
    private static void dimdoors$loadFrom(ResourceManager resourceManager, List<String> languages, boolean defaultRightToLeft, CallbackInfoReturnable<ClientLanguage> cir) {
        AutoGenDoorTranslations.beginReload();
    }
}
