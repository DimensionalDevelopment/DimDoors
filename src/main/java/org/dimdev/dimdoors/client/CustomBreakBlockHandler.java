package org.dimdev.dimdoors.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class CustomBreakBlockHandler {
	private static final Map<BlockPos, BreakBlockInfo> customBreakBlockMap = new ConcurrentHashMap<>();
	private static final ReentrantLock lock = new ReentrantLock();

	public static Map<BlockPos, BreakBlockInfo> getCustomBreakBlockMap(int ticks) {
		lock.lock();
		Set<BlockPos> expired = customBreakBlockMap.entrySet().stream().filter(entry -> ticks - entry.getValue().getLastUpdateTick() > 400).map(entry -> entry.getKey()).collect(Collectors.toSet());
		expired.forEach(customBreakBlockMap::remove);
		Map<BlockPos, BreakBlockInfo> copy = new HashMap<>(customBreakBlockMap);
		lock.unlock();
		return copy;
	}

	public static void customBreakBlock(BlockPos pos, int stage, int ticks) {
		lock.lock();
		if (stage < 0 || stage > 10) {
			customBreakBlockMap.remove(pos);
		} else {
			if (customBreakBlockMap.containsKey(pos)) {

				BreakBlockInfo info = customBreakBlockMap.get(pos);
				info.setStage(stage);
				info.setLastUpdateTick(ticks);
			} else {
				customBreakBlockMap.put(pos, new BreakBlockInfo(stage, ticks));
			}
		}
		lock.unlock();
	}

	public static class BreakBlockInfo {
		private int stage;
		private int lastUpdateTick;

		private BreakBlockInfo(int stage, int lastUpdateTick) {
			this.stage = stage;
			this.lastUpdateTick = lastUpdateTick;
		}

		public void setStage(int stage) {
			this.stage = stage;
		}

		public int getStage() {
			return stage;
		}

		public void setLastUpdateTick(int lastUpdateTick) {
			this.lastUpdateTick = lastUpdateTick;
		}

		public int getLastUpdateTick() {
			return lastUpdateTick;
		}
	}
}
