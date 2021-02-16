package org.dimdev.dimdoors.util.math;

import org.junit.jupiter.api.Test;

import java.util.Vector;

import static org.junit.jupiter.api.Assertions.*;

class GridUtilTest {

	@Test
	void idToGridPos() {
		GridUtil.GridPos pos;
		// I scribbled the grid down so I could figure out these tests, lmao
		pos = GridUtil.idToGridPos(86);
		assertEquals(new GridUtil.GridPos(8, 1), pos);
		pos = GridUtil.idToGridPos(90);
		assertEquals(new GridUtil.GridPos(12, 0), pos);
		pos = GridUtil.idToGridPos(100);
		assertEquals(new GridUtil.GridPos(7, 0), pos);
	}

	@Test
	void conversionConsistencyTest() {
		for (int i = 0; i < 1000; i++) {
			assertEquals(i, GridUtil.gridPosToID(GridUtil.idToGridPos(i)));
		}
	}
}
