package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.model.ids.Location;

public class LowerTooDeep extends Error.LocationError {
    public LowerTooDeep(Location location) {
        super(location);
    }

    @Override
    public String toString() {
        return "This lower element isn't in a matching stage element. If multiple stage elements are nested, the number of corresponding nested lower elements can't exceed the number of stage elements.";
    }
}
