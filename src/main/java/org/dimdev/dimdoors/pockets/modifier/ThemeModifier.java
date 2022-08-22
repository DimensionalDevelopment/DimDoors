package org.dimdev.dimdoors.pockets.modifier;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import org.dimdev.dimdoors.api.util.BlockBoxUtil;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.PocketLoader;
import org.dimdev.dimdoors.pockets.theme.Theme;
import org.dimdev.dimdoors.world.pocket.type.LazyGenerationPocket;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

public class ThemeModifier extends AbstractLazyModifier {
	public static final String KEY = "theme";

	private Identifier themeId = null;

	@Override
	protected NbtCompound toNbtInternal(NbtCompound nbt, boolean allowReference) {
		super.toNbtInternal(nbt, allowReference);
		if(themeId != null) nbt.putString("theme", themeId.toString());
		return nbt;
	}

	@Override
	public void applyToChunk(LazyGenerationPocket pocket, Chunk chunk) {
		BlockBox chunkBox = BlockBoxUtil.getBox(chunk);
		BlockBox pocketBox = pocket.getBox();
		Theme theme = PocketLoader.getInstance().getTheme(themeId);

		if(pocketBox.intersects(chunkBox)) {
			BlockPos.stream(pocketBox).forEach(blockBox -> chunk.setBlockState(blockBox, theme.apply(chunk.getBlockState(blockBox)), false));
		}
	}

	@Override
	public Modifier fromNbt(NbtCompound nbt, ResourceManager manager) {
		themeId = null;

		if(nbt.contains("theme")) {
			themeId = Identifier.tryParse(nbt.getString("theme"));
		}

		return this;
	}

	@Override
	public ModifierType<? extends Modifier> getType() {
		return ModifierType.THEME_MODIFIER_TYPE;
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public void apply(PocketGenerationContext parameters, RiftManager manager) {
		if(!manager.isPocketLazy()) {
			Theme theme = PocketLoader.getInstance().getTheme(themeId);
			ServerWorld world = parameters.world();

			BlockPos.stream(manager.getPocket().getBox()).forEach(blockPos -> world.setBlockState(blockPos, theme.apply(world.getBlockState(blockPos))));
		}
	}

	@Override
	public void apply(PocketGenerationContext parameters, Pocket.PocketBuilder<?, ?> builder) {

	}
}
