package org.dimdev.dimdoors.shared.pockets;



import lombok.NoArgsConstructor;

import java.util.HashMap;


@NoArgsConstructor
public class PocketRules {
    public static final PocketRules EMPTY = new PocketRules();

    private final HashMap<String, PocketRule> rules = new HashMap<String, PocketRule>();

    public PocketRule get(String ruleName) {
        PocketRule rule = rules.get(ruleName);
        if (rule == null) return PocketRule.EMPTY;
        else return rule;
    }

    public PocketRule put(String ruleName, PocketRule rule) {
        return rules.put(ruleName, rule);
    }
}


