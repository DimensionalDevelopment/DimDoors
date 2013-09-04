package StevenDimDoors.mod_pocketDim.watcher;

public interface IUpdateWatcher<T>
{
	public void onCreated(T message);
	public void onDeleted(T message);
}
