package com.zarbosoft.alligatoroid.compiler.jvm.modelother;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportable;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMType;

public class JVMSoftTypeType implements JVMSoftType, AutoBuiltinExportable {
    @Param
    public JVMType type;

    public static JVMSoftTypeType create(JVMType type) {
        final JVMSoftTypeType out = new JVMSoftTypeType();
        out.type = type;
        return out;
    }

    @Override
    public JVMType resolve(EvaluationContext context) {
        return type;
    }
}
