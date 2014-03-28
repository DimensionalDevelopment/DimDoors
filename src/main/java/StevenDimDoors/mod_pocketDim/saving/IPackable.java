package StevenDimDoors.mod_pocketDim.saving;

public interface IPackable<T>
{
	public String name();
	public T pack();
	public boolean isModified();
	public void clearModified();
}
