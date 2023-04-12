package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROPair;

public class StaticMethodCallSignature {
    public final ROList<ROPair<Object, MortarDataType>> arguments;
    public final MortarDataType returnType;

    public StaticMethodCallSignature(ROList<ROPair<Object, MortarDataType>> arguments, MortarDataType returnType) {
        this.arguments = arguments;
        this.returnType = returnType;
    }
}
