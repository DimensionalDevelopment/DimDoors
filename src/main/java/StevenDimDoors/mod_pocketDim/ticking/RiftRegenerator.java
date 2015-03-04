package StevenDimDoors.mod_pocketDim.ticking;

import java.util.PriorityQueue;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import StevenDimDoors.mod_pocketDim.blocks.BlockRift;
import StevenDimDoors.mod_pocketDim.core.DimLink;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.util.Point4D;

public class RiftRegenerator implements IRegularTickReceiver {
	
	// Ranges of regeneration delays, in seconds
	private static final int MIN_FAST_DELAY = 1;
	private static final int MAX_FAST_DELAY = 3;
	private static final int MIN_SLOW_DELAY = 5;
	private static final int MAX_SLOW_DELAY = 15;
	private static final int MIN_RESCHEDULE_DELAY = 4 * 60;
	private static final int MAX_RESCHEDULE_DELAY = 6 * 60;
	
	private static final int TICKS_PER_SECOND = 20;
	private static final int RIFT_REGENERATION_INTERVAL = 1; // Check the regeneration queue every tick
	private static Random random = new Random();
	
	private long tickCount = 0;
	private PriorityQueue<RiftTicket> ticketQueue;
	private BlockRift blockRift;
	
	public RiftRegenerator(IRegularTickSender sender, BlockRift blockRift)
	{
		this.ticketQueue = new PriorityQueue<RiftTicket>();
		this.blockRift = blockRift;
		sender.registerReceiver(this, RIFT_REGENERATION_INTERVAL, false);
	}
	
	@Override
	public void notifyTick()
	{
		processTicketQueue();
		tickCount++;
	}
	
	public void scheduleSlowRegeneration(DimLink link)
	{
		scheduleRegeneration(link, MIN_SLOW_DELAY, MAX_SLOW_DELAY);
	}
	
	public void scheduleSlowRegeneration(int x, int y, int z, World world)
	{
		scheduleRegeneration(PocketManager.getLink(x, y, z, world), MIN_SLOW_DELAY, MAX_SLOW_DELAY);
	}
	
	public void scheduleFastRegeneration(int x, int y, int z, World world)
	{
		scheduleRegeneration(PocketManager.getLink(x, y, z, world), MIN_FAST_DELAY, MAX_FAST_DELAY);
	}
	
	private void scheduleRegeneration(DimLink link, int minDelay, int maxDelay)
	{
		if (link != null)
		{
			int tickDelay = MathHelper.getRandomIntegerInRange(random, minDelay * TICKS_PER_SECOND, maxDelay * TICKS_PER_SECOND);
			ticketQueue.add(new RiftTicket(link.source(), tickCount + tickDelay));
		}
	}
	
	private void processTicketQueue()
	{
		RiftTicket ticket;
		while (!ticketQueue.isEmpty() && ticketQueue.peek().timestamp() <= tickCount)
		{
			ticket = ticketQueue.remove();
			regenerateRift(ticket.location());
		}
	}

	private void regenerateRift(Point4D location)
	{
		int x = location.getX();
		int y = location.getY();
		int z = location.getZ();
		
		// Try to regenerate a rift, or possibly reschedule its regeneration.
		// The world for the given location must be loaded.
		World world = DimensionManager.getWorld(location.getDimension());
		if (world == null)
			return;
		
		// There must be a link at the given location.
		DimLink link = PocketManager.getLink(location);
		if (link == null)
			return;
		
		// The chunk at the given location must be loaded.
		// Note: ChunkProviderServer.chunkExists() returns whether a chunk is
		// loaded, not whether it has already been created.
		if (!world.getChunkProvider().chunkExists(x >> 4, z >> 4))
			return;
		
		// If the location is occupied by an immune DD block, then don't regenerate.
		if (blockRift.isModBlockImmune(world, x, y, z))
			return;
		
		// If the location is occupied by an immune block, then reschedule.
		if (blockRift.isBlockImmune(world, x, y, z))
		{
			scheduleRegeneration(link, MIN_RESCHEDULE_DELAY, MAX_RESCHEDULE_DELAY);
		}
		else
		{
			// All of the necessary conditions have been met. Restore the rift!
			Block block = world.getBlock(x, y, z);
			if (world.setBlock(x, y, z, blockRift))
				blockRift.dropWorldThread(block, world, x, y, z, random);
		}
	}
	
}
