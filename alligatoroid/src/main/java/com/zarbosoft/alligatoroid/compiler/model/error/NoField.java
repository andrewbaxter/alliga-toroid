package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.value.WholeValue;
import com.zarbosoft.rendaw.common.Format;

public class NoField extends Error.LocationError {
    public final WholeValue field;

    public NoField(Location location, WholeValue field) {
        super(location);
        this.field = field;
    }

    @Override
    public String toString() {
        return Format.format("Field [%s] doesn't exist", field.concreteValue());
    }
}
