package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeBindingKey;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.error.MortarInvalidCast;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.ROPair;

public interface MortarDataType extends MortarType {

  JavaDataDescriptor type_jvmDesc();

  Value type_stackAsValue();

  JavaBytecode type_returnBytecode();

  MortarObjectField type_newField(MortarObjectInnerType parentType, String fieldName);
}
