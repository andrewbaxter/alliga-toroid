package com.zarbosoft.alligatoroid.compiler.jvm;

import com.zarbosoft.alligatoroid.compiler.Error;
import com.zarbosoft.alligatoroid.compiler.Module;
import com.zarbosoft.alligatoroid.compiler.language.Builtin;
import com.zarbosoft.alligatoroid.compiler.mortar.Record;
import com.zarbosoft.rendaw.common.ROTuple;
import com.zarbosoft.rendaw.common.TSList;

public class JVMClassBuilder {
    public final JVMClassType base;

    public JVMClassBuilder(JVMClassType base) {
        this.base = base;
    }

    @Builtin.WrapExpose
    public JVMMethod declareMethod(String name, Record spec) {
        JVMShallowMethodFieldType.MethodSpecDetails specDetails =
                JVMShallowMethodFieldType.methodSpecDetails(spec);
        ROTuple keyTuple = ROTuple.create(name).append(specDetails.keyTuple);
        base.incompleteMethods.add(keyTuple);
        return new JVMMethod(base, keyTuple, specDetails);
    }

    @Builtin.WrapExpose
    public byte[] bytes() {
        TSList<Error> errors = new TSList<>();
        base.build(errors);
        if (errors.some()) throw new MultiError(errors);
        return base.built;
    }
}
