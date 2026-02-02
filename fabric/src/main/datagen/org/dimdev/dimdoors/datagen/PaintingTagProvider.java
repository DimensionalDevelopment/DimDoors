package org.dimdev.dimdoors.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.PaintingVariantTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.decoration.PaintingVariant;
import org.dimdev.dimdoors.painting.ModPaintings;
import org.joml.Vector2i;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PaintingTagProvider extends FabricTagProvider<PaintingVariant> {

    public PaintingTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.PAINTING_VARIANT, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        Map<Integer, List<Holder.Reference<PaintingVariant>>> map = wrapperLookup.lookupOrThrow(Registries.PAINTING_VARIANT).listElements().filter(a -> a.is(b -> !b.location().getNamespace().equals("dimdoors"))).collect(Collectors.groupingBy(new Function<Holder.Reference<PaintingVariant>, Integer>() {
            @Override
            public Integer apply(Holder.Reference<PaintingVariant> paintingVariantReference) {
                var value = paintingVariantReference.value();

                return ((value.width() - 1) * 4) + (value.height() - 1);
            }
        }));

        map.forEach((index, references) -> {
            var key = ModPaintings.PLACEHOLDERS.get(index);

            if(key != null) {
                tag(TagKey.create(key.registryKey(), key.location().withPrefix("decays_into_"))).addAll(references.stream().map(a -> a.key()).toList());
            }
        });

        tag(PaintingVariantTags.PLACEABLE).add(ModPaintings.LIMBO);
    }
}
