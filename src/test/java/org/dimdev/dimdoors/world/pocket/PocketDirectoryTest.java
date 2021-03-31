package org.dimdev.dimdoors.world.pocket;

import net.minecraft.util.math.Vec3i;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.dimdev.test.ServerTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

//@RunWith(ServerTestRunner.class)
public class PocketDirectoryTest {

	//@Test
	public void newPocket() {
		PocketDirectory directory = new PocketDirectory(ModDimensions.DUNGEON, 13);//

		Pocket.PocketBuilder<?, ?> builder = Pocket.builder().expand(new Vec3i(16 * directory.getGridSize(), 1, 1));

		assertEquals(0, directory.newPocket(builder).getId()); // from 0 to 0
		assertEquals(1, directory.newPocket(builder).getId()); // from 1 to 1
		assertEquals(2, directory.newPocket(builder).getId()); // from 2 to 2
		assertEquals(3, directory.newPocket(builder).getId()); // from 3 to 3
		assertEquals(4, directory.newPocket(builder).getId()); // from 4 to 4
		assertEquals(5, directory.newPocket(builder).getId()); // from 5 to 5
		assertEquals(6, directory.newPocket(builder).getId()); // from 6 to 6



		builder = Pocket.builder().expand(new Vec3i(16 * directory.getGridSize() + 1, 16 * directory.getGridSize() + 1, 16 * directory.getGridSize() + 1));
		assertEquals(17, directory.newPocket(builder).getId()); // from 9 to 17
		assertEquals(26, directory.newPocket(builder).getId()); // from 18 to 26

		builder = Pocket.builder().expand(new Vec3i(3 * 16 * directory.getGridSize() + 1, 3 * 16 * directory.getGridSize() + 1, 3 * 16 * directory.getGridSize() + 1));
		assertEquals(161, directory.newPocket(builder).getId()); // from 81 to 161



		builder = Pocket.builder().expand(new Vec3i(16 * directory.getGridSize() + 1, 16 * directory.getGridSize() + 1, 16 * directory.getGridSize() + 1));
		assertEquals(35, directory.newPocket(builder).getId()); // from 27 to 35

		builder = Pocket.builder().expand(new Vec3i(1, 1, 1));
		assertEquals(7, directory.newPocket(builder).getId()); // from 7 to 7
		assertEquals(8, directory.newPocket(builder).getId()); // from 8 to 8
		assertEquals(36, directory.newPocket(builder).getId()); // from 36 to 36
	}
}
