package org.dimdev.dimdoors.world.level.component;

import com.mojang.serialization.Codec;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.chunk.LevelChunk;

//TODO: Investigate if making only a boolean is worth it.
public class ChunkLazilyGeneratedComponent {
	public static final Codec<ChunkLazilyGeneratedComponent> CODEC = Codec.BOOL.xmap(ChunkLazilyGeneratedComponent::new, ChunkLazilyGeneratedComponent::hasBeenLazyGenned);

	private boolean hasBeenLazyGenned = false;

	public ChunkLazilyGeneratedComponent(boolean hasBeenLazyGenned) {
		this.hasBeenLazyGenned = hasBeenLazyGenned;
	}

	public boolean hasBeenLazyGenned() {
		return hasBeenLazyGenned;
	}

	public void setGenned() {
		if (hasBeenLazyGenned) throw new UnsupportedOperationException("This chunk seems to have already been lazily generated!");
		hasBeenLazyGenned = true;
	}

	@ExpectPlatform
	public static ChunkLazilyGeneratedComponent get(LevelChunk chunk) {
		throw new RuntimeException();
	}

	public void readFromNbt(CompoundTag nbt) {
		if (nbt.contains("has_been_lazy_genned", Tag.TAG_INT)) {
			hasBeenLazyGenned = nbt.getInt("has_been_lazy_genned") == 1;
		}
	}

	public void writeToNbt(CompoundTag nbt) {
		if (hasBeenLazyGenned) {
			nbt.putInt("has_been_lazy_genned", 1);
		}
	}
}
