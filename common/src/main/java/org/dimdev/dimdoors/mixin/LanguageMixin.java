package org.dimdev.dimdoors.mixin;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.locale.Language;
import net.minecraft.util.GsonHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

@Mixin(Language.class)
public abstract class LanguageMixin {

    @Shadow
    @Final
    private static Gson GSON;

    @Shadow
    @Final
    private static Pattern UNSUPPORTED_FORMAT_PATTERN;

    //TODO: Figure out less crude method of doing this incase some mod for some god foresaken reason has reason to do this same level of direct alteration of languages.
    @Inject(
            method = "loadFromJson",
            at = @At("HEAD"),

            cancellable = true)
    private static void processText(InputStream stream, BiConsumer<String, String> output, CallbackInfo ci) {
        ci.cancel();

        JsonObject jsonobject = GSON.fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), JsonObject.class);

        for(Map.Entry<String, JsonElement> entry : jsonobject.entrySet()) {
            String value = UNSUPPORTED_FORMAT_PATTERN.matcher(GsonHelper.convertToString(entry.getValue(), entry.getKey())).replaceAll("%$1s");

            output.accept(entry.getKey(), value);
        }

    }

}
