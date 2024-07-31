package org.dimdev.dimdoors.mixin;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.DataFixer;
import net.minecraft.core.HolderGetter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.dimdev.dimdoors.api.util.SchematicStructureTemplate;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Mixin(StructureTemplateManager.class)
public abstract class StructureTemplateManagerMixin {
	private static final FileToIdConverter SCHEM_LISTER = new FileToIdConverter("structures", ".schem");

	@Shadow
	private ResourceManager resourceManager;

	@Shadow @Final private static Logger LOGGER;

	@Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableList$Builder;add(Ljava/lang/Object;)Lcom/google/common/collect/ImmutableList$Builder;", ordinal = 2, shift = At.Shift.AFTER, remap = false), locals = LocalCapture.CAPTURE_FAILHARD)
	private void addFabricTemplateProvider(ResourceManager resourceManager, LevelStorageSource.LevelStorageAccess session, DataFixer dataFixer, HolderGetter<Block> blockLookup, CallbackInfo ci, ImmutableList.Builder<StructureTemplateManager.Source> builder) {
		builder.add(new StructureTemplateManager.Source(this::loadSchemFromResource, this::listSchemResources));
	}

	private Optional<StructureTemplate> loadSchemFromResource(ResourceLocation id) {
		ResourceLocation resourceLocation = SCHEM_LISTER.idToFile(id);

		System.out.println("Blarge: " + id);

		return this.loadSchem(() -> this.resourceManager.open(resourceLocation), (throwable) -> {
			LOGGER.error("Couldn't load structure {}", (Object) id, throwable);
		});
	}

	private Stream<ResourceLocation> listSchemResources() {
		return SCHEM_LISTER.listMatchingResources(this.resourceManager).keySet().stream().map(SCHEM_LISTER::fileToId);
	}

	private Optional<StructureTemplate> loadSchem(StructureTemplateManager.InputStreamOpener inputStream, Consumer<Throwable> onError) {
		Optional<StructureTemplate> optional;

		InputStream is = null;

		try {
			is = inputStream.open();
			optional = Optional.of(this.readSchematic(is));
		} catch (Throwable e) {
			try {
				if(is != null) {
					try {
						is.close();
					} catch (Throwable throwable) {
						e.addSuppressed(throwable);
					}
				}

				throw e;
			} catch (IOException ex) {
				onError.accept(ex);
				return Optional.empty();
			}
		}


		return optional;
	}

	private SchematicStructureTemplate readSchematic(InputStream is) throws IOException {
		CompoundTag compoundTag = NbtIo.readCompressed(is);
		return new SchematicStructureTemplate(compoundTag);
	}
}
