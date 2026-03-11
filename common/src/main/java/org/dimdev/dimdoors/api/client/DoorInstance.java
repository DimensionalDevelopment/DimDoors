package org.dimdev.dimdoors.api.client;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Objects;

public final class DoorInstance {
    private final Matrix4f modelMatrix;
    private PortalShape shape;

    public DoorInstance(Matrix4f modelMatrix, PortalShape shape) {
        this.modelMatrix = modelMatrix;
        this.shape = shape;
    }

    public Matrix4f getModelMatrix() {
        return modelMatrix;
    }

    public PortalShape getShape() {
        return shape;
    }

    public void setShape(@NotNull PortalShape shape) {
        this.shape = shape;
    }

    public enum PortalShape {
        NORTH_DOOR,
        EAST_DOOR,
        SOUTH_DOOR,
        WEST_DOOR,
        TOP_TRAP_DOOR,
        BOTTOM_TRAP_DOOR;

        public static @NotNull PortalShape fromDirection(Direction value) {
            return switch (value) {
                case NORTH -> NORTH_DOOR;
                case EAST -> EAST_DOOR;
                case SOUTH -> SOUTH_DOOR;
                case WEST -> WEST_DOOR;
                default -> throw new RuntimeException("Error tried to get portal shape from non horizontal direction.");
            };
        }
    }
}
