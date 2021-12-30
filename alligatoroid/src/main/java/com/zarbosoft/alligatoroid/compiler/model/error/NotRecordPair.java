package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.model.ids.Location;

public class NotRecordPair extends Error.LocationError {
    public final String gotType;

    public NotRecordPair(Location location, String gotType) {
        super(location);
        this.gotType = gotType;
    }

    @Override
    public String toString() {
        return "This element in a record literal is not a record pair";
    }
}
