package com.zarbosoft.alligatoroid.compiler.mortar.halftypes;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Meta;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedJVMName;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.error.Unexpected;
import com.zarbosoft.alligatoroid.compiler.model.error.WrongType;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.ContinueError;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.Record;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.StaticMethodMeta;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.Tuple;
import com.zarbosoft.alligatoroid.compiler.mortar.graph.SingletonBuiltinExportable;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ConstDataValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.DataValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ErrorValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.FutureValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.LooseRecord;
import com.zarbosoft.alligatoroid.compiler.mortar.value.LooseTuple;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROOrderedMap;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;

import java.lang.reflect.InvocationTargetException;

import static com.zarbosoft.alligatoroid.compiler.mortar.MortarBuiltin.nullType;
import static com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetModuleContext.convertFunctionArgumentRoot;
import static com.zarbosoft.alligatoroid.compiler.mortar.value.ConstDataBuiltinSingletonValue.nullValue;

public class MortarStaticMethodType extends MortarObjectType implements SingletonBuiltinExportable {
  // TODO move method type info into the type, check during type check
  public static final MortarStaticMethodType type = new MortarStaticMethodType();
  public static final JVMSharedDataDescriptor DESC =
      JVMSharedDataDescriptor.fromObjectClass(StaticMethodMeta.class);

  private MortarStaticMethodType() {}

  public static ConvertImmediateArgRes convertImmediateArg(EvaluationContext context, Value value) {
    if (value instanceof FutureValue) {
      value = ((FutureValue) value).get();
    }
    if (value == ErrorValue.error) {
      return null;
    } else if (value instanceof LooseTuple) {
      TSList<Object> out = new TSList<>();
      final ROList<EvaluateResult> tupleValue = ((LooseTuple) value).data;
      for (EvaluateResult val : tupleValue) {
        if (!context.target.codeEmpty(val.preEffect) || !context.target.codeEmpty(val.postEffect)) {
          return ConvertImmediateArgRes.resHalf();
        }
        final ConvertImmediateArgRes res = convertImmediateArg(context, val.value);
        if (res == null || !res.isWhole) return res;
        out.add(res.whole);
      }
      return ConvertImmediateArgRes.resWhole(Tuple.create(out));
    } else if (value instanceof LooseRecord) {
      final ROOrderedMap<Object, EvaluateResult> recordValue = ((LooseRecord) value).data;
      TSMap<Object, Object> out = new TSMap<>();
      for (ROPair<Object, EvaluateResult> e : recordValue) {
        if (!context.target.codeEmpty(e.second.preEffect)
            || !context.target.codeEmpty(e.second.postEffect)) {
          return ConvertImmediateArgRes.resHalf();
        }
        final ConvertImmediateArgRes res = convertImmediateArg(context, e.second.value);
        if (res == null || !res.isWhole) return res;
        out.put(e.first, res.whole);
      }
      return ConvertImmediateArgRes.resWhole(Record.create(out));
    } else if (value instanceof DataValue) {
      if (value instanceof ConstDataValue)
        return ConvertImmediateArgRes.resWhole(((ConstDataValue) value).getInner());
      else return ConvertImmediateArgRes.resHalf();
    } else throw new Assertion();
  }

  // Returns null if not immediate (half)
  public static ConvertImmediateArgRootRes convertImmediateRootArg(
      EvaluationContext context, Meta.FuncInfo funcInfo, Value argument) {
    Object[] args;
    if (argument == ErrorValue.error) {
      return new ConvertImmediateArgRootRes(true, false, null);
    } else if (argument instanceof LooseTuple) {
      final ROList<EvaluateResult> tupleArg = ((LooseTuple) argument).data;
      args = new Object[(funcInfo.needsModule ? 1 : 0) + tupleArg.size()];
      int arg = 0;
      if (funcInfo.needsModule) args[arg++] = context.moduleContext;
      for (int i = 0; i < tupleArg.size(); i++) {
        final EvaluateResult val = tupleArg.get(i);
        if (!context.target.codeEmpty(val.preEffect) || !context.target.codeEmpty(val.postEffect)) {
          return new ConvertImmediateArgRootRes(false, false, null);
        }
        final ConvertImmediateArgRes res = convertImmediateArg(context, val.value);
        if (res == null) return new ConvertImmediateArgRootRes(true, false, null);
        if (!res.isWhole) return new ConvertImmediateArgRootRes(false, false, null);
        args[arg++] = res.whole;
      }
    } else {
      args = new Object[(funcInfo.needsModule ? 1 : 0) + 1];
      int arg = 0;
      if (funcInfo.needsModule) args[arg++] = context.moduleContext;
      final ConvertImmediateArgRes res = convertImmediateArg(context, argument);
      if (res == null) return new ConvertImmediateArgRootRes(true, false, null);
      if (!res.isWhole) return new ConvertImmediateArgRootRes(false, false, null);
      args[arg] = res.whole;
    }
    return new ConvertImmediateArgRootRes(false, true, args);
  }

  @Override
  public boolean checkAssignableFrom(
      TSList<Error> errors, Location location, MortarDataType type, TSList<Object> path) {
    if (type instanceof MortarImmutableType) type = ((MortarImmutableType) type).innerType;
    if (type != this.type) {
      errors.add(new WrongType(location, path, type.toString(), toString()));
      return false;
    }
    return true;
  }

  @Override
  public EvaluateResult constCall(
      EvaluationContext context, Location location, Object inner, Value argument) {
    // Try to convert arg value to unwrapped args for immediate method call
    final Meta.FuncInfo funcInfo = ((StaticMethodMeta) inner).funcInfo;
    final ConvertImmediateArgRootRes res = convertImmediateRootArg(context, funcInfo, argument);
    if (res.isError) return EvaluateResult.error;

    // Either call it immediately or defer to 2nd pass
    if (res.isWhole) {
      try {
        return EvaluateResult.pure(
            funcInfo.returnType.constAsValue(funcInfo.method.invoke(null, res.args)));
      } catch (IllegalAccessException e) {
        throw new AssertionError(e);
      } catch (InvocationTargetException e) {
        final Throwable e2 = e.getTargetException();
        if (e2.getClass() != ContinueError.class) {
          context.moduleContext.errors.add(new Unexpected(location, e2));
        }
        return EvaluateResult.error;
      }
    } else {
      JVMSharedCode pre = new JVMSharedCode();
      JVMSharedCode code = new JVMSharedCode();
      JVMSharedCode post = new JVMSharedCode();
      convertFunctionArgumentRoot(context, location, pre, code, post, argument);
      code.add(
          JVMSharedCode.callStaticMethod(
              context.sourceLocation(location),
              JVMSharedJVMName.fromClass(funcInfo.method.getDeclaringClass()),
              funcInfo.method.getName(),
              funcInfo.descriptor));
      if (funcInfo.returnType == nullType)
        return new EvaluateResult(pre.add(code), post, nullValue);
      else return new EvaluateResult(pre, post, funcInfo.returnType.stackAsValue(code));
    }
  }

  @Override
  public JVMSharedDataDescriptor jvmDesc() {
    return DESC;
  }

  public static class ConvertImmediateArgRootRes {
    public final boolean isError;
    public final boolean isWhole;
    public final Object[] args;

    public ConvertImmediateArgRootRes(boolean isError, boolean isWhole, Object[] args) {
      this.isError = isError;
      this.isWhole = isWhole;
      this.args = args;
    }
  }

  public static class ConvertImmediateArgRes {
    private final boolean isWhole;
    private final Object whole;

    private ConvertImmediateArgRes(boolean isWhole, Object whole) {
      this.isWhole = isWhole;
      this.whole = whole;
    }

    public static ConvertImmediateArgRes resWhole(Object object) {
      return new ConvertImmediateArgRes(true, object);
    }

    public static ConvertImmediateArgRes resHalf() {
      return new ConvertImmediateArgRes(false, null);
    }
  }
}
