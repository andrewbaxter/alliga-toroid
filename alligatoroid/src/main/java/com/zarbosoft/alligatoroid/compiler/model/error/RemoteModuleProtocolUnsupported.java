package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.Format;

public class RemoteModuleProtocolUnsupported extends Error.LocationError {
    public final String url;

    public RemoteModuleProtocolUnsupported(Location location, String url) {
        super(location);
        this.url = url;
    }

    @Override
    public String toString() {
        return Format.format("Remote module url [%s] has an unsupported protocol", url);
    }
}
