package org.dimdev.dimdoors.world.level.component;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.level.chunk.LevelChunk;

public class ChunkLazilyGeneratedComponent {
	private boolean hasBeenLazyGenned = false;

	public boolean hasBeenLazyGenned() {
		return hasBeenLazyGenned;
	}

	@ExpectPlatform
	public static void setGenerated(LevelChunk chunk, boolean value) {
		throw new RuntimeException();
	}

	@ExpectPlatform
	public static boolean isGenerated(LevelChunk chunk) {
		throw new RuntimeException();
	}
}
