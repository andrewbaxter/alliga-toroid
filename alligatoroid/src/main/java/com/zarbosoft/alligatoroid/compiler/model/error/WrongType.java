package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.value.Value;
import com.zarbosoft.rendaw.common.Format;

public class WrongType extends Error.LocationError {
    public final Value got;
    public final String expected;

    public WrongType(Location location, Value got, String expected) {
        super(location);
        this.got = got;
        this.expected = expected;
    }

    @Override
    public String toString() {
        return Format.format("Expected [%s] but got value [%s]", expected, got);
    }
}
