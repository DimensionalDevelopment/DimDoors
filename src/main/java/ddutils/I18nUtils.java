package ddutils;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public final class I18nUtils {
    @SideOnly(Side.CLIENT)
    public static void translateAndAdd(String key, List<String> list) { // TODO: move to utils?
        int i = 0;
        while (I18n.hasKey(key + Integer.toString(i))) {
            list.add(I18n.format(key + Integer.toString(i)));
            i++;
        }
    }
}
