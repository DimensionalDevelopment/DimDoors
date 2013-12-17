package StevenDimDoors.mod_pocketDim.ticking;

public class RegularTickReceiverInfo {
	
	public IRegularTickReceiver RegularTickReceiver;
	public int Interval;
	public boolean OnTickStart;
	
	public RegularTickReceiverInfo(IRegularTickReceiver regularTickReceiver, int interval, boolean onTickStart)
	{
		this.RegularTickReceiver = regularTickReceiver;
		this.Interval = interval;
		this.OnTickStart = onTickStart;
	}
	
}
