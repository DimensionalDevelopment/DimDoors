package org.dimdev.dimdoors.pockets.modifier;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.dimdev.dimdoors.api.util.math.Equation;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.util.schematic.SchematicBlockPalette;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public record ShellModifier(List<Layer> layers, Optional<BoundingBox> boxToDrawAround) implements Modifier {
    public static final MapCodec<ShellModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Layer.CODEC.listOf().optionalFieldOf("layers", List.of()).forGetter(ShellModifier::layers),
            BoundingBox.CODEC.optionalFieldOf("box_to_draw_around").forGetter(ShellModifier::boxToDrawAround)
    ).apply(instance, ShellModifier::new));

    //TODO: use boxToDrawAround as an alternate cube to generate around in a pocket.

    public static final String KEY = "shell";

    @Override
    public ModifierType<ShellModifier> getType() {
        return ModifierType.SHELL_MODIFIER_TYPE;
    }

    @Override
    public void apply(PocketGenerationContext parameters, Pocket.PocketBuilder<?, ?> builder) {
        Map<String, Double> variableMap = parameters.toVariableMap(new HashMap<>());
        for (Layer layer : layers) {
            int thickness = layer.getThickness(variableMap);
            builder.expandExpected(new Vec3i(2 * thickness, 2 * thickness, 2 * thickness));
            builder.offsetOrigin(new Vec3i(thickness, thickness, thickness));
        }
    }

    @Override
    public void apply(PocketGenerationContext parameters, RiftManager manager) {
        Pocket<?, ?> pocket = manager.getPocket();
        BoundingBox templateBox = boxToDrawAround.map(a -> {
            var origin = pocket.getOrigin();
            return a.moved(origin.getX(), origin.getY(), origin.getZ());
        }).orElseGet(pocket::getBox);
        var variableMap = pocket.toVariableMap(new HashMap<>());

        int cumulativeThickness = 0;

        for (Layer layer : layers) {
            int thickness = layer.getThickness(variableMap);

            // Draw this layer as a SOLID shell
            drawLayer(layer, templateBox, cumulativeThickness, thickness, parameters.world());

            cumulativeThickness += thickness;
        }
    }

    private void drawLayer(Layer layer, BoundingBox templateBox, int offset, int thickness, ServerLevel world) {
        final BlockState state = layer.blockState();
        final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        final int innerMinX = templateBox.minX() - offset;
        final int innerMinY = templateBox.minY() - offset;
        final int innerMinZ = templateBox.minZ() - offset;
        final int innerMaxX = templateBox.maxX() + offset;
        final int innerMaxY = templateBox.maxY() + offset;
        final int innerMaxZ = templateBox.maxZ() + offset;

        final int outerMinX = innerMinX - thickness;
        final int outerMinY = innerMinY - thickness;
        final int outerMinZ = innerMinZ - thickness;
        final int outerMaxX = innerMaxX + thickness;
        final int outerMaxY = innerMaxY + thickness;
        final int outerMaxZ = innerMaxZ + thickness;

        // -X
        drawSide(world, pos, state, outerMinX, outerMinY, outerMinZ, innerMinX - 1, outerMaxY, outerMaxZ);

        // +X
        drawSide(world, pos, state, innerMaxX + 1, outerMinY, outerMinZ, outerMaxX, outerMaxY, outerMaxZ);

        // -Y
        drawSide(world, pos, state, innerMinX, outerMinY, outerMinZ, innerMaxX, innerMinY - 1, outerMaxZ);

        // +Y
        drawSide(world, pos, state, innerMinX, innerMaxY + 1, outerMinZ, innerMaxX, outerMaxY, outerMaxZ);

        // -Z
        drawSide(world, pos, state, innerMinX, innerMinY, outerMinZ, innerMaxX, innerMaxY, innerMinZ - 1);

        // +Z
        drawSide(world, pos, state, innerMinX, innerMinY, innerMaxZ + 1, innerMaxX, innerMaxY, outerMaxZ);
    }


    private static void drawSide(
            ServerLevel world,
            BlockPos.MutableBlockPos pos,
            BlockState state,
            int minX, int minY, int minZ,
            int maxX, int maxY, int maxZ
    ) {
        for (int x = minX; x <= maxX; x++)
            for (int y = minY; y <= maxY; y++)
                for (int z = minZ; z <= maxZ; z++)
                    world.setBlockAndUpdate(pos.set(x, y, z), state);
    }


    @Override
    public @NotNull String toString() {
        return MoreObjects.toStringHelper(this)
                .add("layers", layers)
                .toString();
    }


    public record Layer(BlockState blockState, Equation thickness) {
        public static final Codec<Layer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                SchematicBlockPalette.Entry.CODEC.fieldOf("block_state").forGetter(Layer::blockState),
                Equation.CODEC.optionalFieldOf("thickness", Equation.ONE).forGetter(a -> a.thickness)
        ).apply(instance, Layer::new));


        public int getThickness(Map<String, Double> variableMap) {
            return (int) thickness.apply(variableMap);
        }
    }
}