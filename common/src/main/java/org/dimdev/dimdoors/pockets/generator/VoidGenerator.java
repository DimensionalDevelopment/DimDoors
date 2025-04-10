package org.dimdev.dimdoors.pockets.generator;

import com.mojang.datafixers.Products;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.api.util.math.Equation;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.modifier.LazyModifier;
import org.dimdev.dimdoors.pockets.modifier.Modifier;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoidGenerator extends LazyPocketGenerator {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final String KEY = "void";

	public static MapCodec<VoidGenerator> CODEC = RecordCodecBuilder.mapCodec(instance -> {
			var common = commonLazyFields(instance);

			return new Products.P9<>(common.t1(), common.t2(), common.t3(), common.t4(), common.t5(), common.t6(),

                    Equation.CODEC.fieldOf("width").forGetter(a -> a.width),
                    Equation.CODEC.fieldOf("height").forGetter(a -> a.height),
                    Equation.CODEC.fieldOf("length").forGetter(a -> a.length))
			.apply(instance, VoidGenerator::new);
	});

	private Equation width;
	private Equation height;
	private Equation length;

	public VoidGenerator(CompoundTag builder, Equation weight, boolean setupLoot, List<Modifier> modifierList, List<String> tags, List<LazyModifier> lazyModifierList, Equation width, Equation height, Equation length) {
		super(builder, weight, setupLoot, modifierList, tags, lazyModifierList);
        this.width = width;
        this.height = height;
        this.length = length;
	}

	@Override
	public Pocket prepareAndPlacePocket(PocketGenerationContext parameters, Pocket.PocketBuilder<?, ?> builder) {
		Pocket pocket = DimensionalRegistry.getPocketDirectory(parameters.world().dimension()).newPocket(builder);
		Map<String, Double> variableMap = parameters.toVariableMap(new HashMap<>());
		pocket.setSize((int) width.apply(variableMap), (int) height.apply(variableMap), (int) length.apply(variableMap));

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
		Map<String, Double> variableMap = parameters.toVariableMap(new HashMap<>());
		return new Vec3i((int) width.apply(variableMap), (int) height.apply(variableMap), (int) length.apply(variableMap));
	}

	@Override
	public LazyPocketGenerator cloneWithEmptyModifiers(BlockPos originalOrigin) {
		VoidGenerator generator = (VoidGenerator) super.cloneWithEmptyModifiers(originalOrigin);
		generator.width = width;
		generator.height = height;
		generator.length = length;

		return generator;
	}

	@Override
	public LazyPocketGenerator getNewInstance() {
		return new VoidGenerator(builder, weight, setupLoot, modifierList, tags, lazyModifierList, width, height, length);
	}
}
