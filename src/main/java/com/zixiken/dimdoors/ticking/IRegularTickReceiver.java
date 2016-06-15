package com.zixiken.dimdoors.ticking;


public interface IRegularTickReceiver {

	/**
	 * This method is called periodically to execute code based on ticks elapsed.
	 */
	public void notifyTick();
}
