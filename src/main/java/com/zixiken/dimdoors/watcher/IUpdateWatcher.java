package com.zixiken.dimdoors.watcher;

public interface IUpdateWatcher<T>
{
	public void onCreated(T message);
	public void update(T message);
	public void onDeleted(T message);
}
