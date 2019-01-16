package org.dimdev.dimdoors.shared.pockets;



import lombok.AllArgsConstructor;
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

    public PocketRules copy() {
        PocketRules copy = new PocketRules();
        copy.setBreakBlockRule(this.breakBlockRule.copy());
        copy.setInteractBlockRule(this.interactBlockRule.copy());
        copy.setUseItemOnBlockRule(this.useItemOnBlockRule.copy());
        copy.setUseItemOnAirRule(this.useItemOnAirRule.copy());
        return copy;
    }
}


