package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.cache.GraphSerializable;
import com.zarbosoft.alligatoroid.compiler.mortar.Record;
import com.zarbosoft.alligatoroid.compiler.mortar.Tuple;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROMap;
import com.zarbosoft.rendaw.common.TSMap;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;

import static com.zarbosoft.rendaw.common.Common.uncheck;

public class AutoBuiltinMeta implements BuiltinMeta {
  private final Class<? extends GraphSerializable> klass;

  public AutoBuiltinMeta(Class<? extends GraphSerializable> klass) {
    this.klass = klass;
  }

  @Override
  public Class getKlass() {
    return klass;
  }

  @Override
  public GraphSerializable graphDeserialize(Record data) {
    Constructor constructor = klass.getConstructors()[0];
    Object[] args = new Object[constructor.getParameterCount()];
    for (int i = 0; i < constructor.getParameters().length; i++) {
      Parameter param = constructor.getParameters()[i];
      args[i] = data.data.get(param.getName());
      if (args[i] instanceof Tuple) {
        args[i] = ((Tuple) args[i]).data;
      }
    }
    return (GraphSerializable) uncheck(() -> constructor.newInstance(args));
  }

  public Record autoGraphSerialize(Object self) {
    Constructor constructor = klass.getConstructors()[0];
    TSMap<Object, Object> preRecord = new TSMap<>();
    for (int i = 0; i < constructor.getParameters().length; i++) {
      Parameter param = constructor.getParameters()[i];
      Object data = uncheck(() -> klass.getField(param.getName()).get(self));
      if (data instanceof ROMap) {
        data = new Record((ROMap<Object, Object>) data);
      } else if (data instanceof ROList) {
        data = new Tuple((ROList<Object>) data);
      } else if (data instanceof String) {
        // nop
      } else if (data instanceof Integer) {
        // nop
      } else if (data instanceof GraphSerializable) {
        // nop
      } else throw new Assertion();
      preRecord.put(param.getName(), data);
    }
    return new Record(preRecord);
  }
}
