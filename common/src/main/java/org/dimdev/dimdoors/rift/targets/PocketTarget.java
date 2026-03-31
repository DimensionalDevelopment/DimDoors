package org.dimdev.dimdoors.rift.targets;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.rift.target.Target;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.pockets.virtual.ImplementedVirtualPocket;
import org.dimdev.dimdoors.ticket.ModTicketTypes;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PocketTarget extends VirtualTarget implements TickingTarget {
    private UUID pocketId;

    @Override
    public <T extends VirtualTarget> VirtualTargetType<T> getType() {
        return null;
    }

    @Override
    public Target receiveOther() {
        return super.receiveOther();
    }

    @Override
    public <T extends RiftBlockEntity> void tick(Level level, BlockPos pos, BlockState state, RiftBlockEntity rift) {
        if(!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        MinecraftServer server = serverLevel.getServer();

        var record = DimensionalRegistry.getPocketDirectory().get(pocketId);

        if (record == null) return;

        var world = record.worldInfo().getLevel();

        world.getChunkSource().addRegionTicket(ModTicketTypes.POCKET_GENERATOR_TICKET);



    }

    @Override
    public VirtualTarget copy() {
        return null;
    }
}
