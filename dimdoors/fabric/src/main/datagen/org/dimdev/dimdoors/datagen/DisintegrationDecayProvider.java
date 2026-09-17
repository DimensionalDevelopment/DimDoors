package org.dimdev.dimdoors.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import org.dimdev.dimdoors.world.decay.DecayPatternHolder;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

//TODO: Populate and Connect
public class DisintegrationDecayProvider extends LimboDecayProvider {
    public DisintegrationDecayProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void generatePatterns(HolderLookup.Provider provider, Consumer<DecayPatternHolder> consumer) {}

    @Override
    public @NotNull String getName() {
        return "Disintegration Decay Patterns";
    }
}
