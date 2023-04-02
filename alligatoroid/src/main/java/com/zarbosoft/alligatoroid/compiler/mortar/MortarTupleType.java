package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCodeStack;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarDataValueVariableStack;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSList;

public class MortarTupleType implements MortarDataType {
    public final ROList<MortarDataType> fields;

    public MortarTupleType(ROList<MortarDataType> fields) {
        this.fields = fields;
    }

    @Override
    public JavaDataDescriptor type_jvmDesc() {
    return MortarTupleTypestate.DESC;
    }

    @Override
    public Value type_stackAsValue(JavaBytecode code) {
        return new MortarDataValueVariableStack(type_newTypestate(), new MortarDeferredCodeStack(code));
    }

    @Override
    public JavaBytecode type_returnBytecode() {
        return JavaBytecodeUtils.returnObj;
    }

    @Override
    public MortarDataTypestate type_newTypestate() {
        final TSList<MortarTupleFieldType> outFields = new TSList<>();
    for (MortarDataType field : fields) {
    outFields.add(field.prototype_newTupleFieldType());
    }
        return new MortarTupleTypestate(outFields);
    }

    @Override
    public MortarProtofield prototype_newProtofield(MortarObjectInnerType baseMeta, String name) {
    return new MortarTupleProtofield(fields);
    }
}
