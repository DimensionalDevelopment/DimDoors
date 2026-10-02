package org.dimdev.dimdoors.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.minecraft.core.HolderLookup
import java.util.*
import java.util.concurrent.CompletableFuture

abstract class AbstractLanguageProvider protected constructor(
    dataOutput: FabricDataOutput?,
    registryLookup: CompletableFuture<HolderLookup.Provider>,
    langCode: String?
) : FabricLanguageProvider(dataOutput, langCode, registryLookup) {
    var currentKeyPath = Stack<String>()
    protected lateinit var provider: HolderLookup.Provider
    protected lateinit var builder: TranslationBuilder

    override fun generateTranslations(registryLookup: HolderLookup.Provider, translationBuilder: TranslationBuilder) {
        this.provider = registryLookup
        this.builder = translationBuilder
        this.generateTranslations()
    }

    protected abstract fun generateTranslations()

    fun add(key: String, value: String, runnable: Runnable) {
        add(value)

        add(key, runnable)
    }

    fun add(value: String) {
        builder.add(currentKeyPath.peek(), value)
    }

    fun add(key: String, value: String) {
        builder.add(if (currentKeyPath.empty()) key else currentKeyPath.peek() + "." + key, value)
    }

    fun add(path: String, runnable: Runnable) {
        push(path)
        runnable.run()
        pop()
    }

    fun push(path: String) {
        val newKey = if (currentKeyPath.empty()) path else currentKeyPath.peek() + "." + path
        currentKeyPath.push(newKey)
    }

    fun pop() {
        currentKeyPath.pop()
    }
}
