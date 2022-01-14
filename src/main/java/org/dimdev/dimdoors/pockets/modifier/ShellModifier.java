package org.dimdev.dimdoors.pockets.modifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.MoreObjects;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.AirBlock;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;

import net.fabricmc.fabric.api.util.NbtType;

import net.minecraft.util.math.Vec3i;
import net.minecraft.world.chunk.Chunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.dimdev.dimdoors.api.util.BlockBoxUtil;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.api.util.math.Equation;
import org.dimdev.dimdoors.util.schematic.SchematicBlockPalette;
import org.dimdev.dimdoors.world.pocket.type.LazyGenerationPocket;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

public class ShellModifier implements LazyModifier {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final String KEY = "shell";

	private final List<Layer> layers = new ArrayList<>();
	private BlockBox boxToDrawAround;

	@Override
	public NbtCompound toNbt(NbtCompound nbt) {
		LazyModifier.super.toNbt(nbt);

		NbtList layersNbt = new NbtList();
		for (Layer layer : layers) {
			layersNbt.add(layer.toNbt());
		}
		nbt.put("layers", layersNbt);
		if (boxToDrawAround != null) {
			nbt.put("box_to_draw_around", BlockBoxUtil.toNbt(boxToDrawAround));
		}

		return nbt;
	}

	@Override
	public void applyToChunk(LazyGenerationPocket pocket, Chunk chunk) {

		int boxExpansion = 0;
		for (Layer layer : layers) {
			int thickness = layer.getThickness(pocket.toVariableMap(new HashMap<>()));
			final BlockState blockState = layer.getBlockState();

			BlockBox chunkBox = BlockBoxUtil.getBox(chunk);

			BlockBox temp;


			// x-planes
			temp = BlockBox.create(new Vec3i(boxToDrawAround.getMaxX() + 1 + boxExpansion, boxToDrawAround.getMinY() - thickness - boxExpansion, boxToDrawAround.getMinZ() - thickness - boxExpansion), new Vec3i(boxToDrawAround.getMaxX() + thickness + boxExpansion, boxToDrawAround.getMaxY() + thickness + boxExpansion, boxToDrawAround.getMaxZ() + thickness + boxExpansion));
			if (temp.intersects(chunkBox)) {
				temp = BlockBoxUtil.intersect(temp, chunkBox);
				BlockPos.stream(temp)
						.forEach(blockPos -> {
							if (chunk.getBlockState(blockPos).isAir()) chunk.setBlockState(blockPos, blockState, false);
						});
			}
			temp = BlockBox.create(new Vec3i(boxToDrawAround.getMinX() - 1 - boxExpansion, boxToDrawAround.getMinY() - thickness - boxExpansion, boxToDrawAround.getMinZ() - thickness - boxExpansion), new Vec3i(boxToDrawAround.getMinX() - thickness - boxExpansion, boxToDrawAround.getMaxY() + thickness + boxExpansion, boxToDrawAround.getMaxZ() + thickness + boxExpansion));
			if (temp.intersects(chunkBox)) {
				temp = BlockBoxUtil.intersect(temp, chunkBox);
				BlockPos.stream(temp)
						.forEach(blockPos -> {
							if (chunk.getBlockState(blockPos).isAir()) chunk.setBlockState(blockPos, blockState, false);
						});
			}

			// y-planes
			temp = BlockBox.create(new Vec3i(boxToDrawAround.getMinX() - boxExpansion, boxToDrawAround.getMaxY() + 1 + boxExpansion, boxToDrawAround.getMinZ() - thickness - boxExpansion), new Vec3i(boxToDrawAround.getMaxX() + boxExpansion, boxToDrawAround.getMaxY() + thickness + boxExpansion, boxToDrawAround.getMaxZ() + thickness + boxExpansion));
			if (temp.intersects(chunkBox)) {
				temp = BlockBoxUtil.intersect(temp, chunkBox);
				BlockPos.stream(temp)
						.forEach(blockPos -> {
							if (chunk.getBlockState(blockPos).getBlock() instanceof AirBlock)
								chunk.setBlockState(blockPos, blockState, false);
						});
			}
			temp = BlockBox.create(new Vec3i(boxToDrawAround.getMinX() - boxExpansion, boxToDrawAround.getMinY() - 1 - boxExpansion, boxToDrawAround.getMinZ() - thickness - boxExpansion), new Vec3i(boxToDrawAround.getMaxX() + boxExpansion, boxToDrawAround.getMinY() - thickness - boxExpansion, boxToDrawAround.getMaxZ() + thickness + boxExpansion));
			if (temp.intersects(chunkBox)) {
				temp = BlockBoxUtil.intersect(temp, chunkBox);
				BlockPos.stream(temp)
						.forEach(blockPos -> {
							if (chunk.getBlockState(blockPos).isAir()) chunk.setBlockState(blockPos, blockState, false);
						});
			}

			// z-planes
			temp = BlockBox.create(new Vec3i(boxToDrawAround.getMinX() - boxExpansion, boxToDrawAround.getMinY() - boxExpansion, boxToDrawAround.getMinZ() - 1 - boxExpansion), new Vec3i(boxToDrawAround.getMaxX() + boxExpansion, boxToDrawAround.getMaxY() + boxExpansion, boxToDrawAround.getMinZ() - thickness - boxExpansion));
			if (temp.intersects(chunkBox)) {
				temp = BlockBoxUtil.intersect(temp, chunkBox);
				BlockPos.stream(temp)
						.forEach(blockPos -> {
							if (chunk.getBlockState(blockPos).isAir()) chunk.setBlockState(blockPos, blockState, false);
						});
			}
			temp = BlockBox.create(new Vec3i(boxToDrawAround.getMinX() - boxExpansion, boxToDrawAround.getMinY() - boxExpansion, boxToDrawAround.getMaxZ() + 1 + boxExpansion), new Vec3i(boxToDrawAround.getMaxX() + boxExpansion, boxToDrawAround.getMaxY() + boxExpansion, boxToDrawAround.getMaxZ() + thickness + boxExpansion));
			if (temp.intersects(chunkBox)) {
				temp = BlockBoxUtil.intersect(temp, chunkBox);
				BlockPos.stream(temp)
						.forEach(blockPos -> {
							if (chunk.getBlockState(blockPos).isAir()) chunk.setBlockState(blockPos, blockState, false);
						});
			}

			boxExpansion += thickness;
		}
	}

	@Override
	public Modifier fromNbt(NbtCompound nbt) {
		for (NbtElement layerNbt : nbt.getList("layers", NbtType.COMPOUND)) {
			NbtCompound nbtCompound = (NbtCompound) layerNbt;
			try {
				Layer layer = Layer.fromNbt(nbtCompound);
				layers.add(layer);
			} catch (CommandSyntaxException e) {
				LOGGER.error("could not parse Layer: " + nbtCompound, e);
			}
		}

		if (nbt.contains("box_to_draw_around", NbtType.INT_ARRAY)) {
			int[] box = nbt.getIntArray("box_to_draw_around");
			boxToDrawAround = BlockBox.create(new Vec3i(box[0], box[1], box[2]), new Vec3i(box[3], box[4], box[5]));
		}

		return this;
	}

	@Override
	public ModifierType<? extends Modifier> getType() {
		return ModifierType.SHELL_MODIFIER_TYPE;
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public void apply(PocketGenerationContext parameters, RiftManager manager) {
		Pocket pocket = manager.getPocket();
		if (pocket instanceof LazyGenerationPocket) {
			Map<String, Double> variableMap = pocket.toVariableMap(new HashMap<>());
			BlockBox pocketBox = pocket.getBox();
			boxToDrawAround = BlockBox.create(new Vec3i(pocketBox.getMinX(), pocketBox.getMinY(), pocketBox.getMinZ()), new Vec3i(pocketBox.getMaxX(), pocketBox.getMaxY(), pocketBox.getMaxZ()));
			layers.forEach(layer -> pocket.expand(layer.getThickness(variableMap)));
		} else {
			layers.forEach(layer -> drawLayer(layer, manager.getPocket(), parameters.world()));
		}
	}

	@Override
	public void apply(PocketGenerationContext parameters, Pocket.PocketBuilder<?, ?> builder) {
		Map<String, Double> variableMap = parameters.toVariableMap(new HashMap<>());
		for (Layer layer : layers) {
			int thickness = layer.getThickness(variableMap);
			builder.expandExpected(new Vec3i(2 * thickness, 2 * thickness, 2 * thickness));
			builder.offsetOrigin(new Vec3i(thickness, thickness, thickness));
		}
	}

	private void drawLayer(Layer layer, Pocket pocket, ServerWorld world) {
		int thickness = layer.getThickness(pocket.toVariableMap(new HashMap<>()));
		final BlockState blockState = layer.getBlockState();
		BlockBox pocketBox = pocket.getBox();

		// x-planes
		BlockPos.stream(BlockBox.create(new Vec3i(pocketBox.getMaxX() + 1, pocketBox.getMinY() - thickness, pocketBox.getMinZ() - thickness), new Vec3i(pocketBox.getMaxX() + thickness, pocketBox.getMaxY() + thickness, pocketBox.getMaxZ() + thickness)))
				.forEach(blockPos -> world.setBlockState(blockPos, blockState));
		BlockPos.stream(BlockBox.create(new Vec3i(pocketBox.getMinX() - 1, pocketBox.getMinY() - thickness, pocketBox.getMinZ() - thickness), new Vec3i(pocketBox.getMinX() - thickness, pocketBox.getMaxY() + thickness, pocketBox.getMaxZ() + thickness)))
				.forEach(blockPos -> world.setBlockState(blockPos, blockState));

		// y-planes
		BlockPos.stream(BlockBox.create(new Vec3i(pocketBox.getMinX(), pocketBox.getMaxY() + 1, pocketBox.getMinZ() - thickness), new Vec3i(pocketBox.getMaxX(), pocketBox.getMaxY() + thickness, pocketBox.getMaxZ() + thickness)))
				.forEach(blockPos -> world.setBlockState(blockPos, blockState));
		BlockPos.stream(BlockBox.create(new Vec3i(pocketBox.getMinX(), pocketBox.getMinY() - 1, pocketBox.getMinZ() - thickness), new Vec3i(pocketBox.getMaxX(), pocketBox.getMinY() - thickness, pocketBox.getMaxZ() + thickness)))
				.forEach(blockPos -> world.setBlockState(blockPos, blockState));

		// z-planes
		BlockPos.stream(BlockBox.create(new Vec3i(pocketBox.getMinX(), pocketBox.getMinY(), pocketBox.getMinZ() - 1), new Vec3i(pocketBox.getMaxX(), pocketBox.getMaxY(), pocketBox.getMinZ() - thickness)))
				.forEach(blockPos -> world.setBlockState(blockPos, blockState));
		BlockPos.stream(BlockBox.create(new Vec3i(pocketBox.getMinX(), pocketBox.getMinY(), pocketBox.getMaxZ() + 1), new Vec3i(pocketBox.getMaxX(), pocketBox.getMaxY(), pocketBox.getMaxZ() + thickness)))
				.forEach(blockPos -> world.setBlockState(blockPos, blockState));

		pocket.expand(thickness);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("layers", layers)
				.toString();
	}

	public static class Layer {
		private final String blockStateString;
		private final String thickness;
		private Equation thicknessEquation;
		private final BlockState blockState;

		public Layer(String blockStateString, String thickness) {
			this.blockStateString = blockStateString;
			this.thickness = thickness;
			try {
				this.thicknessEquation = Equation.parse(thickness);
			} catch (Equation.EquationParseException e) {
				LOGGER.error("Could not parse layer thickness equation. Defaulting to 1");
				this.thicknessEquation = variableMap -> 1d;
			}

			this.blockState = SchematicBlockPalette.Entry.to(blockStateString).getOrThrow(false, LOGGER::error);
		}

		public BlockState getBlockState() {
			return blockState;
		}

		public int getThickness(Map<String, Double> variableMap) {
			return (int) thicknessEquation.apply(variableMap);
		}

		public NbtCompound toNbt() {
			NbtCompound nbt = new NbtCompound();
			nbt.putString("block_state", blockStateString);
			nbt.putString("thickness", thickness);
			return nbt;
		}

		public static Layer fromNbt(NbtCompound nbt) throws CommandSyntaxException {
			return new Layer(nbt.getString("block_state"), nbt.getString("thickness"));
		}
	}
}
