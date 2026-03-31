package org.dimdev.dimdoors.pockets.virtual.selection;

import com.bedrockk.molang.Expression;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.virtual.ImplementedVirtualPocket;
import org.dimdev.dimdoors.pockets.virtual.VirtualPocket;
import org.dimdev.dimdoors.pockets.virtual.reference.PocketGeneratorReference;
import org.dimdev.dimdoors.util.MolangUtils;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ConditionalSelector implements ImplementedVirtualPocket {
    public static final MapCodec<ConditionalSelector> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ConditionalPocket.CODEC.listOf().fieldOf("pockets").forGetter(ConditionalSelector::getPocketMap)
    ).apply(instance, ConditionalSelector::new));
    private static final Logger LOGGER = LogManager.getLogger();
	public static final String KEY = "conditional";

    private final List<ConditionalPocket> pockets;

	public ConditionalSelector(List<ConditionalPocket> pockets) {
		this.pockets = pockets;
	}

	public List<ConditionalPocket> getPocketMap() {
		return pockets;
	}

    @Override
    public UUID prepareAndPlacePocket(PocketGenerationContext parameters, Boolean setupLoot) {
        return getNextPocket(parameters).prepareAndPlacePocket(parameters, setupLoot);
    }

    @Override
	public UUID prepareAndPlacePocket(PocketGenerationContext parameters) {
		return getNextPocket(parameters).prepareAndPlacePocket(parameters);
	}

	@Override
	public PocketGeneratorReference getNextPocketGeneratorReference(PocketGenerationContext parameters) {
		return getNextPocket(parameters).getNextPocketGeneratorReference(parameters);
	}

	@Override
	public PocketGeneratorReference peekNextPocketGeneratorReference(PocketGenerationContext parameters) {
		return getNextPocket(parameters).peekNextPocketGeneratorReference(parameters);
	}

	@Override
	public VirtualPocketType<? extends ImplementedVirtualPocket> getType() {
		return VirtualPocketType.CONDITIONAL_SELECTOR.get();
	}

    @Override
	public double getWeight(PocketGenerationContext parameters) {
		return getNextPocket(parameters).getWeight(parameters);
	}

	private VirtualPocket getNextPocket(PocketGenerationContext parameters) {
        var map = parameters.toVariableMap(new HashMap<>());
        return pockets.stream().filter(entry -> MolangUtils.evaulateBoolean(entry.condition(), map)).findFirst().map(ConditionalPocket::pocket).orElse(NoneVirtualPocket.NONE);

    }

    public record ConditionalPocket(Expression condition, VirtualPocket pocket) {
        public static final Codec<ConditionalPocket> CODEC = RecordCodecBuilder.create(instance -> instance
                .group(
                        MolangUtils.CODEC.fieldOf("condition").forGetter(ConditionalPocket::condition),
                        VirtualPocket.CODEC.fieldOf("pocket").forGetter(ConditionalPocket::pocket))
                .apply(instance, ConditionalPocket::new)
        );
    }
}