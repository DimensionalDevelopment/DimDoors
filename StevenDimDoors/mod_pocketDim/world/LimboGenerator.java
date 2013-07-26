package StevenDimDoors.mod_pocketDim.world;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderGenerate;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.MapGenRavine;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.feature.MapGenScatteredFeature;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.terraingen.ChunkProviderEvent;
import StevenDimDoors.mod_pocketDim.DDProperties;
import StevenDimDoors.mod_pocketDim.ticking.CommonTickHandler;
import StevenDimDoors.mod_pocketDim.ticking.MonolithSpawner;

public class LimboGenerator extends ChunkProviderGenerate implements IChunkProvider
{
	private static Random rand;

	/** A NoiseGeneratorOctaves used in generating terrain */
	private NoiseGeneratorOctaves noiseGen1;

	/** A NoiseGeneratorOctaves used in generating terrain */
	private NoiseGeneratorOctaves noiseGen2;

	/** A NoiseGeneratorOctaves used in generating terrain */
	private NoiseGeneratorOctaves noiseGen3;

	/** A NoiseGeneratorOctaves used in generating terrain */
	private NoiseGeneratorOctaves noiseGen4;

	/** A NoiseGeneratorOctaves used in generating terrain */
	public NoiseGeneratorOctaves noiseGen5;

	public World world;
	private final byte[] field_82700_c = new byte[256];
	private final byte[] field_82698_d = new byte[256];

	/** A NoiseGeneratorOctaves used in generating terrain */
	public NoiseGeneratorOctaves noiseGen6;
	public NoiseGeneratorOctaves mobSpawnerNoise;

	/** Reference to the World object. */
	private World worldObj;

	/** are map structures going to be generated (e.g. strongholds) */
	private final boolean mapFeaturesEnabled = false;

	/** Holds the overall noise array used in chunk generation */
	private double[] noiseArray;
	private double[] stoneNoise = new double[256];
	private MapGenBase caveGenerator = new CustomCaveGen();

	/** Holds Stronghold Generator */
	private MapGenStronghold strongholdGenerator = new MapGenStronghold();

	/** Holds Village Generator */
	private MapGenVillage villageGenerator = new MapGenVillage();

	/** Holds Mineshaft Generator */
	private MapGenMineshaft mineshaftGenerator = new MapGenMineshaft();
	private MapGenScatteredFeature scatteredFeatureGenerator = new MapGenScatteredFeature();

	/** Holds ravine generator */
	private MapGenBase ravineGenerator = new MapGenRavine();

	/** The biomes that are used to generate the chunk */
	private BiomeGenBase[] biomesForGeneration = new BiomeGenBase[1];

	/** A double array that hold terrain noise from noiseGen3 */
	double[] noise3;

	/** A double array that hold terrain noise */
	double[] noise1;

	/** A double array that hold terrain noise from noiseGen2 */
	double[] noise2;

	/** A double array that hold terrain noise from noiseGen5 */
	double[] noise5;

	/** A double array that holds terrain noise from noiseGen6 */
	double[] noise6;

	/**
	 * Used to store the 5x5 parabolic field that is used during terrain generation.
	 */
	float[] parabolicField;
	int[][] field_73219_j = new int[32][32];
	{
		//     caveGenerator = TerrainGen.getModdedMapGen(caveGenerator, CAVE);
	}

	private DDProperties properties;
	private MonolithSpawner spawner;

	public LimboGenerator(World world, long seed, MonolithSpawner spawner, DDProperties properties) 
	{
		super(world, seed, false);

		LimboGenerator.rand = new Random(seed);
		this.noiseGen1 = new NoiseGeneratorOctaves(LimboGenerator.rand, 16); //base terrain
		this.noiseGen2 = new NoiseGeneratorOctaves(LimboGenerator.rand, 16); //hillyness
		this.noiseGen3 = new NoiseGeneratorOctaves(LimboGenerator.rand, 80);  //seems to adjust the size of features, how stretched things are -default 8
		this.noiseGen4 = new NoiseGeneratorOctaves(LimboGenerator.rand, 4);
		this.noiseGen5 = new NoiseGeneratorOctaves(LimboGenerator.rand, 10);
		this.noiseGen6 = new NoiseGeneratorOctaves(LimboGenerator.rand, 16);
		this.mobSpawnerNoise = new NoiseGeneratorOctaves(LimboGenerator.rand, 8);

		NoiseGeneratorOctaves[] noiseGens = {noiseGen1, noiseGen2, noiseGen3, noiseGen4, noiseGen5, noiseGen6, mobSpawnerNoise};
		//     noiseGens = TerrainGen.getModdedNoiseGenerators(par1World, this.rand, noiseGens);
		this.noiseGen1 = noiseGens[0];
		this.noiseGen2 = noiseGens[1];
		this.noiseGen3 = noiseGens[2];
		this.noiseGen4 = noiseGens[3];
		this.noiseGen5 = noiseGens[4];
		this.noiseGen6 = noiseGens[5];
		this.mobSpawnerNoise = noiseGens[6];
		
		this.worldObj = world; 

		this.spawner = spawner;
		this.properties = properties;
	}

	@Override
	public boolean chunkExists(int var1, int var2) {
		// TODO Auto-generated method stub
		return super.chunkExists(var1, var2);
	}
	@Override
	public void replaceBlocksForBiome(int par1, int par2, byte[] par3ArrayOfByte, BiomeGenBase[] par4ArrayOfBiomeGenBase)
	{

	}

	@Override
	public Chunk provideChunk(int chunkX, int chunkZ)
	{
		//TODO: Wtf? Why do you reinitialize the seed when we already initialized it in the constructor?! ~SenseiKiwi
		LimboGenerator.rand.setSeed((long) chunkX * 341873128712L + (long) chunkZ * 132897987541L);
		byte[] var3 = new byte[32768];
		this.generateTerrain(chunkX, chunkZ, var3);
		Chunk var4 = new Chunk(this.worldObj, var3, chunkX, chunkZ);
		var4.generateSkylightMap();
		
		if (!var4.isTerrainPopulated)
		{
			var4.isTerrainPopulated=true;
			spawner.registerChunkForPopulation(properties.LimboDimensionID, chunkX, chunkZ);
		}
		return var4;
	}
	
	@Override
	public Chunk loadChunk(int var1, int var2) {
		// TODO Auto-generated method stub
		return super.provideChunk(var1, var2);
	}

	@Override
	public void populate(IChunkProvider var1, int var2, int var3) 
	{
		
	}

	@Override
	public boolean saveChunks(boolean var1, IProgressUpdate var2) {
		// TODO Auto-generated method stub
		return super.saveChunks(var1, var2);
	}
	private double[] initializeNoiseField(double[] par1ArrayOfDouble, int par2, int par3, int par4, int par5, int par6, int par7)
	{
		ChunkProviderEvent.InitNoiseField event = new ChunkProviderEvent.InitNoiseField(this, par1ArrayOfDouble, par2, par3, par4, par5, par6, par7);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.getResult() == Result.DENY) return event.noisefield;

		if (par1ArrayOfDouble == null)
		{
			par1ArrayOfDouble = new double[par5 * par6 * par7];
		}

		if (this.parabolicField == null)
		{
			this.parabolicField = new float[25];

			for (int var8 = -2; var8 <= 2; ++var8)
			{
				for (int var9 = -2; var9 <= 2; ++var9)
				{
					float var10 = 10.0F / MathHelper.sqrt_float((float)(var8 * var8 + var9 * var9) + 0.2F);
					this.parabolicField[var8 + 2 + (var9 + 2) * 5] = var10;
				}
			}
		}

		double var44 = 884.412D; //large values here create spiky land. add a 0, good -default 884
		double var45 = 9840.412D; //large values here make sheets- default - 684
		this.noise5 = this.noiseGen5.generateNoiseOctaves(this.noise5, par2, par4, par5, par7, 1.121D, 1.121D, 0.5D);
		this.noise6 = this.noiseGen6.generateNoiseOctaves(this.noise6, par2, par4, par5, par7, 200.0D, 200.0D, 0.5D);
		this.noise3 = this.noiseGen3.generateNoiseOctaves(this.noise3, par2, par3, par4, par5, par6, par7, var44 / 80.0D, var45 / 160.0D, var44 / 80.0D);
		this.noise1 = this.noiseGen1.generateNoiseOctaves(this.noise1, par2, par3, par4, par5, par6, par7, var44, var45, var44);
		this.noise2 = this.noiseGen2.generateNoiseOctaves(this.noise2, par2, par3, par4, par5, par6, par7, var44, var45, var44);
		boolean var43 = false;
		boolean var42 = false;
		int var12 = 0;
		int var13 = 0;

		for (int var14 = 0; var14 < par5; ++var14)
		{
			for (int var15 = 0; var15 < par7; ++var15)
			{
				float var16 = 0.0F;
				float var17 = 0.0F;
				float var18 = 0.0F;
				byte var19 = 2;
				BiomeGenBase var20 = this.biomesForGeneration[var14 + 2 + (var15 + 2) * (par5 + 5)];

				for (int var21 = -var19; var21 <= var19; ++var21)
				{
					for (int var22 = -var19; var22 <= var19; ++var22)
					{
						float var24 = this.parabolicField[var21 + 2 + (var22 + 2) * 5] / (BiomeGenBase.plains.minHeight + 9.0F);


						//this adjusts the height of the terrain

						var16 += BiomeGenBase.plains.maxHeight * var24+4;
						var17 += BiomeGenBase.plains.minHeight * var24-1;
						var18 += var24;
					}
				}

				var16 /= var18;
				var17 /= var18;
				var16 =  (var16 * 0.9F + 0.1F);
				var17 = (var17 * 4.0F - 1.0F) / 8.0F;
				double var47 = this.noise6[var13] / 8000.0D;

				if (var47 < 0.0D)
				{
					var47 = -var47 * 0.3D;
				}

				var47 = var47 * 3.0D - 2.0D;

				if (var47 < 0.0D)
				{
					var47 /= 2.0D;

					if (var47 < -1.0D)
					{
						var47 = -1.0D;
					}

					var47 /= 1.4D;
					var47 /= 2.0D;
				}
				else
				{
					if (var47 > 1.0D)
					{
						var47 = 1.0D;
					}

					var47 /= 8.0D;
				}

				++var13;

				for (int var46 = 0; var46 < par6; ++var46)
				{
					double var48 = (double)var17;
					double var26 = (double)var16;
					var48 += var47 * 0.2D;
					var48 = var48 * (double)par6 / 16.0D;
					double var28 = (double)par6 / 2.0D + var48 * 4.0D;
					double var30 = 0.0D;
					double var32 = ((double)var46 - var28) * 12.0D * 128.0D / 128.0D / var26;

					if (var32 < 0.0D)
					{
						var32 *= 4.0D;
					}

					double var34 = this.noise1[var12] / 512.0D;
					double var36 = this.noise2[var12] / 512.0D;
					double var38 = (this.noise3[var12] / 10.0D + 1.0D) / 2.0D;

					if (var38 < 0.0D)
					{
						var30 = var34;
					}
					else if (var38 > 1.0D)
					{
						var30 = var36;
					}
					else
					{
						var30 = var34 + (var36 - var34) * var38;
					}

					var30 -= var32;

					if (var46 > par6 - 4)
					{
						double var40 = (double)((float)(var46 - (par6 - 4)) / 3.0F);
						var30 = var30 * (1.0D - var40) + -10.0D * var40;
					}

					par1ArrayOfDouble[var12] = var30;
					++var12;
				}
			}
		}

		return par1ArrayOfDouble;
	}
	public void generateTerrain(int par1, int par2, byte[] par3ArrayOfByte)
	{
		byte var4 = 4;
		byte var5 = 16;
		byte var6 = 19;
		int var7 = var4 + 1;
		byte var8 = 17;
		int var9 = var4 + 1;
		this.biomesForGeneration = this.worldObj.getWorldChunkManager().getBiomesForGeneration(this.biomesForGeneration, par1 * 4 - 2, par2 * 4 - 2, var7 + 5, var9 + 5);
		this.noiseArray = this.initializeNoiseField(this.noiseArray, par1 * var4, 0, par2 * var4, var7, var8, var9);

		for (int var10 = 0; var10 < var4; ++var10)
		{
			for (int var11 = 0; var11 < var4; ++var11)
			{
				for (int var12 = 0; var12 < var5; ++var12)
				{
					double var13 = 0.125D;
					double var15 = this.noiseArray[((var10 + 0) * var9 + var11 + 0) * var8 + var12 + 0];
					double var17 = this.noiseArray[((var10 + 0) * var9 + var11 + 1) * var8 + var12 + 0];
					double var19 = this.noiseArray[((var10 + 1) * var9 + var11 + 0) * var8 + var12 + 0];
					double var21 = this.noiseArray[((var10 + 1) * var9 + var11 + 1) * var8 + var12 + 0];
					double var23 = (this.noiseArray[((var10 + 0) * var9 + var11 + 0) * var8 + var12 + 1] - var15) * var13;
					double var25 = (this.noiseArray[((var10 + 0) * var9 + var11 + 1) * var8 + var12 + 1] - var17) * var13;
					double var27 = (this.noiseArray[((var10 + 1) * var9 + var11 + 0) * var8 + var12 + 1] - var19) * var13;
					double var29 = (this.noiseArray[((var10 + 1) * var9 + var11 + 1) * var8 + var12 + 1] - var21) * var13;

					for (int var31 = 0; var31 < 8; ++var31)
					{
						double var32 = 0.25D;
						double var34 = var15;
						double var36 = var17;
						double var38 = (var19 - var15) * var32;
						double var40 = (var21 - var17) * var32;

						for (int var42 = 0; var42 < 4; ++var42)
						{
							int var43 = var42 + var10 * 4 << 11 | 0 + var11 * 4 << 7 | var12 * 8 + var31;
							short var44 = 128;
							var43 -= var44;
							double var45 = 0.25D;
							double var49 = (var36 - var34) * var45;
							double var47 = var34 - var49;

							for (int var51 = 0; var51 < 4; ++var51)
							{
								if ((var47 += var49) > 0.0D)
								{
									par3ArrayOfByte[var43 += var44] = (byte)properties.LimboBlockID;
								}
								else if (var12 * 8 + var31 < var6)
								{
									par3ArrayOfByte[var43 += var44] = (byte)properties.PermaFabricBlockID;
								}

								else
								{
									par3ArrayOfByte[var43 += var44] = 0;
								}
							}

							var34 += var38;
							var36 += var40;
						}

						var15 += var23;
						var17 += var25;
						var19 += var27;
						var21 += var29;
					}
				}
			}
		}
	}


	@Override
	public boolean canSave() {
		// TODO Auto-generated method stub
		return super.canSave();
	}

	@Override
	public String makeString() {
		// TODO Auto-generated method stub
		return super.makeString();
	}

	@Override
	public List getPossibleCreatures(EnumCreatureType par1EnumCreatureType, int par2, int par3, int par4)
	{

		BiomeGenBase biomegenbase = this.worldObj.getBiomeGenForCoords(par2, par4);
		return biomegenbase == null ? null : (biomegenbase == BiomeGenBase.swampland && par1EnumCreatureType == EnumCreatureType.monster && this.scatteredFeatureGenerator.hasStructureAt(par2, par3, par4) ? this.scatteredFeatureGenerator.getScatteredFeatureSpawnList() : biomegenbase.getSpawnableList(par1EnumCreatureType));
	}

	@Override
	public ChunkPosition findClosestStructure(World var1, String var2,
			int var3, int var4, int var5) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getLoadedChunkCount() {
		// TODO Auto-generated method stub
		return super.getLoadedChunkCount();
	}

	@Override
	public void recreateStructures(int var1, int var2) {
		// TODO Auto-generated method stub

	}

}