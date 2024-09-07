package org.dimdev.dimdoors.item.component.fabric;

import dev.onyxstudios.cca.api.v3.item.ItemComponent;
import net.minecraft.world.item.ItemStack;
import org.dimdev.dimdoors.DimensionalDoorsComponents;
import org.dimdev.dimdoors.item.component.CounterComponent;

public class CounterComponentImpl extends ItemComponent implements CounterComponent {
    public CounterComponentImpl(ItemStack stack) {
        super(stack);
        if (!this.hasTag("counter"))
            this.putInt("counter", 0);
    }

    public int increment() {
        int counter = count();
        putInt("counter", counter + 1);
        return counter;
    }

    public int count() {
        return getInt("counter");
    }

    public void reset() {
        putInt("counter", 0);
    }

    public static CounterComponent get(ItemStack provider) {
        return DimensionalDoorsComponents.COUNTER_COMPONENT_KEY.get(provider);
    }
}
