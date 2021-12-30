package com.zarbosoft.alligatoroid.compiler.inout.utils.classstate;

import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.Prototype;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.PrototypeInt;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.PrototypeString;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROMap;
import com.zarbosoft.rendaw.common.TSMap;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;

public class ClassInfo {
  // TODO move into derivation
  public final String luxemType;
  public Constructor constructor;
  public ROMap<String, Prototype> fields;
  public TSMap<String, Integer> argOrder;

  public ClassInfo(String luxemType) {
    this.luxemType = luxemType;
  }

  public void fill(Class klass) {
    Constructor constructor = klass.getConstructors()[0];
    TSMap<String, Prototype> fields = new TSMap<>();
    TSMap<String, Integer> argOrder = new TSMap<>();
    for (int i = 0; i < constructor.getParameters().length; i++) {
      Parameter parameter = constructor.getParameters()[i];
      argOrder.put(parameter.getName(), i);
      Prototype prototype;
      if (parameter.getType() == int.class) {
        prototype = PrototypeInt.instance;
      } else if (parameter.getType() == String.class) {
        prototype = PrototypeString.instance;
      } else throw new Assertion();
      fields.put(parameter.getName(), prototype);
    }
    this.constructor = constructor;
    this.argOrder = argOrder;
    this.fields = fields;
  }
}
