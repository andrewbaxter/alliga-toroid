package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeBindingKey;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeSequence;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCode;
import com.zarbosoft.rendaw.common.ROPair;

public interface MortarObjectFieldstateData  extends MortarObjectFieldstate{
@Override
    default ROPair<TargetCode, Binding> fieldstate_bind(EvaluationContext context, Location location, MortarDeferredCode parentCode) {
        JavaBytecodeBindingKey key = new JavaBytecodeBindingKey();
        return new ROPair<>(
                new MortarTargetCode(
                        new JavaBytecodeSequence()
                                .add(((MortarTargetCode) fieldstate_consume(context,location, parentCode)).e)
                                .add(fieldstate_storeBytecode(key))),
                new MortarDataBinding(key, fieldstate_newTypestate()));
    }

    JavaBytecode fieldstate_storeBytecode(JavaBytecodeBindingKey key);
    MortarDataTypestate fieldstate_newTypestate();

    JavaBytecode fieldstate_consume(EvaluationContext context, Location location, MortarDeferredCode parentCode);

    MortarDataType fieldstate_asType();
}
