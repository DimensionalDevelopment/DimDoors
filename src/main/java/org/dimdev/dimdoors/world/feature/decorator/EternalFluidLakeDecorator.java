package org.dimdev.dimdoors.world.feature.decorator;

import java.util.Random;
import java.util.stream.Stream;

import com.mojang.serialization.Codec;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorContext;

public class EternalFluidLakeDecorator extends Decorator<ChanceDecoratorConfig> {
	public EternalFluidLakeDecorator(Codec<ChanceDecoratorConfig> configCodec) {
		super(configCodec);
	}

	@Override
	public Stream<BlockPos> getPositions(DecoratorContext context, Random random, ChanceDecoratorConfig config, BlockPos pos) {
		if (random.nextInt(config.chance) == 0) {
			return Stream.of(new BlockPos(random.nextInt(16) + pos.getX(), random.nextInt(context.getHeight()), random.nextInt(16) + pos.getZ()));
		}

		return Stream.empty();
	}
}
