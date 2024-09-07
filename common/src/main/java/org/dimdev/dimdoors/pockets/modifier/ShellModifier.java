package org.dimdev.dimdoors.pockets.modifier;

import com.google.common.base.MoreObjects;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.api.util.BlockBoxUtil;
import org.dimdev.dimdoors.api.util.math.Equation;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.util.schematic.SchematicBlockPalette;
import org.dimdev.dimdoors.world.pocket.type.LazyGenerationPocket;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShellModifier extends AbstractLazyModifier {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final String KEY = "shell";

	private final List<Layer> layers = new ArrayList<>();
	private BoundingBox boxToDrawAround;

	@Override
	public CompoundTag toNbtInternal(CompoundTag nbt, boolean allowReference) {
		super.toNbtInternal(nbt, allowReference);

		ListTag layersNbt = new ListTag();
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
	public void applyToChunk(LazyGenerationPocket pocket, ChunkAccess chunk) {

		int boxExpansion = 0;
		for (Layer layer : layers) {
			int thickness = layer.getThickness(pocket.toVariableMap(new HashMap<>()));
			final BlockState blockState = layer.getBlockState();

			BoundingBox chunkBox = BlockBoxUtil.getBox(chunk);

			BoundingBox temp;


			// x-planes
			temp = BoundingBox.fromCorners(new Vec3i(boxToDrawAround.maxX() + 1 + boxExpansion, boxToDrawAround.minY() - thickness - boxExpansion, boxToDrawAround.minZ() - thickness - boxExpansion), new Vec3i(boxToDrawAround.maxX() + thickness + boxExpansion, boxToDrawAround.maxY() + thickness + boxExpansion, boxToDrawAround.maxZ() + thickness + boxExpansion));
			if (temp.intersects(chunkBox)) {
				temp = BlockBoxUtil.intersect(temp, chunkBox);
				BlockPos.betweenClosedStream(temp)
						.forEach(blockPos -> {
							if (chunk.getBlockState(blockPos).isAir()) chunk.setBlockState(blockPos, blockState, false);
						});
			}
			temp = BoundingBox.fromCorners(new Vec3i(boxToDrawAround.minX() - 1 - boxExpansion, boxToDrawAround.minY() - thickness - boxExpansion, boxToDrawAround.minZ() - thickness - boxExpansion), new Vec3i(boxToDrawAround.minX() - thickness - boxExpansion, boxToDrawAround.maxY() + thickness + boxExpansion, boxToDrawAround.maxZ() + thickness + boxExpansion));
			if (temp.intersects(chunkBox)) {
				temp = BlockBoxUtil.intersect(temp, chunkBox);
				BlockPos.betweenClosedStream(temp)
						.forEach(blockPos -> {
							if (chunk.getBlockState(blockPos).isAir()) chunk.setBlockState(blockPos, blockState, false);
						});
			}

			// y-planes
			temp = BoundingBox.fromCorners(new Vec3i(boxToDrawAround.minX() - boxExpansion, boxToDrawAround.maxY() + 1 + boxExpansion, boxToDrawAround.minZ() - thickness - boxExpansion), new Vec3i(boxToDrawAround.maxX() + boxExpansion, boxToDrawAround.maxY() + thickness + boxExpansion, boxToDrawAround.maxZ() + thickness + boxExpansion));
			if (temp.intersects(chunkBox)) {
				temp = BlockBoxUtil.intersect(temp, chunkBox);
				BlockPos.betweenClosedStream(temp)
						.forEach(blockPos -> {
							if (chunk.getBlockState(blockPos).getBlock() instanceof AirBlock)
								chunk.setBlockState(blockPos, blockState, false);
						});
			}
			temp = BoundingBox.fromCorners(new Vec3i(boxToDrawAround.minX() - boxExpansion, boxToDrawAround.minY() - 1 - boxExpansion, boxToDrawAround.minZ() - thickness - boxExpansion), new Vec3i(boxToDrawAround.maxX() + boxExpansion, boxToDrawAround.minY() - thickness - boxExpansion, boxToDrawAround.maxZ() + thickness + boxExpansion));
			if (temp.intersects(chunkBox)) {
				temp = BlockBoxUtil.intersect(temp, chunkBox);
				BlockPos.betweenClosedStream(temp)
						.forEach(blockPos -> {
							if (chunk.getBlockState(blockPos).isAir()) chunk.setBlockState(blockPos, blockState, false);
						});
			}

			// z-planes
			temp = BoundingBox.fromCorners(new Vec3i(boxToDrawAround.minX() - boxExpansion, boxToDrawAround.minY() - boxExpansion, boxToDrawAround.minZ() - 1 - boxExpansion), new Vec3i(boxToDrawAround.maxX() + boxExpansion, boxToDrawAround.maxY() + boxExpansion, boxToDrawAround.minZ() - thickness - boxExpansion));
			if (temp.intersects(chunkBox)) {
				temp = BlockBoxUtil.intersect(temp, chunkBox);
				BlockPos.betweenClosedStream(temp)
						.forEach(blockPos -> {
							if (chunk.getBlockState(blockPos).isAir()) chunk.setBlockState(blockPos, blockState, false);
						});
			}
			temp = BoundingBox.fromCorners(new Vec3i(boxToDrawAround.minX() - boxExpansion, boxToDrawAround.minY() - boxExpansion, boxToDrawAround.maxZ() + 1 + boxExpansion), new Vec3i(boxToDrawAround.maxX() + boxExpansion, boxToDrawAround.maxY() + boxExpansion, boxToDrawAround.maxZ() + thickness + boxExpansion));
			if (temp.intersects(chunkBox)) {
				temp = BlockBoxUtil.intersect(temp, chunkBox);
				BlockPos.betweenClosedStream(temp)
						.forEach(blockPos -> {
							if (chunk.getBlockState(blockPos).isAir()) chunk.setBlockState(blockPos, blockState, false);
						});
			}

			boxExpansion += thickness;
		}
	}

	@Override
	public Modifier fromNbt(CompoundTag nbt, ResourceManager manager) {
		for (Tag layerNbt : nbt.getList("layers", Tag.TAG_COMPOUND)) {
			CompoundTag nbtCompound = (CompoundTag) layerNbt;
			try {
				Layer layer = Layer.fromNbt(nbtCompound);
				layers.add(layer);
			} catch (CommandSyntaxException e) {
				LOGGER.error("could not parse Layer: " + nbtCompound, e);
			}
		}

		if (nbt.contains("box_to_draw_around", Tag.TAG_INT_ARRAY)) {
			int[] box = nbt.getIntArray("box_to_draw_around");
			boxToDrawAround = BoundingBox.fromCorners(new Vec3i(box[0], box[1], box[2]), new Vec3i(box[3], box[4], box[5]));
		}

		return this;
	}

	@Override
	public ModifierType<? extends Modifier> getType() {
		return ModifierType.SHELL_MODIFIER_TYPE.get();
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
			BoundingBox pocketBox = pocket.getBox();
			boxToDrawAround = BoundingBox.fromCorners(new Vec3i(pocketBox.minX(), pocketBox.minY(), pocketBox.minZ()), new Vec3i(pocketBox.maxX(), pocketBox.maxY(), pocketBox.maxZ()));
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

	private void drawLayer(Layer layer, Pocket pocket, ServerLevel world) {
		int thickness = layer.getThickness(pocket.toVariableMap(new HashMap<>()));
		final BlockState blockState = layer.getBlockState();
		BoundingBox pocketBox = pocket.getBox();

		// x-planes
		BlockPos.betweenClosedStream(BoundingBox.fromCorners(new Vec3i(pocketBox.maxX() + 1, pocketBox.minY() - thickness, pocketBox.minZ() - thickness), new Vec3i(pocketBox.maxX() + thickness, pocketBox.maxY() + thickness, pocketBox.maxZ() + thickness)))
				.forEach(blockPos -> world.setBlockAndUpdate(blockPos, blockState));
		BlockPos.betweenClosedStream(BoundingBox.fromCorners(new Vec3i(pocketBox.minX() - 1, pocketBox.minY() - thickness, pocketBox.minZ() - thickness), new Vec3i(pocketBox.minX() - thickness, pocketBox.maxY() + thickness, pocketBox.maxZ() + thickness)))
				.forEach(blockPos -> world.setBlockAndUpdate(blockPos, blockState));

		// y-planes
		BlockPos.betweenClosedStream(BoundingBox.fromCorners(new Vec3i(pocketBox.minX(), pocketBox.maxY() + 1, pocketBox.minZ() - thickness), new Vec3i(pocketBox.maxX(), pocketBox.maxY() + thickness, pocketBox.maxZ() + thickness)))
				.forEach(blockPos -> world.setBlockAndUpdate(blockPos, blockState));
		BlockPos.betweenClosedStream(BoundingBox.fromCorners(new Vec3i(pocketBox.minX(), pocketBox.minY() - 1, pocketBox.minZ() - thickness), new Vec3i(pocketBox.maxX(), pocketBox.minY() - thickness, pocketBox.maxZ() + thickness)))
				.forEach(blockPos -> world.setBlockAndUpdate(blockPos, blockState));

		// z-planes
		BlockPos.betweenClosedStream(BoundingBox.fromCorners(new Vec3i(pocketBox.minX(), pocketBox.minY(), pocketBox.minZ() - 1), new Vec3i(pocketBox.maxX(), pocketBox.maxY(), pocketBox.minZ() - thickness)))
				.forEach(blockPos -> world.setBlockAndUpdate(blockPos, blockState));
		BlockPos.betweenClosedStream(BoundingBox.fromCorners(new Vec3i(pocketBox.minX(), pocketBox.minY(), pocketBox.maxZ() + 1), new Vec3i(pocketBox.maxX(), pocketBox.maxY(), pocketBox.maxZ() + thickness)))
				.forEach(blockPos -> world.setBlockAndUpdate(blockPos, blockState));

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
				// FIXME: do we actually want to have it serialize to the broken String equation we input?
				this.thicknessEquation = Equation.newEquation(variableMap -> 1d, stringBuilder -> stringBuilder.append(thickness));
			}

			this.blockState = SchematicBlockPalette.Entry.to(blockStateString).getOrThrow(false, LOGGER::error);
		}

		public BlockState getBlockState() {
			return blockState;
		}

		public int getThickness(Map<String, Double> variableMap) {
			return (int) thicknessEquation.apply(variableMap);
		}

		public CompoundTag toNbt() {
			CompoundTag nbt = new CompoundTag();
			nbt.putString("block_state", blockStateString);
			nbt.putString("thickness", thickness);
			return nbt;
		}

		public static Layer fromNbt(CompoundTag nbt) throws CommandSyntaxException {
			return new Layer(nbt.getString("block_state"), nbt.getString("thickness"));
		}
	}
}
