package org.dimdev.dimdoors.world.pocket.type;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.block.AncientFabricBlock;
import org.dimdev.dimdoors.block.FabricBlock;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.util.EntityUtils;
import org.dimdev.dimdoors.world.pocket.type.addon.DyeablePocket;

public class PrivatePocket extends Pocket implements DyeablePocket {
	public static String KEY = "private_pocket";

	private static final int BLOCKS_PAINTED_PER_DYE = 1000000;

	protected PocketColor dyeColor = PocketColor.WHITE;
	private PocketColor nextDyeColor = PocketColor.NONE;
	private int count = 0;


	@Override
	public CompoundTag toTag(CompoundTag tag) {
		tag = super.toTag(tag);

		tag.putInt("dyeColor", this.dyeColor.getId());
		tag.putInt("nextDyeColor", this.nextDyeColor.getId());
		tag.putInt("count", this.count);

		return tag;
	}

	@Override
	public Pocket fromTag(CompoundTag tag) {
		super.fromTag(tag);

		this.dyeColor = PocketColor.from(tag.getInt("dyeColor"));
		this.nextDyeColor = PocketColor.from(tag.getInt("nextDyeColor"));
		this.count = tag.getInt("count");

		return this;
	}

	@Override
	public void setDyeColor(PocketColor dyeColor) {

	}

	public boolean addDye(Entity entity, DyeColor dyeColor) {
		PocketColor color = PocketColor.from(dyeColor);

		int maxDye = amountOfDyeRequiredToColor(this);

		if (this.dyeColor == color) {
			EntityUtils.chat(entity, new TranslatableText("dimdoors.pockets.dyeAlreadyAbsorbed"));
			return false;
		}

		if (this.nextDyeColor != PocketColor.NONE && this.nextDyeColor == color) {
			if (this.count + 1 > maxDye) {
				repaint(dyeColor);
				this.dyeColor = color;
				this.nextDyeColor = PocketColor.NONE;
				this.count = 0;
				EntityUtils.chat(entity, new TranslatableText("dimdoors.pocket.pocketHasBeenDyed", dyeColor));
			} else {
				this.count++;
				EntityUtils.chat(entity, new TranslatableText("dimdoors.pocket.remainingNeededDyes", this.count, maxDye, color));
			}
		} else {
			this.nextDyeColor = color;
			this.count = 1;
			EntityUtils.chat(entity, new TranslatableText("dimdoors.pocket.remainingNeededDyes", this.count, maxDye, color));
		}
		return true;
	}

	private void repaint(DyeColor dyeColor) {
		ServerWorld serverWorld = DimensionalDoorsInitializer.getWorld(getWorld());
		BlockState innerWall = ModBlocks.fabricFromDye(dyeColor).getDefaultState();
		BlockState outerWall = ModBlocks.ancientFabricFromDye(dyeColor).getDefaultState();

		BlockPos.stream(box).forEach(pos -> {
			if (serverWorld.getBlockState(pos).getBlock() instanceof AncientFabricBlock) {
				serverWorld.setBlockState(pos, outerWall);
			} else if (serverWorld.getBlockState(pos).getBlock() instanceof FabricBlock) {
				serverWorld.setBlockState(pos, innerWall);
			}
		});
	}

	private static int amountOfDyeRequiredToColor(Pocket pocket) {
		int outerVolume = pocket.box.getBlockCountX() * pocket.box.getBlockCountY() * pocket.box.getBlockCountZ();
		int innerVolume = (pocket.box.getBlockCountX() - 5) * (pocket.box.getBlockCountY() - 5) * (pocket.box.getBlockCountZ() - 5);

		return Math.max((outerVolume - innerVolume) / BLOCKS_PAINTED_PER_DYE, 1);
	}

	public static PrivatePocketBuilder<?, PrivatePocket> builderPrivatePocket() {
		return new PrivatePocketBuilder<>(AbstractPocketType.PRIVATE_POCKET);
	}

	public static class PrivatePocketBuilder<P extends PrivatePocketBuilder<P, T>, T extends PrivatePocket> extends PocketBuilder<P, T> implements DyeablePocketBuilder<P> {
		protected PrivatePocketBuilder(AbstractPocketType<T> type) {
			super(type);
		}

		@Override
		public void initAddons() {
			addAddon(DyeableBuilderAddon.class, new DyeableBuilderAddon());
			this.dyeColor(PocketColor.WHITE);
		}
	}
}
