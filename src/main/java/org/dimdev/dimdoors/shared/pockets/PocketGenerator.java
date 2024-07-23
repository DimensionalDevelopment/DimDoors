package org.dimdev.dimdoors.shared.pockets;

import org.dimdev.dimdoors.shared.rifts.targets.VirtualTarget;
import org.dimdev.dimdoors.shared.rifts.registry.LinkProperties;
import org.dimdev.dimdoors.shared.world.ModDimensions;
import org.dimdev.pocketlib.Pocket;
import org.dimdev.pocketlib.PocketRegistry;
import org.dimdev.pocketlib.VirtualLocation;

import java.util.Random;

import static org.dimdev.dimdoors.DimDoors.log;
import static org.dimdev.dimdoors.shared.ModConfig.pockets;
import static org.dimdev.dimdoors.shared.pockets.SchematicHandler.INSTANCE;

public final class PocketGenerator {

    public static Pocket generatePocketFromTemplate(int dim, PocketTemplate template, VirtualLocation location, boolean setup) {
        log.info("Generating pocket from template {} at virtual location {}",template.getId(),location);
        PocketRegistry registry = PocketRegistry.instance(dim);
        Pocket pocket = registry.newPocket();
        template.place(pocket,setup);
        pocket.setVirtualLocation(location);
        if(setup) template.setup(pocket,null,null);
        return pocket;
    }

    public static Pocket generatePrivatePocket(VirtualLocation location) {
        PocketTemplate template = INSTANCE.getPersonalPocketTemplate();
        return generatePocketFromTemplate(ModDimensions.getPrivateDim(),template,location,true);
    }

    // TODO: size of public pockets should increase with depth
    public static Pocket generatePublicPocket(VirtualLocation location, VirtualTarget linkTo, LinkProperties properties) {
        PocketTemplate template = INSTANCE.getPublicPocketTemplate();
        Pocket pocket = generatePocketFromTemplate(ModDimensions.getPublicDim(),template,location,false);
        template.setup(pocket, linkTo, properties);
        return pocket;
    }

    /**
     * Create a dungeon pockets at a certain depth.
     *
     * @param location The virtual location of the pockets
     * @return The newly generated dungeon pockets
     */
    public static Pocket generateDungeonPocket(VirtualLocation location, VirtualTarget linkTo, LinkProperties properties) {
        int depth = location.getDepth();
        float netherChance = location.getDim()==-1 ? 1 : (float)depth/200f; // TODO: improve nether probability
        Random random = new Random();
        String group = random.nextFloat()<netherChance ? "nether" : "ruins";
        PocketTemplate template = INSTANCE.getRandomTemplate(group,depth,pockets.maxPocketSize,false);
        Pocket pocket = generatePocketFromTemplate(ModDimensions.getDungeonDim(),template,location,false);
        template.setup(pocket, linkTo, properties);
        return pocket;
    }
}
