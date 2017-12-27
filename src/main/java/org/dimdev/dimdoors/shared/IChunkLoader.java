package org.dimdev.dimdoors.shared;

import net.minecraftforge.common.ForgeChunkManager.Ticket;

public interface IChunkLoader {

    boolean isInitialized();

    void initialize(Ticket ticket);
}
