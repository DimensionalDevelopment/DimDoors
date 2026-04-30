package org.dimdev.dimdoors.pockets.modifier;

import com.google.common.base.MoreObjects;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.api.util.BlockBoxUtil;
import org.dimdev.dimdoors.api.util.math.Equation;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.util.schematic.SchematicBlockPalette;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShellModifier extends AbstractModifier {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String KEY = "shell";

    private final List<Layer> layers = new ArrayList<>();
    private BoundingBox boxToDrawAround;

    @Override
    public CompoundTag toNbtInternal(CompoundTag nbt, HolderLookup.Provider provider, boolean allowReference) {
    super.toNbtInternal(nbt, provider, allowReference);

    ListTag layersNbt = new ListTag();
    for (Layer layer : layers) {
        layersNbt.add(layer.toNbt());
    }
    nbt.put("layers", layersNbt);
    if (boxToDrawAround != null) {
        nbt.put("box_to_draw_around", BlockBoxUtil.toNbt(boxToDrawAround));
    }

    return nbt;
    }

    @Override
    public Modifier fromNbt(CompoundTag nbt, HolderLookup.Provider provider, ResourceManager manager) {
    for (Tag layerNbt : nbt.getList("layers", Tag.TAG_COMPOUND)) {
        CompoundTag nbtCompound = (CompoundTag) layerNbt;
        try {
        Layer layer = Layer.fromNbt(nbtCompound);
        layers.add(layer);
        } catch (CommandSyntaxException e) {
                LOGGER.error("could not parse Layer: {}", nbtCompound, e);
        }
    }

    if (nbt.contains("box_to_draw_around", Tag.TAG_INT_ARRAY)) {
        int[] box = nbt.getIntArray("box_to_draw_around");
        boxToDrawAround = BoundingBox.fromCorners(new Vec3i(box[0], box[1], box[2]), new Vec3i(box[3], box[4], box[5]));
    }

    return this;
    }

    @Override
    public Modifier.ModifierType<? extends Modifier> getType() {
    return Modifier.ModifierType.SHELL_MODIFIER_TYPE;
    }

    @Override
    public String getKey() {
    return KEY;
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
        Pocket pocket = manager.getPocket();
        BoundingBox templateBox = pocket.getBox();
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
        final BlockState state = layer.getBlockState();
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
    public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("layers", layers)
        .toString();
    }

    public static class Layer {
    private final String blockStateString;
    private final String thickness;
    private Equation thicknessEquation;
    private final BlockState blockState;

    public Layer(String blockStateString, String thickness) {
        this.blockStateString = blockStateString;
        this.thickness = thickness;
        try {
        this.thicknessEquation = Equation.parse(thickness);
        } catch (Equation.EquationParseException e) {
        LOGGER.error("Could not parse layer thickness equation. Defaulting to 1");
        // FIXME: do we actually want to have it serialize to the broken String equation we input?
        this.thicknessEquation = Equation.newEquation(variableMap -> 1d, stringBuilder -> stringBuilder.append(thickness));
        }

        this.blockState = SchematicBlockPalette.Entry.to(blockStateString).getOrThrow();
    }

    public BlockState getBlockState() {
        return blockState;
    }

    public int getThickness(Map<String, Double> variableMap) {
        return (int) thicknessEquation.apply(variableMap);
    }

    public CompoundTag toNbt() {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("block_state", blockStateString);
        nbt.putString("thickness", thickness);
        return nbt;
    }

    public static Layer fromNbt(CompoundTag nbt) throws CommandSyntaxException {
        return new Layer(nbt.getString("block_state"), nbt.getString("thickness"));
    }
    }
}