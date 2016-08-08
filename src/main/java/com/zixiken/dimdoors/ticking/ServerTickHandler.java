package com.zixiken.dimdoors.ticking;

import java.util.ArrayList;
import java.util.EnumSet;

import com.zixiken.dimdoors.core.DDTeleporter;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

public class ServerTickHandler implements IRegularTickSender {
	private static final String PROFILING_LABEL = "Dimensional Doors: Server Tick";
	
	private int tickCount = 0;
	private ArrayList<RegularTickReceiverInfo> receivers;

	public ServerTickHandler() {
		this.receivers = new ArrayList<RegularTickReceiverInfo>();
	}

	@Override
	public void registerReceiver(IRegularTickReceiver receiver, int interval, boolean onTickStart) {
		RegularTickReceiverInfo info = new RegularTickReceiverInfo(receiver, interval, onTickStart);
		receivers.add(info);
	}
	
	@Override
	public void unregisterReceivers() {
		receivers.clear();
	}

	@SubscribeEvent
	public void tickEvent(TickEvent event) {
        if (event.side != Side.SERVER)
            return;

        if (event.phase == TickEvent.Phase.START)
            tickStart(event.type);
        else if (event.phase == TickEvent.Phase.END)
            tickEnd(event.type);
    }

    private void tickStart(TickEvent.Type type) {
		if (type.equals(EnumSet.of(TickEvent.Type.SERVER))) {
			for (RegularTickReceiverInfo info : receivers) {
				if (info.OnTickStart && tickCount % info.Interval == 0) {
					info.RegularTickReceiver.notifyTick();
				}
			}
		}
		
		//TODO: Stuck this in here because it's already rather hackish.
		//We should standardize this as an IRegularTickReceiver in the future. ~SenseiKiwi
		if (DDTeleporter.cooldown > 0) {
			DDTeleporter.cooldown--;
		}
	}

	private void tickEnd(TickEvent.Type type) {
		for (RegularTickReceiverInfo info : receivers) {
			if (!info.OnTickStart && tickCount % info.Interval == 0) {
				info.RegularTickReceiver.notifyTick();
			}
		}
		tickCount++; //There is no need to reset the counter. Let it overflow.
	}
}
