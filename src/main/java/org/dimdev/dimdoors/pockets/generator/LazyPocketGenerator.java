package org.dimdev.dimdoors.pockets.generator;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.chunk.Chunk;
import org.dimdev.dimdoors.pockets.modifier.LazyModifier;
import org.dimdev.dimdoors.pockets.modifier.Modifier;
import org.dimdev.dimdoors.pockets.modifier.RiftManager;
import org.dimdev.dimdoors.world.pocket.type.LazyGenerationPocket;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.*;
import java.util.stream.Collectors;

public abstract class LazyPocketGenerator extends PocketGenerator {
	public static boolean currentlyGenerating = false;
	public static Queue<Chunk> generationQueue = new LinkedList<>();


	private List<LazyModifier> lazyModifierList = new ArrayList<>();

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
	public PocketGenerator fromTag(CompoundTag tag) {
		super.fromTag(tag);

		if (tag.contains("lazy_modifiers")) {
			ListTag modifiersTag = tag.getList("lazy_modifiers", 10);
			for (int i = 0; i < modifiersTag.size(); i++) {
				lazyModifierList.add((LazyModifier) Modifier.deserialize(modifiersTag.getCompound(i)));
			}
		}

		return this;
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);

		if (lazyModifierList.size() > 0) {
			List<CompoundTag> lazyModTags = lazyModifierList.stream().map(lazyModifier -> lazyModifier.toTag(new CompoundTag())).collect(Collectors.toList());
			ListTag lazyModifiersTag = new ListTag();
			lazyModifiersTag.addAll(lazyModTags);
			tag.put("lazy_modifiers", lazyModifiersTag);
		}

		return tag;
	}

	@Override
	public RiftManager getRiftManager(Pocket pocket) {
		return new RiftManager(pocket, true);
	}

	public void attachLazyModifiers(Collection<LazyModifier> lazyModifiers) {
		this.lazyModifierList.addAll(lazyModifiers);
	}

	public LazyPocketGenerator cloneWithLazyModifiers() {
		LazyPocketGenerator clone = cloneWithEmptyModifiers();
		clone.attachLazyModifiers(this.modifierList.stream().filter(LazyModifier.class::isInstance).map(LazyModifier.class::cast).collect(Collectors.toList()));
		return clone;
	}

	public LazyPocketGenerator cloneWithEmptyModifiers() {
		LazyPocketGenerator generator = getNewInstance();

		// Builder/ weight related stuff seems irrelevant here
		generator.setupLoot = this.setupLoot;

		return generator;
	}

	abstract public LazyPocketGenerator getNewInstance();
}
