package org.dimdev.dimdoors.compat.sable.mixins;

import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.system.SubLevelTrackingSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Keeps Sable from telling a client to drop a sub-level while DimDoors moves a player out of it.
 *
 * <p>The silenced call is the {@code sendRemoval} in {@code tick()} that fires when a tracking
 * player is no longer in this level but is still online, which is exactly the state a player passes
 * through when a rift moves them between dimensions.</p>
 */
@Mixin(SubLevelTrackingSystem.class)
public class SubLevelTrackingSystemDimensionChangeMixin {
    // TODO: Make this more robust by only silencing the removal packet for sublevels DimDoors forced to load for teleportation.
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Ldev/ryanhcode/sable/sublevel/system/SubLevelTrackingSystem;sendRemoval(Lfoundry/veil/api/network/VeilPacketManager$PacketSink;Ldev/ryanhcode/sable/sublevel/ServerSubLevel;)V", ordinal = 0), remap = false)
    private void dimdoors$skipSourceLevelRemovalPacket(SubLevelTrackingSystem instance, @Coerce Object sink, ServerSubLevel subLevel) {
    }
}
