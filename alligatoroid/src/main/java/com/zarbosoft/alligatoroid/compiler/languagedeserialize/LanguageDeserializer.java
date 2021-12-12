package com.zarbosoft.alligatoroid.compiler.languagedeserialize;

import com.zarbosoft.alligatoroid.compiler.AutoBuiltinMeta;
import com.zarbosoft.alligatoroid.compiler.BuiltinMeta;
import com.zarbosoft.alligatoroid.compiler.Error;
import com.zarbosoft.alligatoroid.compiler.Location;
import com.zarbosoft.alligatoroid.compiler.ModuleId;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.deserialize.BaseStateRecord;
import com.zarbosoft.alligatoroid.compiler.deserialize.BaseStateSingle;
import com.zarbosoft.alligatoroid.compiler.deserialize.DefaultStateSingle;
import com.zarbosoft.alligatoroid.compiler.deserialize.Deserializer;
import com.zarbosoft.alligatoroid.compiler.deserialize.LocationPrototype;
import com.zarbosoft.alligatoroid.compiler.deserialize.ObjectInfo;
import com.zarbosoft.alligatoroid.compiler.deserialize.State;
import com.zarbosoft.alligatoroid.compiler.deserialize.StateErrorSingle;
import com.zarbosoft.alligatoroid.compiler.deserialize.StateObject;
import com.zarbosoft.alligatoroid.compiler.deserialize.StatePrototype;
import com.zarbosoft.alligatoroid.compiler.deserialize.StatePrototypeArray;
import com.zarbosoft.alligatoroid.compiler.deserialize.StatePrototypeBool;
import com.zarbosoft.alligatoroid.compiler.deserialize.StatePrototypeInt;
import com.zarbosoft.alligatoroid.compiler.deserialize.StatePrototypeString;
import com.zarbosoft.alligatoroid.compiler.language.Access;
import com.zarbosoft.alligatoroid.compiler.language.Bind;
import com.zarbosoft.alligatoroid.compiler.language.Block;
import com.zarbosoft.alligatoroid.compiler.language.Builtin;
import com.zarbosoft.alligatoroid.compiler.language.Call;
import com.zarbosoft.alligatoroid.compiler.language.Import;
import com.zarbosoft.alligatoroid.compiler.language.LiteralBool;
import com.zarbosoft.alligatoroid.compiler.language.LiteralString;
import com.zarbosoft.alligatoroid.compiler.language.Local;
import com.zarbosoft.alligatoroid.compiler.language.Lower;
import com.zarbosoft.alligatoroid.compiler.language.ModLocal;
import com.zarbosoft.alligatoroid.compiler.language.ModRemote;
import com.zarbosoft.alligatoroid.compiler.language.Record;
import com.zarbosoft.alligatoroid.compiler.language.RecordElement;
import com.zarbosoft.alligatoroid.compiler.language.Stage;
import com.zarbosoft.alligatoroid.compiler.language.Tuple;
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

import static com.zarbosoft.alligatoroid.compiler.deserialize.Deserializer.errorRet;

public class LanguageDeserializer {
  public static final BuiltinMeta[] LANGUAGE =
      new BuiltinMeta[] {
        new AutoBuiltinMeta(Access.class),
        new AutoBuiltinMeta(Bind.class),
        new AutoBuiltinMeta(Block.class),
        new AutoBuiltinMeta(Builtin.class),
        new AutoBuiltinMeta(Call.class),
        new AutoBuiltinMeta(LiteralString.class),
        new AutoBuiltinMeta(LiteralBool.class),
        new AutoBuiltinMeta(Local.class),
        new AutoBuiltinMeta(Record.class),
        new AutoBuiltinMeta(RecordElement.class),
        new AutoBuiltinMeta(Tuple.class),
        new AutoBuiltinMeta(Stage.class),
        new AutoBuiltinMeta(Lower.class),
        new AutoBuiltinMeta(Import.class),
        new AutoBuiltinMeta(ModLocal.class),
        new AutoBuiltinMeta(ModRemote.class)
      };
  private final StatePrototype valuePrototype;
  private final TSMap<String, ObjectInfo> languageNodeInfos = new TSMap<>();

  public LanguageDeserializer(ModuleId module) {
    valuePrototype =
        new StatePrototype() {
          @Override
          public BaseStateSingle create(TSList<Error> errors, LuxemPathBuilder luxemPath) {
            return new DefaultStateSingle() {
              private StateObject child;

              @Override
              public Object build(TSList<Error> errors) {
                if (child == null) return null;
                return child.build(errors);
              }

              @Override
              protected BaseStateSingle innerEatType(
                  TSList<Error> errors, LuxemPathBuilder luxemPath, String name) {
                ObjectInfo info = languageNodeInfos.getOpt(name);
                if (info == null) {
                  errors.add(
                      new Error.DeserializeUnknownType(
                          luxemPath.render(), name, languageNodeInfos.keys().toList()));
                  return StateErrorSingle.state;
                }
                return new DefaultStateSingle() {
                  @Override
                  protected BaseStateRecord innerEatRecordBegin(
                      TSList<Error> errors, LuxemPathBuilder luxemPath) {
                    child = new StateObject(luxemPath, info);
                    return child;
                  }
                };
              }
            };
          }
        };

    for (BuiltinMeta klass : LANGUAGE) {
      // String type = toUnderscore(klass.getSimpleName());
      String type = toUnderscore(klass.getKlass().getSimpleName());
      languageNodeInfos.put(type, new ObjectInfo(type));
    }
    for (BuiltinMeta klass : LANGUAGE) {
      Constructor constructor = klass.getKlass().getConstructors()[0];
      TSMap<String, StatePrototype> fields = new TSMap<>();
      TSMap<String, Integer> argOrder = new TSMap<>();
      for (int i = 0; i < constructor.getParameters().length; i++) {
        Parameter parameter = constructor.getParameters()[i];
        argOrder.put(parameter.getName(), i);
        StatePrototype prototype;
        if (parameter.getType() == Location.class) {
          prototype = new LocationPrototype(module);
        } else if (parameter.getType() == Value.class) {
          prototype = valuePrototype;
        } else if (parameter.getType() == int.class) {
          prototype = StatePrototypeInt.instance;
        } else if (parameter.getType() == String.class) {
          prototype = StatePrototypeString.instance;
        } else if (parameter.getType() == boolean.class) {
          prototype = StatePrototypeBool.instance;
        } else if (parameter.getType() == ROList.class) {
          Type paramType =
              ((ParameterizedType) parameter.getParameterizedType()).getActualTypeArguments()[0];
          if (paramType == Value.class) {
            prototype = new StatePrototypeArray(valuePrototype);
          } else throw new Assertion();
        } else throw new Assertion();
        fields.put(parameter.getName(), prototype);
      }
      String type = toUnderscore(klass.getKlass().getSimpleName());
      ObjectInfo prototype = languageNodeInfos.get(type);
      prototype.constructor = constructor;
      prototype.argOrder = argOrder;
      prototype.fields = fields;
    }
  }

  private static String toUnderscore(String name) {
    StringBuilder out = new StringBuilder();
    for (int i = 0; i < name.length(); ++i) {
      if (Character.isUpperCase(name.codePointAt(i))) {
        if (i > 0) {
          out.append('_');
        }
        out.appendCodePoint(Character.toLowerCase(name.codePointAt(i)));
      } else {
        out.appendCodePoint(name.codePointAt(i));
      }
    }
    return out.toString();
  }

  public ROList<Value> deserialize(TSList<Error> errors, String path, InputStream source) {
    TSList<State> stack = new TSList<>();
    State[] rootNodes = new State[1];
    stack.add(
        new DefaultStateSingle() {
          @Override
          protected BaseStateSingle innerEatType(
              TSList<Error> errors, LuxemPathBuilder luxemPath, String name) {
            String expected = "alligatoroid:0.0.1";
            if (!expected.equals(name)) {
              errors.add(new Error.DeserializeUnknownLanguageVersion(luxemPath.render(), expected));
              return StateErrorSingle.state;
            }
            BaseStateSingle out = new StatePrototypeArray(valuePrototype).create(errors, luxemPath);
            rootNodes[0] = out;
            return out;
          }
        });
    Deserializer.deserialize(errors, path, source, stack);
    Object out = rootNodes[0].build(errors);
    if (out == errorRet) {
      return null;
    }
    return (ROList<Value>) out;
  }
}
