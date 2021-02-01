package org.dimdev.dimdoors.pockets.virtual;

import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.world.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.pockets.TemplateUtils;
import org.dimdev.dimdoors.pockets.virtual.modifier.Modifier;
import org.dimdev.dimdoors.util.Location;
import org.dimdev.dimdoors.util.PocketGenerationParameters;
import org.dimdev.dimdoors.world.pocket.Pocket;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class VirtualGeneratorPocket extends VirtualSingularPocket {
	private static final Logger LOGGER = LogManager.getLogger();
	private final List<Modifier> modifierList = new ArrayList<>();

	public VirtualGeneratorPocket fromTag(CompoundTag tag) {
		if (!tag.contains("modifiers")) return this;
		ListTag modifiersTag = tag.getList("modifiers", 10);
		for (int i = 0; i < modifiersTag.size(); i++) {
			modifierList.add(Modifier.deserialize(modifiersTag.getCompound(i)));
		}
		return this;
	}

	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);

		ListTag modifiersTag = new ListTag();
		for (Modifier modifier : modifierList) {
			modifiersTag.add(modifier.toTag(new CompoundTag()));
		}
		if (modifiersTag.size() > 0) tag.put("modifiers", modifiersTag);
		return tag;
	}

	public void applyModifiers(Pocket pocket, PocketGenerationParameters parameters) {
		for (Modifier modifier : modifierList) {
			modifier.apply(pocket, parameters);
		}
	}

	public void setup(Pocket pocket, PocketGenerationParameters parameters, boolean setupLootTables) {
		ServerWorld world = DimensionalDoorsInitializer.getWorld(pocket.world);
		List<RiftBlockEntity> rifts = new ArrayList<>();

		pocket.getBlockEntities().forEach((blockPos, blockEntity) -> {
			if (blockEntity instanceof RiftBlockEntity) {
				LOGGER.debug("Rift found in pocket at " + blockPos);
				RiftBlockEntity rift = (RiftBlockEntity) blockEntity;
				rift.getDestination().setLocation(new Location((ServerWorld) Objects.requireNonNull(rift.getWorld()), rift.getPos()));
				rifts.add(rift);
			} else if (setupLootTables && blockEntity instanceof Inventory) {
				Inventory inventory = (Inventory) blockEntity;
				if (inventory.isEmpty()) {
					if (blockEntity instanceof ChestBlockEntity || blockEntity instanceof DispenserBlockEntity) {
						TemplateUtils.setupLootTable(world, blockEntity, inventory, LOGGER);
						if (inventory.isEmpty()) {
							LOGGER.error(", however Inventory is: empty!");
						}
					}
				}
			}
		});
		TemplateUtils.registerRifts(rifts, parameters.getLinkTo(), parameters.getLinkProperties(), pocket);
		pocket.virtualLocation = parameters.getSourceVirtualLocation(); //TODO: this makes very little sense
	}
}
