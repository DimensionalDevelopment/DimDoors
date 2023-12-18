package org.dimdev.dimdoors.schematic;


import net.minecraft.block.Block;

public class ReplacementFilter extends SchematicFilter {

    private final Block targetBlock;
    private byte targetMetadata;
    private final boolean matchMetadata;
    private final Block replacementBlock;
    private byte replacementMetadata;
    private final boolean changeMetadata;

    public ReplacementFilter(Block targetBlock, byte targetMetadata, Block replacementBlock, byte replacementMetadata) {
        super("ReplacementFilter");
        this.targetBlock = targetBlock;
        this.targetMetadata = targetMetadata;
        this.matchMetadata = true;
        this.replacementBlock = replacementBlock;
        this.replacementMetadata = replacementMetadata;
        this.changeMetadata = true;
    }

    public ReplacementFilter(Block targetBlock, Block replacementBlock, byte replacementMetadata) {
        super("ReplacementFilter");
        this.targetBlock = targetBlock;
        this.matchMetadata = false;
        this.replacementBlock = replacementBlock;
        this.replacementMetadata = replacementMetadata;
        this.changeMetadata = true;
    }

    public ReplacementFilter(Block targetBlock, byte targetMetadata, Block replacementBlock) {
        super("ReplacementFilter");
        this.targetBlock = targetBlock;
        this.targetMetadata = targetMetadata;
        this.matchMetadata = true;
        this.replacementBlock = replacementBlock;
        this.changeMetadata = false;
    }

    public ReplacementFilter(Block targetBlock, Block replacementBlock) {
        super("ReplacementFilter");
        this.targetBlock = targetBlock;
        this.matchMetadata = false;
        this.replacementBlock = replacementBlock;
        this.changeMetadata = false;
    }

    @Override
    protected boolean applyToBlock(int index, Block[] blocks, byte[] metadata) {
        if (blocks[index] == targetBlock) {
            if (!matchMetadata || metadata[index] == targetMetadata) {
                blocks[index] = replacementBlock;
                if (changeMetadata) {
                    metadata[index] = replacementMetadata;
                }
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean terminates() {
        return false;
    }
}
