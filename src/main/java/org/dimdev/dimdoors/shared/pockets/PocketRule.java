package org.dimdev.dimdoors.shared.pockets;

import lombok.AllArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
public class PocketRule {
    @Setter private boolean whitelist;
    @Setter private List<String> matches;

    public boolean matches(String itemOrBlockName) {
        for (String match : matches) {
            if (itemOrBlockName.matches(match)) {
                return !whitelist;
            }
        }
        return whitelist;
    }
}
