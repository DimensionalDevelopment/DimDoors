package org.dimdev.dimdoors.rift.targets;

import org.dimdev.dimdoors.rift.registry.DialingRegistry;

public class DialingExitTarget extends PlayerTrackingExitTarget<DialingExitTarget, DialingRegistry> {
    public static final DialingExitTarget INSTANCE = new DialingExitTarget();

    private DialingExitTarget() {}

    @Override
    public VirtualTargetType<DialingExitTarget> getType() {
        return VirtualTargetType.DIALING_EXIT;
    }

    @Override
    public DialingExitTarget copy() {
        return this;
    }

    @Override
    public DialingRegistry getSubsystem() {
        return DialingRegistry.getInstance();
    }
}
