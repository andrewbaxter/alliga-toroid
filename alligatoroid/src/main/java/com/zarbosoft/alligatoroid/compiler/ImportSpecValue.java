package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.mortar.SimpleValue;
import com.zarbosoft.alligatoroid.compiler.mortar.WholeValue;
import com.zarbosoft.luxem.write.Writer;

public class ImportSpecValue implements SimpleValue {
    public final ImportSpec spec;

    public ImportSpecValue(ImportSpec spec) {
        this.spec = spec;
    }
}
