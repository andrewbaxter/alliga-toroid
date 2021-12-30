package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.model.ids.Location;

public class IncompatibleTargetValues extends Error.LocationError {
    public final String expectedTarget;
    public final String gotTarget;

    public IncompatibleTargetValues(Location location, String expectedTarget, String gotTarget) {
        super(location);
        this.expectedTarget = expectedTarget;
        this.gotTarget = gotTarget;
    }

    @Override
    public String toString() {
        return "ASSERTION! This block contains values for incompatible targets";
    }
}
