package org.dimdev.dimdoors.pockets.virtual.reference;

import com.bedrockk.molang.Expression;
import com.google.common.collect.Maps;
import com.mojang.datafixers.Products;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.generator.PocketGenerator;
import org.dimdev.dimdoors.pockets.virtual.ImplementedVirtualPocket;
import org.dimdev.dimdoors.util.MolangUtils;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;

import java.util.*;

public abstract class PocketGeneratorReference implements ImplementedVirtualPocket {
    public static <T extends PocketGeneratorReference> Products.P2<RecordCodecBuilder.Mu<T>, Optional<Expression>, Optional<Boolean>> commonFields(RecordCodecBuilder.Instance<T> instance) {
        return instance.group(
                MolangUtils.CODEC.optionalFieldOf("weight").forGetter(a -> Optional.ofNullable(a.weight)),
        );
    }
	private static final Logger LOGGER = LogManager.getLogger();

	protected Expression weight;

    public PocketGeneratorReference(Optional<Expression> weight) {
        this.weight = weight.orElse(null);
    }

    @Override
	public double getWeight(PocketGenerationContext parameters) {
		try {
			return weight != null ? MolangUtils.evaulateDouble(weight, parameters.toVariableMap(Maps.newHashMap())) : peekReferencedPocketGenerator(parameters).value().getWeight(parameters);
		} catch (RuntimeException e) {
			LOGGER.error(this.toString());
			throw new AssertionError(e);
		}
	}

    public UUID prepareAndPlacePocket(PocketGenerationContext parameters) {
        return prepareAndPlacePocket(parameters, false);
    }

	@Override
	public UUID prepareAndPlacePocket(PocketGenerationContext parameters, Boolean setupLoot) {
        Holder<PocketGenerator> generator = getReferencedPocketGenerator(parameters);

        return DimensionalRegistry.getPocketDirectory().createPocket(generator);
	}

	@Override
	public PocketGeneratorReference peekNextPocketGeneratorReference(PocketGenerationContext parameters) {
		return this;
	}

	@Override
	public PocketGeneratorReference getNextPocketGeneratorReference(PocketGenerationContext parameters) {
		return this;
	}

	public abstract Holder<PocketGenerator> peekReferencedPocketGenerator(PocketGenerationContext parameters);

	public abstract Holder<PocketGenerator> getReferencedPocketGenerator(PocketGenerationContext parameters);

	@Override
	public abstract String toString();

}