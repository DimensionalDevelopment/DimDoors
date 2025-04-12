package org.dimdev.dimdoors.pockets.generator;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.math.Equation;
import org.dimdev.dimdoors.pockets.TemplateUtils;
import org.dimdev.dimdoors.pockets.modifier.LazyModifier;
import org.dimdev.dimdoors.pockets.modifier.Modifier;
import org.dimdev.dimdoors.pockets.modifier.RiftManager;
import org.dimdev.dimdoors.world.pocket.type.LazyGenerationPocket;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class LazyPocketGenerator extends PocketGenerator {
	private static final Logger LOGGER = LogManager.getLogger();

	public static boolean currentlyGenerating = false;
	public static Queue<LevelChunk> generationQueue = new LinkedList<>();

	protected List<LazyModifier> lazyModifierList = new ArrayList<>();

	public static  <T extends LazyPocketGenerator> Products.P6<RecordCodecBuilder.Mu<T>, CompoundTag, Equation, Boolean, List<Modifier>, List<String>, List<LazyModifier>> commonLazyFields(RecordCodecBuilder.Instance<T> instance) {
		return commonFields(instance).and(
				Modifier.CODEC.flatXmap(a -> a instanceof LazyModifier lazyModifier ? DataResult.success(lazyModifier) : DataResult.error(() -> "Modifier wasn't lazy."), DataResult::success).listOf().xmap(a -> (List<LazyModifier>) new ArrayList<>(a), a -> a).optionalFieldOf("lazy_modifiers", new ArrayList<>()).forGetter(a -> a.lazyModifierList));
	}


	public LazyPocketGenerator(CompoundTag builder, Equation weight, boolean setupLoot, List<Modifier> modifierList, List<String> tags, List<LazyModifier> lazyModifierList) {
		super(builder, weight, setupLoot, modifierList, tags);
        this.lazyModifierList = lazyModifierList;
    }

	public void generateChunk(LazyGenerationPocket pocket, LevelChunk chunk) {
		var provider = chunk.getLevel().registryAccess();

		lazyModifierList.forEach(modifier -> modifier.applyToChunk(pocket, chunk, provider));
	}

	// LazyPocketGenerator handles attaching itself so that it can drop itself if it has already generated everything necessary.
	public void attachToPocket(LazyGenerationPocket pocket) {
		// We assume that this LazyPocketGenerator has not been cloned yet if the modifier list has any entries since it should be empty at this stage
//		if (!this.lazyModifierList.isEmpty()) { TODO: Find out if we need this.
//			throw new UnsupportedOperationException("Cannot attach LazyPocketGenerator that has not been cloned yet to pocket");
//		}
		pocket.attachGenerator(this);
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
