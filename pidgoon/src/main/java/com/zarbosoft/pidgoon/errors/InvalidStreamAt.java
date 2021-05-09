package com.zarbosoft.pidgoon.errors;

import com.zarbosoft.pidgoon.errors.InvalidStream;

public class InvalidStreamAt extends InvalidStream {
    public final Object at;

    public InvalidStreamAt(Object at, InvalidStream e) {
        super(e.step);
        this.at = at;
    }
}
