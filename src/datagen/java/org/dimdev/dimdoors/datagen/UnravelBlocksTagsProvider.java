package org.dimdev.dimdoors.datagen;

import java.nio.file.Path;

import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.server.AbstractTagProvider;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.tag.FabricTagBuilder;

public class UnravelBlocksTagsProvider extends AbstractTagProvider<Block> {
    public UnravelBlocksTagsProvider(DataGenerator root) {
        super(root, Registry.BLOCK);
    }

    @Override
    protected void configure() {

    }

    @Override
    protected Path getOutput(Identifier id) {
        return this.root.getOutput().resolve("data/" + id.getNamespace() + "/tags/blocks/unravel/" + id.getPath() + ".json");
    }

    @Override
    public String getName() {
        return "Block Tags";
    }
}
