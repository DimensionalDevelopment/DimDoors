package org.dimdev.dimdoors.shared.pockets;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor @NoArgsConstructor
public class PocketRule {
    public static final PocketRule EMPTY = new PocketRule();

    @Setter private boolean whitelist = false;
    @Setter private List<String> matches = new ArrayList<>();

    public boolean matches(String itemOrBlockName, String meta) {
        for (String match : matches) {
            if (itemOrBlockName.matches(match) || (itemOrBlockName + ":" + meta).matches(match)) {
                return !whitelist;
            }
        }
        return whitelist;
    }
}
