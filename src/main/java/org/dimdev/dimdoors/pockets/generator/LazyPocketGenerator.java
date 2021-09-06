package org.dimdev.dimdoors.pockets.generator;

import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTask;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.pockets.TemplateUtils;
import org.dimdev.dimdoors.pockets.modifier.LazyModifier;
import org.dimdev.dimdoors.pockets.modifier.Modifier;
import org.dimdev.dimdoors.pockets.modifier.RiftManager;
import org.dimdev.dimdoors.world.pocket.type.LazyGenerationPocket;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.*;
import java.util.stream.Collectors;

public abstract class LazyPocketGenerator extends PocketGenerator {
	private static final Logger LOGGER = LogManager.getLogger();

	public static boolean currentlyGenerating = false;
	public static Queue<Chunk> generationQueue = new LinkedList<>();


	protected List<LazyModifier> lazyModifierList = new ArrayList<>();

	public void generateChunk(LazyGenerationPocket pocket, Chunk chunk) {
		lazyModifierList.forEach(modifier -> modifier.applyToChunk(pocket, chunk));
	}

	// LazyPocketGenerator handles attaching itself so that it can drop itself if it has already generated everything necessary.
	public void attachToPocket(LazyGenerationPocket pocket) {
		// We assume that this LazyPocketGenerator has not been cloned yet if the modifier list has any entries since it should be empty at this stage
		if (this.modifierList.size() > 0) throw new UnsupportedOperationException("Cannot attach LazyPocketGenerator that has not been cloned yet to pocket");
		pocket.attachGenerator(this);
	}

	@Override
	public PocketGenerator fromNbt(NbtCompound nbt) {
		super.fromNbt(nbt);

		if (nbt.contains("lazy_modifiers")) {
			NbtList modifiersNbt = nbt.getList("lazy_modifiers", 10);
			for (int i = 0; i < modifiersNbt.size(); i++) {
				// TODO: skip deserialization of single Modifiers on Exception.
				lazyModifierList.add((LazyModifier) Modifier.deserialize(modifiersNbt.getCompound(i)));
			}
		}

		return this;
	}

	@Override
	public NbtCompound toNbt(NbtCompound nbt) {
		super.toNbt(nbt);

		if (lazyModifierList.size() > 0) {
			List<NbtCompound> lazyModNbts = lazyModifierList.stream().map(lazyModifier -> lazyModifier.toNbt(new NbtCompound())).collect(Collectors.toList());
			NbtList lazyModifiersNbt = new NbtList();
			lazyModifiersNbt.addAll(lazyModNbts);
			nbt.put("lazy_modifiers", lazyModifiersNbt);
		}

		return nbt;
	}

	@Override
	public RiftManager getRiftManager(Pocket pocket) {
		if (pocket instanceof LazyGenerationPocket) {
			return new RiftManager(pocket, true);
		} else {
			return new RiftManager(pocket, false);
		}
	}

	public void attachLazyModifiers(Collection<LazyModifier> lazyModifiers) {
		this.lazyModifierList.addAll(lazyModifiers);
	}

	public LazyPocketGenerator cloneWithLazyModifiers(BlockPos originalOrigin) {
		LazyPocketGenerator clone = cloneWithEmptyModifiers(originalOrigin);
		clone.attachLazyModifiers(this.modifierList.stream().filter(LazyModifier.class::isInstance).map(LazyModifier.class::cast).collect(Collectors.toList()));
		return clone;
	}

	public LazyPocketGenerator cloneWithEmptyModifiers(BlockPos originalOrigin) {
		LazyPocketGenerator generator = getNewInstance();

		// Builder/ weight related stuff seems irrelevant here
		generator.setupLoot = this.setupLoot;

		return generator;
	}

	public void setSetupLoot(Boolean setupLoot) {
		this.setupLoot = setupLoot;
	}

	abstract public LazyPocketGenerator getNewInstance();

	public void setupChunk(Pocket pocket, Chunk chunk, boolean setupLootTables) {
		MinecraftServer server = DimensionalDoorsInitializer.getServer();
		chunk.getBlockEntityPositions().stream().map(chunk::getBlockEntity).forEach(blockEntity -> { // RiftBlockEntities should already be initialized here
			if (setupLootTables && blockEntity instanceof Inventory) {
				Inventory inventory = (Inventory) blockEntity;
				server.send(new ServerTask(server.getTicks(), () -> {
					if (inventory.isEmpty()) {
						if (blockEntity instanceof ChestBlockEntity || blockEntity instanceof DispenserBlockEntity) {
							TemplateUtils.setupLootTable(DimensionalDoorsInitializer.getWorld(pocket.getWorld()), blockEntity, inventory, LOGGER);
						}
					}
				}));
			}
		});
	}
}
