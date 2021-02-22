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
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.chunk.Chunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.dimdev.dimdoors.util.BlockBoxUtil;
import org.dimdev.dimdoors.util.PocketGenerationParameters;
import org.dimdev.dimdoors.util.math.Equation;
import org.dimdev.dimdoors.util.schematic.SchematicBlockPalette;
import org.dimdev.dimdoors.world.pocket.type.LazyGenerationPocket;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

public class ShellModifier implements LazyModifier {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final String KEY = "shell";

	private final List<Layer> layers = new ArrayList<>();
	private BlockBox boxToDrawAround;

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		LazyModifier.super.toTag(tag);

		ListTag layersTag = new ListTag();
		for (Layer layer : layers) {
			layersTag.add(layer.toTag());
		}
		tag.put("layers", layersTag);
		if (boxToDrawAround != null) {
			tag.put("box_to_draw_around", boxToDrawAround.toNbt());
		}

		return tag;
	}

	@Override
	public void applyToChunk(LazyGenerationPocket pocket, Chunk chunk) {

		int boxExpansion = 0;
		for (Layer layer : layers) {
			int thickness = layer.getThickness(pocket.toVariableMap(new HashMap<>()));
			final BlockState blockState = layer.getBlockState();

			ChunkPos pos = chunk.getPos();
			BlockBox chunkBox = BlockBox.create(pos.getStartX(), chunk.getBottomY(), pos.getStartZ(), pos.getEndX(), chunk.getTopY(), pos.getEndZ());

			BlockBox temp;


			// x-planes
			temp = BlockBox.create(boxToDrawAround.maxX + 1 + boxExpansion, boxToDrawAround.minY - thickness - boxExpansion, boxToDrawAround.minZ - thickness - boxExpansion, boxToDrawAround.maxX + thickness + boxExpansion, boxToDrawAround.maxY + thickness + boxExpansion, boxToDrawAround.maxZ + thickness + boxExpansion);
			temp = BlockBoxUtil.intersection(temp, chunkBox);
			if (BlockBoxUtil.isRealBox(temp)) BlockPos.stream(temp)
					.forEach(blockPos -> {
						if(chunk.getBlockState(blockPos).isAir()) chunk.setBlockState(blockPos, blockState, false);
					});
			temp = BlockBox.create(boxToDrawAround.minX - 1 - boxExpansion, boxToDrawAround.minY - thickness - boxExpansion, boxToDrawAround.minZ - thickness - boxExpansion, boxToDrawAround.minX - thickness - boxExpansion, boxToDrawAround.maxY + thickness + boxExpansion, boxToDrawAround.maxZ + thickness + boxExpansion);
			temp = BlockBoxUtil.intersection(temp, chunkBox);
			if (BlockBoxUtil.isRealBox(temp)) BlockPos.stream(temp)
					.forEach(blockPos -> {
						if(chunk.getBlockState(blockPos).isAir()) chunk.setBlockState(blockPos, blockState, false);
					});

			// y-planes
			temp = BlockBox.create(boxToDrawAround.minX - boxExpansion, boxToDrawAround.maxY + 1 + boxExpansion, boxToDrawAround.minZ - thickness - boxExpansion, boxToDrawAround.maxX + boxExpansion, boxToDrawAround.maxY + thickness + boxExpansion, boxToDrawAround.maxZ + thickness + boxExpansion);
			temp = BlockBoxUtil.intersection(temp, chunkBox);
			if (BlockBoxUtil.isRealBox(temp)) BlockPos.stream(temp)
					.forEach(blockPos -> {
						if(chunk.getBlockState(blockPos).getBlock() instanceof AirBlock) chunk.setBlockState(blockPos, blockState, false);
					});
			temp = BlockBox.create(boxToDrawAround.minX - boxExpansion, boxToDrawAround.minY - 1 - boxExpansion, boxToDrawAround.minZ - thickness - boxExpansion, boxToDrawAround.maxX + boxExpansion, boxToDrawAround.minY - thickness - boxExpansion, boxToDrawAround.maxZ + thickness + boxExpansion);
			temp = BlockBoxUtil.intersection(temp, chunkBox);
			if (BlockBoxUtil.isRealBox(temp)) BlockPos.stream(temp)
					.forEach(blockPos -> {
						if(chunk.getBlockState(blockPos).isAir()) chunk.setBlockState(blockPos, blockState, false);
					});

			// z-planes
			temp = BlockBox.create(boxToDrawAround.minX - boxExpansion, boxToDrawAround.minY - boxExpansion, boxToDrawAround.minZ - 1 - boxExpansion, boxToDrawAround.maxX + boxExpansion, boxToDrawAround.maxY + boxExpansion, boxToDrawAround.minZ - thickness - boxExpansion);
			temp = BlockBoxUtil.intersection(temp, chunkBox);
			if (BlockBoxUtil.isRealBox(temp)) BlockPos.stream(temp)
					.forEach(blockPos -> {
						if(chunk.getBlockState(blockPos).isAir()) chunk.setBlockState(blockPos, blockState, false);
					});
			temp = BlockBox.create(boxToDrawAround.minX - boxExpansion, boxToDrawAround.minY - boxExpansion, boxToDrawAround.maxZ + 1 + boxExpansion, boxToDrawAround.maxX + boxExpansion, boxToDrawAround.maxY + boxExpansion, boxToDrawAround.maxZ + thickness + boxExpansion);
			temp = BlockBoxUtil.intersection(temp, chunkBox);
			if (BlockBoxUtil.isRealBox(temp)) BlockPos.stream(temp)
					.forEach(blockPos -> {
						if(chunk.getBlockState(blockPos).isAir()) chunk.setBlockState(blockPos, blockState, false);
					});

			boxExpansion += thickness;
		}
	}

	@Override
	public Modifier fromTag(CompoundTag tag) {
		for (Tag layerTag : tag.getList("layers", NbtType.COMPOUND)) {
			CompoundTag compoundTag = (CompoundTag) layerTag;
			try {
				Layer layer = Layer.fromTag(compoundTag);
				layers.add(layer);
			} catch (CommandSyntaxException e) {
				LOGGER.error("could not parse Layer: " + compoundTag.toString(), e);
			}
		}

		if (tag.contains("box_to_draw_around", NbtType.INT_ARRAY)) {
			int[] box = tag.getIntArray("box_to_draw_around");
			boxToDrawAround = BlockBox.create(box[0], box[1], box[2], box[3], box[4], box[5]);
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
	public void apply(PocketGenerationParameters parameters, RiftManager manager) {
		Pocket pocket = manager.getPocket();
		if (pocket instanceof LazyGenerationPocket) {
			Map<String, Double> variableMap = pocket.toVariableMap(new HashMap<>());
			BlockBox pocketBox = pocket.getBox();
			boxToDrawAround = BlockBox.create(pocketBox.minX, pocketBox.minY, pocketBox.minZ, pocketBox.maxX, pocketBox.maxY, pocketBox.maxZ);
			layers.forEach(layer -> pocket.expand(layer.getThickness(variableMap)));
		} else {
			layers.forEach(layer -> drawLayer(layer, manager.getPocket(), parameters.getWorld()));
		}
	}

	@Override
	public void apply(PocketGenerationParameters parameters, Pocket.PocketBuilder<?, ?> builder) {
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
		BlockPos.stream(BlockBox.create(pocketBox.maxX + 1, pocketBox.minY - thickness, pocketBox.minZ - thickness, pocketBox.maxX + thickness, pocketBox.maxY + thickness, pocketBox.maxZ + thickness))
				.forEach(blockPos -> world.setBlockState(blockPos, blockState));
		BlockPos.stream(BlockBox.create(pocketBox.minX - 1, pocketBox.minY - thickness, pocketBox.minZ - thickness, pocketBox.minX - thickness, pocketBox.maxY + thickness, pocketBox.maxZ + thickness))
				.forEach(blockPos -> world.setBlockState(blockPos, blockState));

		// y-planes
		BlockPos.stream(BlockBox.create(pocketBox.minX, pocketBox.maxY + 1, pocketBox.minZ - thickness, pocketBox.maxX, pocketBox.maxY + thickness, pocketBox.maxZ + thickness))
				.forEach(blockPos -> world.setBlockState(blockPos, blockState));
		BlockPos.stream(BlockBox.create(pocketBox.minX, pocketBox.minY - 1, pocketBox.minZ - thickness, pocketBox.maxX, pocketBox.minY - thickness, pocketBox.maxZ + thickness))
				.forEach(blockPos -> world.setBlockState(blockPos, blockState));

		// z-planes
		BlockPos.stream(BlockBox.create(pocketBox.minX, pocketBox.minY, pocketBox.minZ - 1, pocketBox.maxX, pocketBox.maxY, pocketBox.minZ - thickness))
				.forEach(blockPos -> world.setBlockState(blockPos, blockState));
		BlockPos.stream(BlockBox.create(pocketBox.minX, pocketBox.minY, pocketBox.maxZ + 1, pocketBox.maxX, pocketBox.maxY, pocketBox.maxZ + thickness))
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
				LOGGER.error("Could not parse layer thickness equation");
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

		public CompoundTag toTag() {
			CompoundTag tag = new CompoundTag();
			tag.putString("block_state", blockStateString);
			tag.putString("thickness", thickness);
			return tag;
		}

		public static Layer fromTag(CompoundTag tag) throws CommandSyntaxException {
			return new Layer(tag.getString("block_state"), tag.getString("thickness"));
		}
	}
}
