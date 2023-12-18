package org.dimdev.dimdoors.dungeon.pack;

import org.dimdev.dimdoors.util.WeightedContainer;

import java.util.ArrayList;

public class DungeonChainRuleDefinition {
    private final ArrayList<String> conditions;
    private final ArrayList<WeightedContainer<String>> products;

    public DungeonChainRuleDefinition(ArrayList<String> conditions, ArrayList<WeightedContainer<String>> products) {
        //Validate the arguments, just in case
        if (conditions == null) {
            throw new NullPointerException("conditions cannot be null");
        }
        if (products.isEmpty()) {
            throw new IllegalArgumentException("products cannot be an empty list");
        }
        for (WeightedContainer<String> product : products) {
            //Check for weights less than 1. Those could cause Minecraft's random selection algorithm to throw an exception.
            //At the very least, they're useless values.
            if (product.itemWeight < 1) {
                throw new IllegalArgumentException("products cannot contain items with weights less than 1");
            }
        }

        this.conditions = conditions;
        this.products = products;
    }

    public ArrayList<String> getCondition() {
        return conditions;
    }

    public ArrayList<WeightedContainer<String>> getProducts() {
        return products;
    }

}
