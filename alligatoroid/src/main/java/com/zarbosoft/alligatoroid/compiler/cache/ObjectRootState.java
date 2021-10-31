package com.zarbosoft.alligatoroid.compiler.cache;

import com.zarbosoft.alligatoroid.compiler.Error;
import com.zarbosoft.alligatoroid.compiler.ImportSpec;
import com.zarbosoft.alligatoroid.compiler.deserialize.BaseStateArray;
import com.zarbosoft.alligatoroid.compiler.deserialize.BaseStateSingle;
import com.zarbosoft.alligatoroid.compiler.deserialize.DefaultStatePrimitive;
import com.zarbosoft.alligatoroid.compiler.deserialize.DefaultStateSingle;
import com.zarbosoft.alligatoroid.compiler.deserialize.State;
import com.zarbosoft.alligatoroid.compiler.deserialize.StateErrorSingle;
import com.zarbosoft.luxem.read.path.LuxemPathBuilder;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSList;

import java.nio.file.Paths;

public class ObjectRootState extends DefaultStateSingle {
  private final Cache cache;
  private State inner;

  public ObjectRootState(Cache cache) {
    this.cache = cache;
  }

  @Override
  protected BaseStateSingle innerEatType(TSList<Error> errors, LuxemPathBuilder luxemPath, String name) {
    switch (name) {
      case Cache.CACHE_OBJECT_TYPE_IMPORT_SPEC:
        return new DefaultStatePrimitive() {
          ImportSpec out;

          @Override
          public Object build(TSList<Error> errors) {
            return out;
          }

          @Override
          protected void innerEatPrimitiveUntyped(
                  TSList<Error> errors, LuxemPathBuilder luxemPath, String value) {
            out = ImportSpecDeserializer.deserialize(errors, Paths.get(value));
          }
        };
      case Cache.CACHE_OBJECT_TYPE_OUTPUT:
        return new DefaultStateSingle() {
          @Override
          protected BaseStateArray innerArrayBegin(TSList<Error> errors, LuxemPathBuilder luxemPath) {
            OutputTypeState out = new OutputTypeState(cache);
            inner = out;
            return out;
          }
        };
      case Cache.CACHE_OBJECT_TYPE_BUILTIN:
        return new DefaultStateSingle() {
          @Override
          protected BaseStateArray innerArrayBegin(TSList<Error> errors, LuxemPathBuilder luxemPath) {
            BuiltinTypeState out = new BuiltinTypeState(cache);
            inner = out;
            return out;
          }
        };
      default:
        {
          errors.add(new Error.DeserializeUnknownType(luxemPath.render(), name, ROList.empty));
          return StateErrorSingle.state;
        }
    }
  }

  @Override
  public Object build(TSList<Error> errors) {
    return inner.build(errors);
  }
}
