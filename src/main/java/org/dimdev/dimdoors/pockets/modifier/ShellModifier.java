package org.dimdev.dimdoors.pockets.modifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.MoreObjects;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.util.math.Vec3i;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.dimdev.dimdoors.util.PocketGenerationParameters;
import org.dimdev.dimdoors.util.math.Equation;
import org.dimdev.dimdoors.util.schematic.v2.SchematicBlockPalette;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

public class ShellModifier implements Modifier {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final String KEY = "shell";

	private final List<Layer> layers = new ArrayList<>();

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		Modifier.super.toTag(tag);

		ListTag layersTag = new ListTag();
		for (Layer layer : layers) {
			layersTag.add(layer.toTag());
		}
		tag.put("layers", layersTag);

		return tag;
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
		layers.forEach(layer -> drawLayer(layer, manager.getPocket(), parameters.getWorld()));
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
