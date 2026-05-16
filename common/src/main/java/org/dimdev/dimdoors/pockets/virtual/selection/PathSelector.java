package org.dimdev.dimdoors.pockets.virtual.selection;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.ModRegistryKeys;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.virtual.ImplementedVirtualPocket;
import org.dimdev.dimdoors.pockets.virtual.VirtualPocket;
import org.dimdev.dimdoors.pockets.virtual.reference.PocketGeneratorReference;

// TODO: Override equals
public class PathSelector extends AbstractVirtualPocketList<PathSelector> {
    public static final MapCodec<PathSelector> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(ResourceLocation.CODEC.fieldOf("path").forGetter(a -> a.path)).apply(instance, PathSelector::new));
    public static final String KEY = "path";

    private final ResourceLocation path;
    private boolean initalized;

    public PathSelector(ResourceLocation path) {
        this.path = path;
    }

    @Override
    public ImplementedVirtualPocket.VirtualPocketType<PathSelector> getType() {
        return ImplementedVirtualPocket.VirtualPocketType.PATH_SELECTOR;
    }

    @Override
    public double getWeight(PocketGenerationContext context) {
        initialize(context);

        return super.getWeight(context);
    }

    @Override
    public PocketGeneratorReference<?> getNextPocketGeneratorReference(PocketGenerationContext context) {
        initialize(context);

        return super.getNextPocketGeneratorReference(context);
    }

    @Override
    public PocketGeneratorReference<?> peekNextPocketGeneratorReference(PocketGenerationContext context) {
        initialize(context);

        return super.peekNextPocketGeneratorReference(context);
    }

    private void initialize(PocketGenerationContext context) {
        if(!initalized) {
            context.provider().lookup(ModRegistryKeys.VIRTUAL_POCKET).stream().flatMap(HolderLookup::listElements).filter(this::checkKey).map(Holder.Reference::value).forEach(this::add);
            this.initalized = true;
        }
    }

    private boolean checkKey(Holder.Reference<VirtualPocket> virtualPocketReference) {
        var key = virtualPocketReference.key().location();
        return (key.getNamespace().equals("minecraft") || key.getNamespace().equals(path.getNamespace())) && key.getPath().startsWith(path.getPath());
    }
}
