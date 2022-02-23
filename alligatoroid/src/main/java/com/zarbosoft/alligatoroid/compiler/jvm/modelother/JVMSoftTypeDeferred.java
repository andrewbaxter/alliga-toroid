package com.zarbosoft.alligatoroid.compiler.jvm.modelother;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportable;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMExternClassInstanceType;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMType;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;

public class JVMSoftTypeDeferred implements JVMSoftType, AutoBuiltinExportable {
    @Param
    public ImportId importId;

    public static JVMSoftType create(ImportId importId) {
        final JVMSoftTypeDeferred out = new JVMSoftTypeDeferred();
        out.importId = importId;
        return out;
    }

    @Override
    public JVMType resolve(EvaluationContext context) {
        return JVMExternClassInstanceType.resolveType(context, importId);
    }
}
