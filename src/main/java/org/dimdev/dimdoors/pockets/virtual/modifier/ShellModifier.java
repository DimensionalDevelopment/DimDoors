package org.dimdev.dimdoors.pockets.virtual.modifier;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.util.NbtType;
import org.dimdev.dimdoors.util.PocketGenerationParameters;
import org.dimdev.dimdoors.world.pocket.Pocket;

public class ShellModifier implements Modifier{
	private List<Layer> layers = new ArrayList<>();

	private int total = 0;

	private void calculate() {
		total = layers.stream().mapToInt(Layer::getThickness).sum();
		double half = total * 0.5;

		Box box = new Box(0,0,0,0,0,0).offset(half, half, half);

		layers.forEach(layer -> layer.adjust(box));
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		ListTag layersTag = new ListTag();

		for (Layer layer : layers) {
			layersTag.add(layer.toTag());
		}

		tag.put("layers", layersTag);

		return tag;
	}

	@Override
	public Modifier fromTag(CompoundTag tag) {
		layers.clear();

		for (Tag layerTag : tag.getList("layers", NbtType.COMPOUND)) {
			CompoundTag compoundTag = (CompoundTag) layerTag;
			Layer layer = Layer.fromTag(compoundTag);
			layers.add(layer);
		}

		calculate();

		return this;
	}

	@Override
	public ModifierType<? extends Modifier> getType() {
		return ModifierType.SHELL_MODIFIER_TYPE;
	}

	@Override
	public String getKey() {
		return "shell";
	}

	@Override
	public void apply(Pocket pocket, PocketGenerationParameters parameters) {
		for (int x = 0; x < total; x++) {
			for (int y = 0; y < total; y++) {
				for (int z = 0; z < total; z++) {
					parameters.getWorld().setBlockState(pocket.getOrigin().add(x,y,z), getBlockState(x,y,z));
				}
			}
		}

		pocket.setSize(total, total, total);
	}

	private BlockState getBlockState(int x, int y, int z) {
		return layers.stream().filter(layer -> layer.contains(x, y, z)).findFirst().map(Layer::getBlock).orElse(Blocks.AIR).getDefaultState();
	}

	public static class Layer {
		private final Identifier material;
		private final int thickness;

		private Box box;

		public Layer(Identifier material, int thickness) {
			this.material = material;
			this.thickness = thickness;
		}

		public Identifier getMaterial() {
			return material;
		}

		public int getThickness() {
			return thickness;
		}

		public boolean contains(int x, int y, int z) {
			return box.contains(x, y, z);
		}

		public Block getBlock() {
			return Registry.BLOCK.get(material);
		}

		public void adjust(Box box) {
			double half = thickness * 0.5d;
			this.box = box.expand(thickness);
		}

		public CompoundTag toTag() {
			CompoundTag tag = new CompoundTag();
			tag.putString("material", material.toString());
			tag.putInt("thickness", thickness);
			return tag;
		}

		public static Layer fromTag(CompoundTag tag) {
			return new Layer(Identifier.tryParse(tag.getString("material")), tag.getInt("thickness"));
		}

	}
}

