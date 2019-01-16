package org.dimdev.dimdoors.shared.pockets;



import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;



@NoArgsConstructor
public class PocketRules {
    public static final PocketRules EMPTY = new PocketRules();


    @Getter @Setter private PocketRule breakBlockRule = new PocketRule(false, new ArrayList<String>());
    @Getter @Setter private PocketRule interactBlockRule = new PocketRule(false, new ArrayList<String>());
    @Getter @Setter private PocketRule useItemRule = new PocketRule(false, new ArrayList<String>());
}


