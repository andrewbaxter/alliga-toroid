package com.zarbosoft.alligatoroid.compiler.inout.utils.languageinout;

import com.zarbosoft.alligatoroid.compiler.inout.utils.classstate.ClassInfo;
import com.zarbosoft.alligatoroid.compiler.inout.utils.classstate.StateClassBody;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.BaseStateRecordBody;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.BaseStateSingle;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.DefaultStateSingle;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.Deserializer;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.Prototype;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.PrototypeArray;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.PrototypeBool;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.PrototypeInt;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.PrototypeString;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.State;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.StateErrorSingle;
import com.zarbosoft.alligatoroid.compiler.model.error.DeserializeUnknownLanguageVersion;
import com.zarbosoft.alligatoroid.compiler.model.error.DeserializeUnknownType;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.model.ids.ModuleId;
import com.zarbosoft.alligatoroid.compiler.mortar.value.LanguageElement;
import com.zarbosoft.alligatoroid.compiler.mortar.value.Value;
import com.zarbosoft.luxem.read.path.LuxemPathBuilder;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static com.zarbosoft.alligatoroid.compiler.Meta.LANGUAGE;
import static com.zarbosoft.alligatoroid.compiler.Meta.toUnderscore;
import static com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.Deserializer.errorRet;

public class LanguageDeserializer {
  private static final Prototype languagePrototype;

  static {
    TSMap<String, ClassInfo> languageNodeInfos = new TSMap<>();
    languagePrototype =
        new Prototype() {
          @Override
          public BaseStateSingle create(TSList<Error> errors, LuxemPathBuilder luxemPath) {
            return new DefaultStateSingle<ModuleId, LanguageElement>() {
              private StateClassBody<ModuleId, LanguageElement> child;

              @Override
              public LanguageElement build(ModuleId moduleId, TSList<Error> errors) {
                if (child == null) return null;
                return child.build(moduleId, errors);
              }

              @Override
              protected BaseStateSingle<ModuleId, LanguageElement> innerEatType(
                  ModuleId moduleId,
                  TSList<Error> errors,
                  LuxemPathBuilder luxemPath,
                  String name) {
                ClassInfo info = languageNodeInfos.getOpt(name);
                if (info == null) {
                  errors.add(
                      new DeserializeUnknownType(
                          luxemPath.render(), name, languageNodeInfos.keys().toList()));
                  return StateErrorSingle.state;
                }

                return new DefaultStateSingle<ModuleId, LanguageElement>() {
                  @Override
                  public LanguageElement build(ModuleId moduleId, TSList<Error> errors) {
                    return null;
                  }

                  @Override
                  protected BaseStateRecordBody<ModuleId, LanguageElement> innerEatRecordBegin(
                      ModuleId moduleId, TSList<Error> errors, LuxemPathBuilder luxemPath) {
                    child = new StateClassBody<ModuleId, LanguageElement>(luxemPath, info);
                    return child;
                  }
                };
              }
            };
          }
        };

    PrototypeLocation locationPrototype = new PrototypeLocation();
    for (Class klass : LANGUAGE) {
      // String type = toUnderscore(klass.getSimpleName());
      String type = toUnderscore(klass);
      languageNodeInfos.put(type, new ClassInfo(type));
    }
    for (Class klass : LANGUAGE) {
      Constructor constructor = klass.getConstructors()[0];
      TSMap<String, Prototype> fields = new TSMap<>();
      TSMap<String, Integer> argOrder = new TSMap<>();
      for (int i = 0; i < constructor.getParameters().length; i++) {
        Parameter parameter = constructor.getParameters()[i];
        argOrder.put(parameter.getName(), i);
        Prototype prototype;
        if (parameter.getType() == Location.class) {
          prototype = locationPrototype;
        } else if (parameter.getType() == LanguageElement.class) {
          prototype = languagePrototype;
        } else if (parameter.getType() == int.class) {
          prototype = PrototypeInt.instance;
        } else if (parameter.getType() == String.class) {
          prototype = PrototypeString.instance;
        } else if (parameter.getType() == boolean.class) {
          prototype = PrototypeBool.instance;
        } else if (parameter.getType() == ROList.class) {
          Type paramType =
              ((ParameterizedType) parameter.getParameterizedType()).getActualTypeArguments()[0];
          if (paramType == Value.class) {
            prototype = new PrototypeArray(languagePrototype);
          } else throw new Assertion();
        } else throw new Assertion();
        fields.put(parameter.getName(), prototype);
      }
      String type = toUnderscore(klass);
      ClassInfo prototype = languageNodeInfos.get(type);
      prototype.constructor = constructor;
      prototype.argOrder = argOrder;
      prototype.fields = fields;
    }
  }

  public static LanguageElement deserialize(
      ModuleId moduleId, TSList<Error> errors, String path, InputStream source) {
    TSList<State> stack = new TSList<>();
    State[] rootNodes = new State[1];
    stack.add(
        new DefaultStateSingle<ModuleId, Object>() {
          @Override
          public Object build(ModuleId context, TSList<Error> errors) {
            return null;
          }

          @Override
          protected BaseStateSingle innerEatType(
              ModuleId module, TSList<Error> errors, LuxemPathBuilder luxemPath, String name) {
            String expected = "alligatoroid:0.0.1";
            if (!expected.equals(name)) {
              errors.add(new DeserializeUnknownLanguageVersion(luxemPath.render(), expected));
              return StateErrorSingle.state;
            }
            BaseStateSingle out = languagePrototype.create(errors, luxemPath);
            rootNodes[0] = out;
            return out;
          }
        });
    Deserializer.deserialize(moduleId, errors, path, source, stack);
    Object out = rootNodes[0].build(moduleId, errors);
    if (out == errorRet) {
      return null;
    }
    return (LanguageElement) out;
  }
}
