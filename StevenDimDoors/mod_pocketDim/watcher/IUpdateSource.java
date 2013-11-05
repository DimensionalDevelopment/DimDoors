package StevenDimDoors.mod_pocketDim.watcher;

import StevenDimDoors.mod_pocketDim.util.Point4D;

public interface IUpdateSource
{
	public void registerWatchers(IUpdateWatcher<ClientDimData> dimWatcher, IUpdateWatcher<Point4D> linkWatcher);
}
