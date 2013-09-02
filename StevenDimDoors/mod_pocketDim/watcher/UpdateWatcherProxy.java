package StevenDimDoors.mod_pocketDim.watcher;

import java.util.ArrayList;
import java.util.List;

public class UpdateWatcherProxy implements IUpdateWatcher
{
	private List<IUpdateWatcher> watchers;
	
	public UpdateWatcherProxy()
	{
		watchers = new ArrayList<IUpdateWatcher>();
	}

	@Override
	public void onCreated(IOpaqueMessage message)
	{
		for (IUpdateWatcher receiver : watchers)
		{
			receiver.onCreated(message);
		}
	}

	@Override
	public void onUpdated(IOpaqueMessage message)
	{
		for (IUpdateWatcher receiver : watchers)
		{
			receiver.onUpdated(message);
		}
	}

	@Override
	public void onDeleted(IOpaqueMessage message)
	{
		for (IUpdateWatcher receiver : watchers)
		{
			receiver.onDeleted(message);
		}
	}
	
	public void registerReceiver(IUpdateWatcher receiver)
	{
		watchers.add(receiver);
	}
	
	public boolean unregisterReceiver(IUpdateWatcher receiver)
	{
		return watchers.remove(receiver);
	}
}
