package org.dimdev.dimdoors;

import net.minecraftforge.common.ForgeChunkManager.Ticket;

public interface IChunkLoader {
    boolean isInitialized();

    void initialize(Ticket ticket);
}
