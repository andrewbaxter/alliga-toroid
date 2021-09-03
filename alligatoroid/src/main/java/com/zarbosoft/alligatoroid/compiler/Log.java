package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.rendaw.common.TSList;

public class Log {
    public final TSList<Error> errors = new TSList<>();
    public final TSList<Error> warnings = new TSList<>();
    public final TSList<String> log = new TSList<>();
}
