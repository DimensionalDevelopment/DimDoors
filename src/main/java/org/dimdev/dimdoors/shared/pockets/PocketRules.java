package org.dimdev.dimdoors.shared.pockets;



import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@NoArgsConstructor
public class PocketRules {
    @Getter @Setter private PocketRule breakBlockRule = new PocketRule(false, new ArrayList<String>());
    @Getter @Setter private PocketRule interactBlockRule = new PocketRule(false, new ArrayList<String>());
    @Getter @Setter private PocketRule useItemOnBlockRule = new PocketRule(false, new ArrayList<String>());
    @Getter @Setter private PocketRule useItemOnAirRule = new PocketRule(false, new ArrayList<String>());
}


