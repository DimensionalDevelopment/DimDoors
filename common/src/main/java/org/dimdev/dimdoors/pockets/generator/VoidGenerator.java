package org.dimdev.dimdoors.pockets.generator;

import com.bedrockk.molang.Expression;
import com.bedrockk.molang.runtime.value.MoValue;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Vec3i;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.api.util.math.Equation;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.modifier.Modifier;
import org.dimdev.dimdoors.util.MolangUtils;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.type.PocketBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoidGenerator extends PocketGenerator {
    public static MapCodec<VoidGenerator> CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(instance)
            .and(MolangUtils.CODEC.fieldOf("height").forGetter(a -> a.height))
            .and(MolangUtils.CODEC.fieldOf("height").forGetter(a -> a.width))
            .and(MolangUtils.CODEC.fieldOf("height").forGetter(a -> a.length)).apply(instance, VoidGenerator::new)
    );

	private static final Logger LOGGER = LogManager.getLogger();
	public static final String KEY = "void";
    private final Expression height;
    private final Expression width;
    private final Expression length;

    public VoidGenerator(PocketBuilder builder, Expression weight, HolderSet<Modifier> modifiers, Boolean setupLoot, List <String> tags, Expression height, Expression width, Expression length) {
        super(builder, weight, modifiers, setupLoot, tags);
        this.height = height;
        this.width = width;
        this.length = length;
    }

	@Override
	public Pocket prepareAndPlacePocket(PocketGenerationContext parameters, Pocket.PocketBuilder<?, ?> builder) {
		Pocket pocket = DimensionalRegistry.getPocketDirectory(parameters.world().dimension()).newPocket(builder);
		Map<String, Double> variableMap = parameters.toVariableMap(new HashMap<>());
		pocket.setSize((int) widthEquation.apply(variableMap), (int) heightEquation.apply(variableMap), (int) lengthEquation.apply(variableMap));

		return pocket;
	}

	@Override
	public PocketGeneratorType<? extends PocketGenerator> getType() {
		return PocketGeneratorType.VOID.get();
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public Vec3i getSize(PocketGenerationContext parameters) {
		Map<String, MoValue> variableMap = parameters.toVariableMap(new HashMap<>());
		return new Vec3i((int) MolangUtils.evaulateDouble(width, variableMap), (int) MolangUtils.evaulateDouble(height, variableMap), (int) MolangUtils.evaulateDouble(length, variableMap));
	}

}