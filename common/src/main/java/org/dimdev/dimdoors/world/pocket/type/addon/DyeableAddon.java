package org.dimdev.dimdoors.world.pocket.type.addon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.EntityUtils;
import org.dimdev.dimdoors.block.AncientFabricBlock;
import org.dimdev.dimdoors.block.FabricBlock;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.dimdev.dimdoors.world.pocket.type.PocketColor;
import org.dimdev.dimdoors.world.pocket.type.PrivatePocket;

public class DyeableAddon implements PocketAddon {
    public static ResourceLocation ID = DimensionalDoors.id("dyeable");
    private static final int BLOCKS_PAINTED_PER_DYE = 1000000;
    public static final MapCodec<DyeableAddon> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            PocketColor.CODEC.fieldOf("dyeColor").forGetter(a -> a.dyeColor),
            PocketColor.CODEC.fieldOf("nextDyeColor").forGetter(a -> a.nextDyeColor),
            Codec.INT.fieldOf("count").forGetter(a -> a.count)
            ).apply(instance, DyeableAddon::new)

    );

    public DyeableAddon() {
        this(PocketColor.WHITE);
    }

    public DyeableAddon(PocketColor dyeColor) {
        this(dyeColor, PocketColor.NONE);
    }

    public DyeableAddon(PocketColor dyeColor, PocketColor nextDyeColor) {
        this(dyeColor, nextDyeColor, 0);
    }

    public DyeableAddon(PocketColor dyeColor, PocketColor nextDyeColor, int count) {
        this.dyeColor = dyeColor;
        this.nextDyeColor = nextDyeColor;
    }

    protected PocketColor dyeColor = PocketColor.WHITE;
    private PocketColor nextDyeColor = PocketColor.NONE;
    private int count = 0;

    private static int amountOfDyeRequiredToColor(Pocket pocket) {
    int outerVolume = pocket.getBox().getYSpan() * pocket.getBox().getZSpan() * pocket.getBox().getXSpan();
    int innerVolume = (pocket.getBox().getYSpan() - 5) * (pocket.getBox().getZSpan() - 5) * (pocket.getBox().getXSpan() - 5);

    return Math.max((outerVolume - innerVolume) / BLOCKS_PAINTED_PER_DYE, 1);
    }

    private void repaint(Pocket pocket, DyeColor dyeColor) {
    Level serverWorld = DimensionalDoors.getWorld(pocket.getWorld());
    BlockState innerWall = ModBlocks.fabricFromDye(dyeColor).defaultBlockState();;
    BlockState outerWall = ModBlocks.ancientFabricFromDye(dyeColor).defaultBlockState();;

    BlockPos.betweenClosedStream(pocket.getBox()).forEach(pos -> {
            System.out.println(pos + ": " + serverWorld.getBlockState(pos).toString());
        if (serverWorld.getBlockState(pos).getBlock() instanceof AncientFabricBlock) {
        serverWorld.setBlockAndUpdate(pos, outerWall);
        } else if (serverWorld.getBlockState(pos).getBlock() instanceof FabricBlock) {
        serverWorld.setBlockAndUpdate(pos, innerWall);
        }
    });
    }

    public boolean addDye(Pocket pocket, Entity entity, DyeColor dyeColor) {
    PocketColor color = PocketColor.from(dyeColor);

    int maxDye = amountOfDyeRequiredToColor(pocket);

    if (this.dyeColor == color) {
        EntityUtils.chat(entity, Component.translatable("dimdoors.pockets.dyeAlreadyAbsorbed"));
        return false;
    }

    if (this.nextDyeColor != PocketColor.NONE && this.nextDyeColor == color) {
        if (this.count + 1 > maxDye) {
        repaint(pocket, dyeColor);
        this.dyeColor = color;
        this.nextDyeColor = PocketColor.NONE;
        this.count = 0;
        EntityUtils.chat(entity, Component.translatable("dimdoors.pocket.pocketHasBeenDyed", dyeColor));
        } else {
        this.count++;
        EntityUtils.chat(entity, Component.translatable("dimdoors.pocket.remainingNeededDyes", this.count, maxDye, color));
        }
    } else {
        this.nextDyeColor = color;
        this.count = 1;
        EntityUtils.chat(entity, Component.translatable("dimdoors.pocket.remainingNeededDyes", this.count, maxDye, color));
    }
    return true;
    }

    @Override
    public boolean applicable(Pocket pocket) {
    return pocket instanceof PrivatePocket;
    }

    @Override
    public PocketAddonType<?, ?> getType() {
    return PocketAddonType.DYEABLE_ADDON;
    }

    public interface DyeablePocketBuilder<T extends Pocket.PocketBuilder<T, ?>> extends PocketBuilderExtension<T> {
    default T dyeColor(PocketColor dyeColor) {

        this.<DyeableBuilderAddon>getAddon(PocketAddonType.DYEABLE_ADDON).dyeColor = dyeColor;

        return getSelf();
    }
    }

    public static class DyeableBuilderAddon implements PocketBuilderAddon<DyeableAddon, DyeableBuilderAddon> {

        public static MapCodec<DyeableBuilderAddon> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(PocketColor.CODEC.lenientOptionalFieldOf("dye_color", PocketColor.NONE).forGetter(a -> a.dyeColor)).apply(instance, DyeableBuilderAddon::new));

        private PocketColor dyeColor = PocketColor.NONE;

        public DyeableBuilderAddon() {
            this(PocketColor.NONE);
        }

        public DyeableBuilderAddon(PocketColor dyeColor) {
            this.dyeColor = dyeColor;
        }

    // TODO: add some Pocket#init so that we can have boolean shouldRepaintOnInit

    @Override
    public void apply(Pocket pocket) {
        DyeableAddon addon = new DyeableAddon(dyeColor);
        addon.dyeColor = dyeColor;
        pocket.addAddon(addon);
    }

        @Override
    public PocketAddonType<DyeableAddon, DyeableBuilderAddon> getType() {
        return PocketAddonType.DYEABLE_ADDON;
    }
    }

    public interface DyeablePocket extends AddonProvider {
//    default boolean addDye(Entity entity, DyeColor dyeColor) {
//          TODO: REnable personal pocket dyeing.
//        ensureIsPocket();
//        if (!this.hasAddon(ID)) {
//        DyeableAddon addon = new DyeableAddon();
//        this.addAddon(addon);
//        return addon.addDye((Pocket) this, entity, dyeColor);
//        }
//        return this.<DyeableAddon>getAddon(ID).addDye((Pocket) this, entity, dyeColor);
//    }
    }
}