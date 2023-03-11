package org.dimdev.dimdoors.world.pocket.type.addon;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.EntityUtils;
import org.dimdev.dimdoors.block.AncientFabricBlock;
import org.dimdev.dimdoors.block.FabricBlock;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.dimdev.dimdoors.world.pocket.type.PocketColor;
import org.dimdev.dimdoors.world.pocket.type.PrivatePocket;

public class DyeableAddon implements PocketAddon {
	public static ResourceLocation ID = DimensionalDoors.id("dyeable");

	private static final int BLOCKS_PAINTED_PER_DYE = 1000000;

	protected PocketColor dyeColor = PocketColor.WHITE;
	private PocketColor nextDyeColor = PocketColor.NONE;
	private int count = 0;

	private static int amountOfDyeRequiredToColor(Pocket pocket) {
		int outerVolume = pocket.getBox().getYSpan() * pocket.getBox().getZSpan() * pocket.getBox().getZSpan();
		int innerVolume = (pocket.getBox().getYSpan() - 5) * (pocket.getBox().getZSpan() - 5) * (pocket.getBox().getZSpan() - 5);

		return Math.max((outerVolume - innerVolume) / BLOCKS_PAINTED_PER_DYE, 1);
	}

	private void repaint(Pocket pocket, DyeColor dyeColor) {
		ServerLevel serverWorld = DimensionalDoors.getWorld(pocket.getWorld());
		BlockState innerWall = ModBlocks.fabricFromDye(dyeColor).defaultBlockState();
		BlockState outerWall = ModBlocks.ancientFabricFromDye(dyeColor).defaultBlockState();

		BlockPos.betweenClosedStream(pocket.getBox()).forEach(pos -> {
			if (serverWorld.getBlockState(pos).getBlock() instanceof AncientFabricBlock) {
				serverWorld.setBlockAndUpdate(pos, outerWall);
			} else if (serverWorld.getBlockState(pos).getBlock() instanceof FabricBlock) {
				serverWorld.setBlockAndUpdate(pos, innerWall);
			}
		});
	}

	public boolean addDye(Pocket pocket, Entity entity, DyeColor dyeColor) {
		PocketColor color = PocketColor.from(dyeColor);

		int maxDye = amountOfDyeRequiredToColor(pocket);

		if (this.dyeColor == color) {
			EntityUtils.chat(entity, MutableComponent.create(new TranslatableContents("dimdoors.pockets.dyeAlreadyAbsorbed")));
			return false;
		}

		if (this.nextDyeColor != PocketColor.NONE && this.nextDyeColor == color) {
			if (this.count + 1 > maxDye) {
				repaint(pocket, dyeColor);
				this.dyeColor = color;
				this.nextDyeColor = PocketColor.NONE;
				this.count = 0;
				EntityUtils.chat(entity, MutableComponent.create(new TranslatableContents("dimdoors.pocket.pocketHasBeenDyed", dyeColor)));
			} else {
				this.count++;
				EntityUtils.chat(entity, MutableComponent.create(new TranslatableContents("dimdoors.pocket.remainingNeededDyes", this.count, maxDye, color)));
			}
		} else {
			this.nextDyeColor = color;
			this.count = 1;
			EntityUtils.chat(entity, MutableComponent.create(new TranslatableContents("dimdoors.pocket.remainingNeededDyes", this.count, maxDye, color)));
		}
		return true;
	}

	@Override
	public boolean applicable(Pocket pocket) {
		return pocket instanceof PrivatePocket;
	}

	@Override
	public PocketAddon fromNbt(CompoundTag nbt) {

		this.dyeColor = PocketColor.from(nbt.getInt("dyeColor"));
		this.nextDyeColor = PocketColor.from(nbt.getInt("nextDyeColor"));
		this.count = nbt.getInt("count");

		return this;
	}

	@Override
	public CompoundTag toNbt(CompoundTag nbt) {
		PocketAddon.super.toNbt(nbt);

		nbt.putInt("dyeColor", this.dyeColor.getId());
		nbt.putInt("nextDyeColor", this.nextDyeColor.getId());
		nbt.putInt("count", this.count);

		return nbt;
	}

	@Override
	public PocketAddonType<? extends PocketAddon> getType() {
		return PocketAddonType.DYEABLE_ADDON;
	}

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	public interface DyeablePocketBuilder<T extends Pocket.PocketBuilder<T, ?>> extends PocketBuilderExtension<T> {
		default T dyeColor(PocketColor dyeColor) {

			this.<DyeableBuilderAddon>getAddon(ID).dyeColor = dyeColor;

			return getSelf();
		}
	}

	public static class DyeableBuilderAddon implements PocketBuilderAddon<DyeableAddon> {

		private PocketColor dyeColor = PocketColor.NONE;
		// TODO: add some Pocket#init so that we can have boolean shouldRepaintOnInit

		@Override
		public void apply(Pocket pocket) {
			DyeableAddon addon = new DyeableAddon();
			addon.dyeColor = dyeColor;
			pocket.addAddon(addon);
		}

		@Override
		public ResourceLocation getId() {
			return ID;
		}

		@Override
		public PocketBuilderAddon<DyeableAddon> fromNbt(CompoundTag nbt) {
			this.dyeColor = PocketColor.from(nbt.getInt("dye_color"));

			return this;
		}

		@Override
		public CompoundTag toNbt(CompoundTag nbt) {
			PocketBuilderAddon.super.toNbt(nbt);

			nbt.putInt("dye_color", dyeColor.getId());

			return nbt;
		}

		@Override
		public PocketAddonType<DyeableAddon> getType() {
			return PocketAddonType.DYEABLE_ADDON;
		}
	}

	public interface DyeablePocket extends AddonProvider {
		default boolean addDye(Entity entity, DyeColor dyeColor) {
			ensureIsPocket();
			if (!this.hasAddon(ID)) {
				DyeableAddon addon = new DyeableAddon();
				this.addAddon(addon);
				return addon.addDye((Pocket) this, entity, dyeColor);
			}
			return this.<DyeableAddon>getAddon(ID).addDye((Pocket) this, entity, dyeColor);
		}
	}
}
