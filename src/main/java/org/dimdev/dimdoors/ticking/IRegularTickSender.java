package org.dimdev.dimdoors.ticking;


public interface IRegularTickSender {

    void registerReceiver(IRegularTickReceiver receiver, int interval, boolean onTickStart);

    void unregisterReceivers();

}
