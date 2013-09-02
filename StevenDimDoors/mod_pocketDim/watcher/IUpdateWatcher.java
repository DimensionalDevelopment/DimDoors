package StevenDimDoors.mod_pocketDim.watcher;

public interface IUpdateWatcher
{
	public void onCreated(IOpaqueMessage message);
	public void onUpdated(IOpaqueMessage message);
	public void onDeleted(IOpaqueMessage message);
}
