package StevenDimDoors.mod_pocketDim.watcher;

import java.util.ArrayList;
import java.util.List;

public class UpdateWatcherProxy<T> implements IUpdateWatcher<T>
{
	private List<IUpdateWatcher<T>> watchers;
	
	public UpdateWatcherProxy()
	{
		watchers = new ArrayList<IUpdateWatcher<T>>();
	}

	@Override
	public void onCreated(T message)
	{
		for (IUpdateWatcher<T> receiver : watchers)
		{
			receiver.onCreated(message);
		}
	}

	@Override
	public void onDeleted(T message)
	{
		for (IUpdateWatcher<T> receiver : watchers)
		{
			receiver.onDeleted(message);
		}
	}
	
	public void registerReceiver(IUpdateWatcher<T> receiver)
	{
		watchers.add(receiver);
	}
	
	public boolean unregisterReceiver(IUpdateWatcher<T> receiver)
	{
		return watchers.remove(receiver);
	}
}
