package StevenDimDoors.mod_pocketDim.ticking;


public interface IRegularTickSender {

	public void registerReceiver(IRegularTickReceiver receiver, int interval, boolean onTickStart);
	public void unregisterReceivers();
	
}
