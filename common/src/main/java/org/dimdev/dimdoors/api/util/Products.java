package org.dimdev.dimdoors.api.util;

import com.mojang.datafixers.Products.P9;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.K1;

public class Products {
    public static <F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9> P9<F, T1, T2, T3, T4, T5, T6, T7, T8, T9> and(com.mojang.datafixers.Products.P8<F, T1, T2, T3, T4, T5, T6, T7, T8> p8, App<F, T9> app) {
        return new P9<>(p8.t1(), p8.t2(), p8.t3(), p8.t4(), p8.t5(), p8.t6(), p8.t7(), p8.t8(), app);
    }
}
