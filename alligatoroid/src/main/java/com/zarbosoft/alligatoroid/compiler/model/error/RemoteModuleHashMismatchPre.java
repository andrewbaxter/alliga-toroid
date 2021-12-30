package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.model.ids.Location;

public class RemoteModuleHashMismatchPre extends Error.PreError {
    public final String url;
    public final String wantHash;
    public final String foundHash;

    public RemoteModuleHashMismatchPre(String url, String wantHash, String foundHash) {
        this.url = url;
        this.wantHash = wantHash;
        this.foundHash = foundHash;
    }

    @Override
    public Error toError(Location location) {
        return new RemoteModuleHashMismatch(location, url, wantHash, foundHash);
    }
}
