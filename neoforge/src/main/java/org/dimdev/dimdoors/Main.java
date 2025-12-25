package org.dimdev.dimdoors;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.dimdev.dimdoors.util.schematic.Schematic;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        var workers = ChunkWorker.getWorkers(new BlockPos(0, 0, 0), new BlockPos(0, 0, 0), 5, 5 + 16, 5 + 16);

        System.out.println();
    }

    private record ChunkWorker(int offsetX, int offsetY, int offsetZ, int indexX, int indexY, int indexZ,
                               int width, int height, int length) {
        private static List<ChunkWorker> getWorkers(
                BlockPos origin,
                Vec3i offset,
                int width,
                int height,
                int length) {

            List<ChunkWorker> workers = new ArrayList<>();

            int startX = offset.getX();
            int startY = offset.getY();
            int startZ = offset.getZ();

            int endX = startX + width;
            int endY = startY + height;
            int endZ = startZ + length;

            int minSectionY = (origin.getY() + startY) >> 4;
            int maxSectionY = (origin.getY() + endY   - 1) >> 4;
            int minSectionX = (origin.getX() + startX) >> 4;
            int maxSectionX = (origin.getX() + endX   - 1) >> 4;
            int minSectionZ = (origin.getZ() + startZ) >> 4;
            int maxSectionZ = (origin.getZ() + endZ   - 1) >> 4;


            for (int sectionX = minSectionX; sectionX <= maxSectionX; sectionX++) {
                for (int sectionZ = minSectionZ; sectionZ <= maxSectionZ; sectionZ++) {
                    for (int sectionY = minSectionY; sectionY <= maxSectionY; sectionY++) {

                        int localX = ((sectionX << 4) - origin.getX());
                        int localY = ((sectionY << 4) - origin.getY());
                        int localZ = ((sectionZ << 4) - origin.getZ());

                        int localStartX = Math.max(startX, localX);
                        int localStartY = Math.max(startY, localY);
                        int localStartZ = Math.max(startZ, localZ);

                        int localEndX = Math.min(endX, localX + 16);
                        int localEndY = Math.min(endY, localY + 16);
                        int localEndZ = Math.min(endZ, localZ + 16);

                        int jobWidth = localEndX - localStartX;
                        int jobHeight = localEndY - localStartY;
                        int jobLength = localEndZ - localStartZ;

                        int secOffsetX = (localStartX + origin.getX()) & 15;
                        int secOffsetY = (localStartY + origin.getY()) & 15;
                        int secOffsetZ = (localStartZ + origin.getZ()) & 15;

                        workers.add(new ChunkWorker(
                                secOffsetX,
                                secOffsetY,
                                secOffsetZ,
                                localStartX,
                                localStartY,
                                localStartZ,
                                jobWidth,
                                jobHeight,
                                jobLength
                        ));
                    }
                }
            }

            return workers;
        }

        public void execute() {
            int idx = (indexY * length + indexZ) * width + indexX;

            for (int y = 0; y < height; y++) {
                for (int z = 0; z < length; z++) {
                    for (int x = 0; x < width; x++, idx++) {

                    }
                }
            }
        }
    }
}
