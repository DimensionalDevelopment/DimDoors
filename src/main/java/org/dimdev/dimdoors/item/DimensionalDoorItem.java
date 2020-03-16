package org.dimdev.dimdoors.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.TallBlockItem;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.dimdev.dimdoors.ModConfig;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.RiftProvider;
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.client.DetachedRiftBlockEntityRenderer;

import java.util.function.Consumer;

public class DimensionalDoorItem extends TallBlockItem {
    private final Consumer<? super EntranceRiftBlockEntity> setupFunction;

    public DimensionalDoorItem(Block block, Item.Settings settings, Consumer<? super EntranceRiftBlockEntity> setupFunction) {
        super(block, settings);
        this.setupFunction = setupFunction;
    }

    @Override
    public ActionResult place(ItemPlacementContext context) {
        BlockPos pos = context.getBlockPos();

        if (!context.getWorld().getBlockState(pos).canReplace(context)) {
            pos = pos.offset(context.getPlayerFacing());
        }

        boolean placedOnRift = context.getWorld().getBlockState(pos).getBlock() == ModBlocks.DETACHED_RIFT;

        if (!placedOnRift && !context.getPlayer().isSneaking() && isRiftNear(context.getWorld(), pos)) {
            // Allowing on second right click would require cancelling client-side, which
            // is impossible (see https://github.com/MinecraftForge/MinecraftForge/issues/3272)
            // without sending custom packets.

            if (context.getWorld().isClient) {
                Object[] translationArgs = new Object[0];
                context.getPlayer().sendMessage(new TranslatableText("rifts.entrances.rift_too_close", translationArgs));
                DetachedRiftBlockEntityRenderer.showRiftCoreUntil = System.currentTimeMillis() + ModConfig.GRAPHICS.highlightRiftCoreFor;
            }

            return ActionResult.FAIL;
        }

        if (context.getWorld().isClient) {
            return super.place(context);
        }

        // Store the rift entity if there's a rift block there that may be broken
        DetachedRiftBlockEntity rift = null;
        if (placedOnRift) {
            rift = (DetachedRiftBlockEntity) context.getWorld().getBlockEntity(pos);
            rift.setUnregisterDisabled(true);
        }

        ActionResult result = super.place(context);
        if (result == ActionResult.SUCCESS) {
            BlockState state = context.getWorld().getBlockState(pos);
            if (rift == null) {
                // Get the rift entity (not hard coded, works with any door size)
                @SuppressWarnings("unchecked") // Guaranteed to be IRiftProvider<TileEntityEntranceRift> because of constructor
                        EntranceRiftBlockEntity entranceRift = ((RiftProvider<EntranceRiftBlockEntity>) state.getBlock()).getRift(context.getWorld(), pos, state);

                // Configure the rift to its default functionality
                setupRift(entranceRift);

                // Register the rift in the registry
                entranceRift.markDirty();
                entranceRift.register();
            } else {
                // Copy from the old rift
                EntranceRiftBlockEntity newRift = (EntranceRiftBlockEntity) context.getWorld().getBlockEntity(pos);
                newRift.copyFrom(rift);
                newRift.updateType();
            }
        } else if (rift != null) {
            rift.setUnregisterDisabled(false);
        }

        return result;
    }

    public static boolean isRiftNear(World world, BlockPos pos) {
        for (int x = pos.getX() - 5; x < pos.getX() + 5; x++) {
            for (int y = pos.getY() - 5; y < pos.getY() + 5; y++) {
                for (int z = pos.getZ() - 5; z < pos.getZ() + 5; z++) {
                    BlockPos searchPos = new BlockPos(x, y, z);
                    if (world.getBlockState(searchPos).getBlock() == ModBlocks.DETACHED_RIFT) {
                        DetachedRiftBlockEntity rift = (DetachedRiftBlockEntity) world.getBlockEntity(searchPos);
                        if (Math.sqrt(pos.getSquaredDistance(searchPos)) < rift.size) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public void setupRift(EntranceRiftBlockEntity entranceRift) {
        setupFunction.accept(entranceRift);
    }
}
