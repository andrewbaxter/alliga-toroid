package com.zarbosoft.alligatoroid.compiler.modules;

import java.nio.file.Path;

public class Source {
    public final String hash;
    public final Path path;

    public Source(String hash, Path path) {
        this.hash = hash;
        this.path = path;
    }
}
