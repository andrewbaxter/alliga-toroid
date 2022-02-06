package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Meta;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedJVMName;
import com.zarbosoft.alligatoroid.compiler.model.error.Unexpected;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.Record;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.Tuple;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROOrderedMap;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;

import java.lang.reflect.InvocationTargetException;

import static com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetModuleContext.convertFunctionArgument;
import static com.zarbosoft.alligatoroid.compiler.mortar.value.ConstDataStackValue.nullValue;

public class StaticMethodValue implements SimpleValue {
  private final Meta.FuncInfo funcInfo;

  public StaticMethodValue(Meta.FuncInfo funcInfo) {
    this.funcInfo = funcInfo;
  }

  public static ConvertImmediateArgRes convertImmediateArg(Value value) {
    if (value instanceof LooseTuple) {
      TSList<Object> out = new TSList<>();
      final ROList<EvaluateResult> tupleValue = ((LooseTuple) value).data;
      for (EvaluateResult val : tupleValue) {
        if (val.preEffect != null || val.postEffect != null) {
          return ConvertImmediateArgRes.resHalf();
        }
        final ConvertImmediateArgRes res = convertImmediateArg(val.value);
        if (!res.isWhole) return res;
        out.add(res.whole);
      }
      return ConvertImmediateArgRes.resWhole(new Tuple(out));
    } else if (value instanceof LooseRecord) {
      final ROOrderedMap<Object, EvaluateResult> recordValue = ((LooseRecord) value).data;
      TSMap<Object, Object> out = new TSMap<>();
      for (ROPair<Object, EvaluateResult> e : recordValue) {
        if (e.second.preEffect != null || e.second.postEffect != null) {
          return ConvertImmediateArgRes.resHalf();
        }
        final ConvertImmediateArgRes res = convertImmediateArg(e.second.value);
        if (!res.isWhole) return res;
        out.put(e.first, res.whole);
      }
      return ConvertImmediateArgRes.resWhole(new Record(out));
    } else if (value instanceof DataValue) {
      if (value instanceof ConstDataValue)
        return ConvertImmediateArgRes.resWhole(((ConstDataValue) value).getInner());
      else return ConvertImmediateArgRes.resHalf();
    } else throw new Assertion();
  }

  // Returns null if not immediate (half)
  public static Object[] convertImmediateRootArg(
      EvaluationContext context, Meta.FuncInfo funcInfo, Object self, Value argument) {
    Object[] args;
    if (argument instanceof LooseTuple) {
      final ROList<EvaluateResult> tupleArg = ((LooseTuple) argument).data;
      args = new Object[(self != null ? 1 : 0) + (funcInfo.needsModule ? 1 : 0) + tupleArg.size()];
      int arg = 0;
      if (self != null) args[arg++] = self;
      if (funcInfo.needsModule) args[arg++] = context.moduleContext;
      for (int i = 0; i < tupleArg.size(); i++) {
        final EvaluateResult val = tupleArg.get(i);
        if (val.preEffect != null | val.postEffect != null) {
          return null;
        }
        final ConvertImmediateArgRes res = convertImmediateArg(val.value);
        if (!res.isWhole) {
          return null;
        }
        args[arg++] = res.whole;
      }
    } else {
      args = new Object[(self != null ? 1 : 0) + (funcInfo.needsModule ? 1 : 0) + 1];
      int arg = 0;
      if (self != null) args[arg++] = self;
      if (funcInfo.needsModule) args[arg++] = context.moduleContext;
      final ConvertImmediateArgRes res = convertImmediateArg(argument);
      if (!res.isWhole) {
        return null;
      }
      args[arg] = res.whole;
    }
    return args;
  }

  @Override
  public EvaluateResult call(EvaluationContext context, Location location, Value argument) {
    // Try to convert arg value to unwrapped args for immediate method call
    final Object[] args = convertImmediateRootArg(context, funcInfo, null, argument);

    // Either call it immediately or defer to 2nd pass
    if (args != null) {
      try {
        return EvaluateResult.pure(funcInfo.returnType.constAsValue(funcInfo.method.invoke(args)));
      } catch (IllegalAccessException e) {
        throw new AssertionError(e);
      } catch (InvocationTargetException e) {
        context.moduleContext.errors.add(new Unexpected(location, e.getTargetException()));
        return EvaluateResult.error;
      }
    } else {
      JVMSharedCode pre = new JVMSharedCode();
      JVMSharedCode code = new JVMSharedCode();
      JVMSharedCode post = new JVMSharedCode();
      convertFunctionArgument(context, location, pre, code, post, argument);
      code.add(
          JVMSharedCode.callStaticMethod(
              context.sourceLocation(location),
              JVMSharedJVMName.fromClass(funcInfo.method.getDeclaringClass()),
              funcInfo.method.getName(),
              funcInfo.descriptor));
      if (funcInfo.returnType == null) return new EvaluateResult(pre.add(code), post, nullValue);
      else return new EvaluateResult(pre, post, funcInfo.returnType.stackAsValue(code));
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
