package StevenDimDoors.mod_pocketDim.messages;

public interface IUpdateWatcher
{
	public void onCreated(IDataMessage message);
	public void onUpdated(IDataMessage message);
	public void onDeleted(IDataMessage message);
}
