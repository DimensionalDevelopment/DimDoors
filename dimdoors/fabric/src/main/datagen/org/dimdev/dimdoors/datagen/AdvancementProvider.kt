package org.dimdev.dimdoors.datagen

import net.minecraft.advancements.AdvancementHolder
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.advancements.AdvancementSubProvider
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

class AdvancementProvider(arg: PackOutput, completableFuture: CompletableFuture<HolderLookup.Provider>) : net.minecraft.data.advancements.AdvancementProvider(arg, completableFuture, listOf(AdvancementSubProvider { provider, consumer -> generateAdvancement(provider, consumer) })) {

    companion object {
        @JvmStatic
        fun generateAdvancement(arg: HolderLookup.Provider, consumer: Consumer<AdvancementHolder>) {
            AdvancementTab().accept(consumer)
        }
    }
}
