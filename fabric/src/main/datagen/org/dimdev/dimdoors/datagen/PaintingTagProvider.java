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

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
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

                return (value.width() - 1) + (value.height() - 1) * 4;
            }
        }));

        map.forEach((index, references) -> {
            var key = ModPaintings.PAINTINGS_TO_DECAY_INTO.get(index);

            if(key != null) {
                var id = key.location().withPrefix("decays_into_");

                if(!id.getPath().contains("placeholder")) id = id.withSuffix("_painting");

                tag(TagKey.create(key.registryKey(), id)).addAll(references.stream().map(Holder.Reference::key).toList());
            }
        });

        tag(PaintingVariantTags.PLACEABLE).add(ModPaintings.LIMBO);
    }
}
