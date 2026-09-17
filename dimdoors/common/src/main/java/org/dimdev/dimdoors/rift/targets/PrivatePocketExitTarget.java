package org.dimdev.dimdoors.rift.targets;

import org.dimdev.dimdoors.api.util.RGBA;
import org.dimdev.dimdoors.world.pocket.PrivateRegistry;

public class PrivatePocketExitTarget extends PlayerTrackingExitTarget<PrivatePocketExitTarget, PrivateRegistry> {
    public static final PrivatePocketExitTarget INSTANCE = new PrivatePocketExitTarget();
    public static final RGBA COLOR = new RGBA(0, 1, 0, 1);

    private PrivatePocketExitTarget() {}

    @Override
    public VirtualTargetType<PrivatePocketExitTarget> getType() {
        return VirtualTargetType.PRIVATE_POCKET_EXIT;
    }

    @Override
    public PrivatePocketExitTarget copy() {
        return this;
    }

    @Override
    public PrivateRegistry getSubsystem() {
        return PrivateRegistry.getInstance();
    }
}
