package StevenDimDoors.mod_pocketDim.ticking;

import java.util.ArrayList;
import java.util.EnumSet;

import StevenDimDoors.mod_pocketDim.DDTeleporter;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class CommonTickHandler implements ITickHandler, IRegularTickSender
{
	private static final String PROFILING_LABEL = "Dimensional Doors: Common Tick";
	
	private int tickCount = 0;
	private ArrayList<RegularTickReceiverInfo> receivers;


	public CommonTickHandler()
	{
		this.receivers = new ArrayList<RegularTickReceiverInfo>();
	}

	@Override
	public void registerForTicking(IRegularTickReceiver receiver, int interval, boolean onTickStart)
	{
		RegularTickReceiverInfo info = new RegularTickReceiverInfo(receiver, interval, onTickStart);
		receivers.add(info);
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) 
	{
		if (type.equals(EnumSet.of(TickType.SERVER)))
		{
			for (RegularTickReceiverInfo info : receivers)
			{
				if (info.OnTickStart && tickCount % info.Interval == 0)
				{
					info.RegularTickReceiver.notifyTick();
				}
			}
		}
		
		//TODO: Stuck this in here because it's already rather hackish.
		//We should standardize this as an IRegularTickReceiver in the future. ~SenseiKiwi
		if (DDTeleporter.cooldown > 0)
		{
			DDTeleporter.cooldown--;
		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData)
	{
		for (RegularTickReceiverInfo info : receivers)
		{
			if (!info.OnTickStart && tickCount % info.Interval == 0)
			{
				info.RegularTickReceiver.notifyTick();
			}
		}
		tickCount++; //There is no need to reset the counter. Let it overflow.
	}

	@Override
	public EnumSet<TickType> ticks()
	{
		return EnumSet.of(TickType.SERVER);
	}

	@Override
	public String getLabel()
	{
		return PROFILING_LABEL; //Used for profiling!
	}
}
