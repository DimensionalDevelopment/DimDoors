package org.dimdev.dimdoors.ticking;


public interface IRegularTickReceiver {

    /**
     * This method is called periodically to execute code based on ticks elapsed.
     */
    void notifyTick();
}
