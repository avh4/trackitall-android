package net.avh4.trackitall.model;

import net.avh4.trackitall.R;

public interface Counters {
    public static final Counter[] ALL = {
            new Counter("vegetables", R.string.type_vegetables_label, R.id.btn_veg),
            new Counter("fruit", R.string.type_fruit_label, R.id.btn_fruit),
            new Counter("water", R.string.type_water_label, R.id.btn_water)
    };
}
