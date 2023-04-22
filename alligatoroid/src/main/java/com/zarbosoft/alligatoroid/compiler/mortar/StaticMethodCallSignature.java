package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROPair;

public class StaticMethodCallSignature {
    public final ROList<ROPair<Object, MortarType>> arguments;
    public final MortarType returnType;

    public StaticMethodCallSignature(ROList<ROPair<Object, MortarType>> arguments, MortarType returnType) {
        this.arguments = arguments;
        this.returnType = returnType;
    }
}
