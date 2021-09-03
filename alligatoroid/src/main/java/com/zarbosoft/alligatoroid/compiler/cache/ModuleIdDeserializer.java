package com.zarbosoft.alligatoroid.compiler.cache;

import com.zarbosoft.alligatoroid.compiler.Error;
import com.zarbosoft.alligatoroid.compiler.LocalModuleId;
import com.zarbosoft.alligatoroid.compiler.ModuleId;
import com.zarbosoft.alligatoroid.compiler.deserialize.BaseState;
import com.zarbosoft.alligatoroid.compiler.deserialize.Deserializer;
import com.zarbosoft.alligatoroid.compiler.deserialize.ObjectInfo;
import com.zarbosoft.alligatoroid.compiler.deserialize.State;
import com.zarbosoft.alligatoroid.compiler.deserialize.StateErrorSingle;
import com.zarbosoft.alligatoroid.compiler.deserialize.StateObject;
import com.zarbosoft.alligatoroid.compiler.deserialize.StatePrototype;
import com.zarbosoft.alligatoroid.compiler.deserialize.StatePrototypeInt;
import com.zarbosoft.alligatoroid.compiler.deserialize.StatePrototypeString;
import com.zarbosoft.alligatoroid.compiler.deserialize.StateRecordBegin;
import com.zarbosoft.luxem.read.path.LuxemPath;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.zarbosoft.alligatoroid.compiler.deserialize.Deserializer.errorRet;

public class ModuleIdDeserializer {
  private static final StatePrototype valuePrototype;
  private static final TSMap<String, ObjectInfo> typeInfos = new TSMap<>();

  static {
    final ROPair<String, Class>[] idTypes =
        new ROPair[] {
          new ROPair<>("local",LocalModuleId.class),
        };
    valuePrototype =
        new StatePrototype() {
          @Override
          public State create(TSList<Error> errors, LuxemPath luxemPath, TSList<State> stack) {
            BaseState out =
                new BaseState() {
                  State child;

                  @Override
                  public void eatType(
                      TSList<Error> errors, TSList<State> stack, LuxemPath luxemPath, String name) {
                    stack.removeLast();
                    ObjectInfo info = typeInfos.getOpt(name);
                    if (info == null) {
                      errors.add(
                          Error.deserializeUnknownType(luxemPath, name, typeInfos.keys().toList()));
                      stack.add(StateErrorSingle.state);
                      return;
                    }
                    child = new StateObject(info);
                    stack.add(child);
                    stack.add(StateRecordBegin.state);
                  }

                  @Override
                  public Object build(TSList<Error> errors) {
                    if (child == null) return null;
                    return child.build(errors);
                  }
                };
            stack.add(out);
            return out;
          }
        };

    for (ROPair<String, Class> pair : idTypes) {
      typeInfos.put(pair.first, new ObjectInfo(pair.first));
    }
    for (ROPair<String, Class> pair : idTypes) {
      Constructor constructor = pair.second.getConstructors()[0];
      TSMap<String, StatePrototype> fields = new TSMap<>();
      TSMap<String, Integer> argOrder = new TSMap<>();
      for (int i = 0; i < constructor.getParameters().length; i++) {
        Parameter parameter = constructor.getParameters()[i];
        argOrder.put(parameter.getName(), i);
        StatePrototype prototype;
        if (parameter.getType() == int.class) {
          prototype = StatePrototypeInt.instance;
        } else if (parameter.getType() == String.class) {
          prototype = StatePrototypeString.instance;
        } else throw new Assertion();
        fields.put(parameter.getName(), prototype);
      }
      String type = pair.first;
      ObjectInfo prototype = typeInfos.get(type);
      prototype.constructor = constructor;
      prototype.argOrder = argOrder;
      prototype.fields = fields;
    }
  }

  /**
   * Returns null if there's an external issue
   *
   * @param errors
   * @param path
   * @return
   */
  public static ModuleId deserialize(TSList<Error> errors, Path path) {
    if (!Files.exists(path)) return null;
    TSList<State> stack = new TSList<>();
    State state = valuePrototype.create(errors, null, stack);
    Deserializer.deserialize(errors, path, stack);
    Object out = state.build(errors);
    if (out == errorRet) return null;
    return (ModuleId) out;
  }
}
