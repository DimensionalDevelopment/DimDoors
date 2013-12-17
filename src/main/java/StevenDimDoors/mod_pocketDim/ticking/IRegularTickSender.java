package StevenDimDoors.mod_pocketDim.ticking;


public interface IRegularTickSender {

	public void registerForTicking(IRegularTickReceiver receiver, int interval, boolean onTickStart);
	
}
