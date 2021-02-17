package org.dimdev.dimdoors.world.pocket;

import net.minecraft.util.math.Vec3i;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PocketDirectoryTest {


	//@Test
	void newPocket() {
		PocketDirectory directory = null;// new PocketDirectory(ModDimensions.DUNGEON, 512);

		Pocket.PocketBuilder<?, ?> builder = Pocket.builder().expand(new Vec3i(1, 1, 1));

		assertEquals(0, directory.newPocket(builder).getId());
		assertEquals(1, directory.newPocket(builder).getId());
		assertEquals(2, directory.newPocket(builder).getId());
		assertEquals(3, directory.newPocket(builder).getId());
		assertEquals(4, directory.newPocket(builder).getId());
		assertEquals(5, directory.newPocket(builder).getId());
		assertEquals(6, directory.newPocket(builder).getId());



		builder = Pocket.builder().expand(new Vec3i(directory.getGridSize() + 1, directory.getGridSize() + 1, directory.getGridSize() + 1));
		assertEquals(9, directory.newPocket(builder).getId());
		assertEquals(18, directory.newPocket(builder).getId());

		builder = Pocket.builder().expand(new Vec3i(3 * directory.getGridSize() + 1, 3 * directory.getGridSize() + 1, 3 * directory.getGridSize() + 1));
		assertEquals(81, directory.newPocket(builder).getId());



		Pocket.builder().expand(new Vec3i(directory.getGridSize() + 1, directory.getGridSize() + 1, directory.getGridSize() + 1));
		assertEquals(27, directory.newPocket(builder).getId());

		builder = Pocket.builder().expand(new Vec3i(1, 1, 1));
		assertEquals(7, directory.newPocket(builder).getId());
		assertEquals(8, directory.newPocket(builder).getId());
		assertEquals(36, directory.newPocket(builder).getId());
	}
}
