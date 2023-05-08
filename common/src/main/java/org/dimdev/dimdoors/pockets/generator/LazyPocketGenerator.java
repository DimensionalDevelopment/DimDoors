package org.dimdev.dimdoors.pockets.generator;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.DimensionalDoors;
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
	public static Queue<LevelChunk> generationQueue = new LinkedList<>();


	protected List<LazyModifier> lazyModifierList = new ArrayList<>();

	public void generateChunk(LazyGenerationPocket pocket, LevelChunk chunk) {
		lazyModifierList.forEach(modifier -> modifier.applyToChunk(pocket, chunk));
	}

	// LazyPocketGenerator handles attaching itself so that it can drop itself if it has already generated everything necessary.
	public void attachToPocket(LazyGenerationPocket pocket) {
		// We assume that this LazyPocketGenerator has not been cloned yet if the modifier list has any entries since it should be empty at this stage
		if (this.modifierList.size() > 0) throw new UnsupportedOperationException("Cannot attach LazyPocketGenerator that has not been cloned yet to pocket");
		pocket.attachGenerator(this);
	}

	@Override
	public PocketGenerator fromNbt(CompoundTag nbt, ResourceManager manager) {
		super.fromNbt(nbt, manager);

		if (nbt.contains("lazy_modifiers")) {
			ListTag modifiersNbt = nbt.getList("lazy_modifiers", 10);
			for (int i = 0; i < modifiersNbt.size(); i++) {
				// TODO: skip deserialization of single Modifiers on Exception.
				// TODO: Modifier via ResourceManager
				lazyModifierList.add((LazyModifier) Modifier.deserialize(modifiersNbt.getCompound(i), manager));
			}
		}

		if (nbt.contains("lazy_modifier_references")) {
			ListTag modifiersNbt = nbt.getList("lazy_modifier_references", Tag.TAG_STRING);
			for (Tag nbtElement : modifiersNbt) {
				// TODO: skip deserialization of single Modifiers on Exception.
				// TODO: Modifier via ResourceManager
				lazyModifierList.add((LazyModifier) Modifier.deserialize(nbtElement, manager));
			}
		}

		return this;
	}

	@Override
	public CompoundTag toNbtInternal(CompoundTag nbt, boolean allowReference) {
		super.toNbtInternal(nbt, allowReference);

		if (lazyModifierList.size() > 0) {
			List<Tag> lazyModNbts = lazyModifierList.stream().map(lazyModifier -> lazyModifier.toNbt(new CompoundTag(), allowReference)).toList();

			ListTag lazyModifiersNbt = new ListTag();
			lazyModifiersNbt.addAll(lazyModNbts.stream().filter(CompoundTag.class::isInstance).toList());
			nbt.put("lazy_modifiers", lazyModifiersNbt);

			ListTag lazyModifierReferences = new ListTag();
			lazyModifiersNbt.addAll(lazyModNbts.stream().filter(StringTag.class::isInstance).toList());
			nbt.put("lazy_modifier_references", lazyModifierReferences);
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

	public void setupChunk(Pocket pocket, ChunkAccess chunk, boolean setupLootTables) {
		MinecraftServer server = DimensionalDoors.getServer();
		chunk.getBlockEntitiesPos().stream().map(chunk::getBlockEntity).forEach(blockEntity -> { // RiftBlockEntities should already be initialized here
			if (setupLootTables && blockEntity instanceof Container inventory) {
				server.tell(new TickTask(server.getTickCount(), () -> {
					if (inventory.isEmpty()) {
						if (blockEntity instanceof ChestBlockEntity || blockEntity instanceof DispenserBlockEntity) {
							TemplateUtils.setupLootTable(DimensionalDoors.getWorld(pocket.getWorld()), blockEntity, inventory, LOGGER);
						}
					}
				}));
			}
		});
	}
}
