package org.dimdev.dimdoors.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.mehvahdjukaar.moonlight.api.resources.assets.LangBuilder;
import net.minecraft.core.HolderLookup;

import java.util.Stack;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractLanguageProvider extends FabricLanguageProvider {
    Stack<String> currentKeyPath = new Stack<>();
    protected HolderLookup.Provider provider;
    protected TranslationBuilder builder;

    protected AbstractLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup, String langCode) {
        super(dataOutput, langCode, registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder translationBuilder) {
        this.provider = registryLookup;
        this.builder = translationBuilder;
        this.generateTranslations();
    }

    abstract protected void generateTranslations();

    public void add(String key, String value, Runnable runnable) {
        add(value);

        scope(key, runnable);
    }

    public void add(String value) {
        builder.add(currentKeyPath.peek(), value);
    }

    public void add(String key, String value) {
        builder.add(currentKeyPath.peek() + "." + key, value);
    }

    public void scope(String path, Runnable runnable) {
        push(path);
        runnable.run();
        pop();
    }

    public void push(String path) {
        var current = currentKeyPath.peek();
        var newKey = current + "." + path;
        currentKeyPath.push(newKey);

    }

    public void pop() {
        currentKeyPath.pop();
    }
}
