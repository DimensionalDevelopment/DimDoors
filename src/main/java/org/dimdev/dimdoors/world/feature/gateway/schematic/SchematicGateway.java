package org.dimdev.dimdoors.world.feature.gateway.schematic;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.function.BiPredicate;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.pockets.TemplateUtils;
import org.dimdev.dimdoors.api.util.BlockPlacementType;
import org.dimdev.dimdoors.util.schematic.Schematic;
import org.dimdev.dimdoors.util.schematic.SchematicPlacer;
import org.dimdev.dimdoors.world.feature.gateway.Gateway;

import net.minecraft.nbt.NbtIo;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;

public abstract class SchematicGateway implements Gateway, BiPredicate<StructureWorldAccess, BlockPos> {
	private Schematic schematic;
	private final String id;
	public static final BiMap<String, SchematicGateway> ID_SCHEMATIC_MAP = HashBiMap.create();
	private boolean replaced;

	public SchematicGateway(String id) {
		ID_SCHEMATIC_MAP.putIfAbsent(id, this);
		this.id = id;
	}

	public void init() {
		String schematicJarDirectory = "/data/dimdoors/gateways/";

		try (InputStream stream = DimensionalDoors.class.getResourceAsStream(schematicJarDirectory + this.id + ".schem")) {
			if (stream == null) {
				throw new RuntimeException("Schematic '" + this.id + "' was not found in the jar or config directory, neither with the .schem extension, nor with the .schematic extension.");
			}
			try {
				this.schematic = Schematic.fromNbt(NbtIo.readCompressed(stream));
			} catch (IOException ex) {
				throw new RuntimeException("Schematic file for " + this.id + " could not be read as a valid schematic NBT file.", ex);
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public final void generate(StructureWorldAccess world, BlockPos pos) {
		if (DimensionalDoors.getConfig()
				.getWorldConfig()
				.gatewayDimBlacklist
				.contains(world.toServerWorld().getRegistryKey().getValue().toString())
		) {
			return;
		}
		if (!this.replaced) {
			TemplateUtils.replacePlaceholders(this.schematic, world);
			this.replaced = true;
		}
		SchematicPlacer.place(this.schematic, world, pos, BlockPlacementType.SECTION_NO_UPDATE_QUEUE_BLOCK_ENTITY);
		this.generateRandomBits(world, pos);
	}

	/**
	 * Generates randomized portions of the gateway structure (e.g. rubble, foliage)
	 *
	 * @param world - the world in which to generate the gateway
	 * @param pos   - the position at which the schematic is placed
	 */
	protected void generateRandomBits(StructureWorldAccess world, BlockPos pos) {
	}
}
