package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.Format;

public class RemoteModuleHashMismatch extends Error.LocationError {
    public final String url;
    public final String wantHash;
    public final String foundHash;

    public RemoteModuleHashMismatch(
            Location location, String url, String wantHash, String foundHash) {
        super(location);
        this.url = url;
        this.wantHash = wantHash;
        this.foundHash = foundHash;
    }

    @Override
    public String toString() {
        return Format.format(
                "Downloaded module at %s has hash %s but expected hash %s", url, foundHash, wantHash);
    }
}
