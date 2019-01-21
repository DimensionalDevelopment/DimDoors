package org.dimdev.dimdoors.shared.pockets;

import lombok.NoArgsConstructor;

import java.util.HashMap;

@NoArgsConstructor
public class PocketRules {
    public static final HashMap<String, PocketRules> GROUPRULESMAP = new HashMap<String, PocketRules>();
    public static final PocketRules EMPTY = new PocketRules();

    private final HashMap<String, PocketRule> rules = new HashMap<String, PocketRule>();
    private String group;

    public PocketRules(String group) {
        this.group = group;
    }

    public PocketRule get(String ruleName) {
        PocketRule rule = rules.get(ruleName);
        if (rule == null && group != null) {
            PocketRules groupRules = GROUPRULESMAP.get(group);
            if (groupRules != null) return groupRules.get(ruleName);
            return PocketRule.EMPTY;
        }
        return rule;
    }

    public PocketRule put(String ruleName, PocketRule rule) {
        rules.put(ruleName, rule);
        return rule;
    }
}
