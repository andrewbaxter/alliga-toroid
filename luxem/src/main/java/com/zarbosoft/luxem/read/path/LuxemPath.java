package com.zarbosoft.luxem.read.path;

import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROPair;

public class LuxemPath {
    /**
     * First is index, second is key=true/value=false (always false except in records)
     */
    public final ROList<ROPair<Integer, Boolean>> data;

    public LuxemPath(ROList<ROPair<Integer, Boolean>> data) {
        this.data = data;
    }
}
