package StevenDimDoors.mod_pocketDim.world;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.client.IRenderHandler;
import StevenDimDoors.mod_pocketDim.CloudRenderBlank;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.config.DDProperties;
import StevenDimDoors.mod_pocketDim.ticking.CustomLimboPopulator;
import StevenDimDoors.mod_pocketDim.util.Point4D;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class LimboProvider extends WorldProvider
{
	@Override
	public String getDimensionName() {
		return "Limbo";
	}

	private IRenderHandler skyRenderer;
	private DDProperties properties;
	private CustomLimboPopulator spawner;

	public LimboProvider()
	{
		this.hasNoSky = false;
		this.skyRenderer = new LimboSkyProvider();
		this.spawner = mod_pocketDim.spawner;
		this.properties = mod_pocketDim.properties;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IRenderHandler getSkyRenderer()
	{
		return this.skyRenderer;
	}

	@Override
	protected void registerWorldChunkManager()
	{
		super.worldChunkMgr = new WorldChunkManagerHell(mod_pocketDim.limboBiome,1);
	}

	@Override
	public BiomeGenBase getBiomeGenForCoords(int x, int z)
	{
		return mod_pocketDim.limboBiome;
	}

	@Override
	public boolean canRespawnHere()
	{
		return properties.HardcoreLimboEnabled;
	}

	@Override
	public boolean isBlockHighHumidity(int x, int y, int z)
	{
		return false;
	}


	@Override
	public boolean canSnowAt(int x, int y, int z, boolean checkLight)
	{
		return false;
	}
	
	@Override
	protected void generateLightBrightnessTable()
	{
		float modifier = 0.0F;

		for (int steps = 0; steps <= 15; ++steps)
		{
			float var3 = 1.0F - steps / 15.0F;
			this.lightBrightnessTable[steps] = ((0.0F + var3) / (var3 * 3.0F + 1.0F) * (1.0F - modifier) + modifier)*3;
			//     System.out.println( this.lightBrightnessTable[steps]+"light");
		}
	}

	@Override
	public ChunkCoordinates getSpawnPoint()
	{

		return this.getRandomizedSpawnPoint();
	}

	@Override
	public float calculateCelestialAngle(long par1, float par3)
	{
		int var4 = (int)(par1 % 24000L);
		float var5 = (var4 + par3) / 24000.0F - 0.25F;

		if (var5 < 0.0F)
		{
			++var5;
		}

		if (var5 > 1.0F)
		{
			--var5;
		}

		float var6 = var5;
		var5 = 1.0F - (float)((Math.cos(var5 * Math.PI) + 1.0D) / 2.0D);
		var5 = var6 + (var5 - var6) / 3.0F;
		return 0;
	}

	@SideOnly(Side.CLIENT)
	public int getMoonPhase(long par1, float par3)
	{
		return 4;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public String getSaveFolder()
	{
		return (dimensionId == 0 ? null : "DimensionalDoors/Limbo" + dimensionId);
	}

	@Override
	public boolean canCoordinateBeSpawn(int par1, int par2)
	{
		Block block = this.worldObj.getTopBlock(par1, par2);
		return block == mod_pocketDim.blockLimbo;
	}
	@Override
	public double getHorizon()
	{
		return worldObj.getHeight()/4-800;
	}
	@Override
	public Vec3 getSkyColor(Entity cameraEntity, float partialTicks)
	{
		setCloudRenderer( new CloudRenderBlank());
		return Vec3.createVectorHelper(0, 0, 0);

	}
	@SideOnly(Side.CLIENT)
	@Override
	public Vec3 getFogColor(float par1, float par2)
	{
		return Vec3.createVectorHelper(.2, .2, .2);

	}
	@Override
	public int getRespawnDimension(EntityPlayerMP player)
	{
		return 0;
	}

	@Override
	public IChunkProvider createChunkGenerator()
	{
		//TODO: ...We're passing the LimboGenerator a fixed seed. We should be passing the world seed! @_@ ~SenseiKiwi
		return new LimboGenerator(worldObj, 45, spawner, properties);
	}
	
	@Override
	public boolean canBlockFreeze(int x, int y, int z, boolean byWater)
	{
		return false;
	}

	public static Point4D getLimboSkySpawn(EntityPlayer player, DDProperties properties)
	{
		int x = (int) (player.posX) + MathHelper.getRandomIntegerInRange(player.worldObj.rand, -properties.LimboEntryRange, properties.LimboEntryRange);
		int z = (int) (player.posZ) + MathHelper.getRandomIntegerInRange(player.worldObj.rand, -properties.LimboEntryRange, properties.LimboEntryRange);
		return new Point4D(x, 700, z, properties.LimboDimensionID);
	}
	
	@Override
	public ChunkCoordinates getRandomizedSpawnPoint()
	{
		int x = MathHelper.getRandomIntegerInRange(this.worldObj.rand, -500, 500);
		int z = MathHelper.getRandomIntegerInRange(this.worldObj.rand, -500, 500);
		return new ChunkCoordinates(x, 700, z);
	}
}