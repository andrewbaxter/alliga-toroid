package com.zarbosoft.alligatoroid.compiler.inout.utils.classstate;

import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.BaseStateRecordBody;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.BaseStateSingle;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.DefaultStateArrayBody;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.Prototype;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.PrototypeInt;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.PrototypeString;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.StateArray;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.StateInt;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.StateRecord;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.StateString;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.luxem.read.path.LuxemPathBuilder;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROMap;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.ROSetRef;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;
import com.zarbosoft.rendaw.common.TSSet;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

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
      } else if (ROList.class.isAssignableFrom(parameter.getType())) {
        prototype =
            new Prototype() {
              @Override
              public BaseStateSingle create(TSList<Error> errors, LuxemPathBuilder luxemPath) {
                return new StateArray(
                    new DefaultStateArrayBody() {
                      final TSList<BaseStateSingle> data = new TSList<>();

                      @Override
                      public BaseStateSingle createElementState(
                          Object context, TSList tsList, LuxemPathBuilder luxemPath) {
                        final BaseStateSingle state =
                            createSingleState(
                                ((ParameterizedType) parameter.getParameterizedType())
                                    .getActualTypeArguments()[0]);
                        data.add(state);
                        return state;
                      }

                      @Override
                      public Object build(Object context, TSList tsList) {
                        TSList out = new TSList();
                        for (BaseStateSingle e : data) {
                          out.add(e.build(context, errors));
                        }
                        return out;
                      }
                    });
              }
            };
      } else if (ROSetRef.class.isAssignableFrom(parameter.getType())) {
        prototype =
            new Prototype() {
              @Override
              public BaseStateSingle create(TSList<Error> errors, LuxemPathBuilder luxemPath) {
                return new StateArray(
                    new DefaultStateArrayBody() {
                      final TSList<BaseStateSingle> data = new TSList<>();

                      @Override
                      public BaseStateSingle createElementState(
                          Object context, TSList tsList, LuxemPathBuilder luxemPath) {
                        final BaseStateSingle state =
                            createSingleState(
                                ((ParameterizedType) parameter.getParameterizedType())
                                    .getActualTypeArguments()[0]);
                        data.add(state);
                        return state;
                      }

                      @Override
                      public Object build(Object context, TSList tsList) {
                        TSSet out = new TSSet();
                        for (BaseStateSingle e : data) {
                          out.add(e.build(context, errors));
                        }
                        return out;
                      }
                    });
              }
            };
      } else if (ROMap.class.isAssignableFrom(parameter.getType())) {
        prototype =
            new Prototype() {
              @Override
              public BaseStateSingle create(TSList<Error> errors, LuxemPathBuilder luxemPath) {
                return new StateRecord(
                    new BaseStateRecordBody() {
                      final TSList<ROPair<String, BaseStateSingle>> data = new TSList<>();

                      @Override
                      public BaseStateSingle createKeyState(
                          Object context, TSList tsList, LuxemPathBuilder luxemPath) {
                        return new StateString();
                      }

                      @Override
                      public BaseStateSingle createValueState(
                          Object context, TSList tsList, LuxemPathBuilder luxemPath, Object key) {
                        final BaseStateSingle state =
                            createSingleState(
                                ((ParameterizedType) parameter.getParameterizedType())
                                    .getActualTypeArguments()[1]);
                        data.add(new ROPair<>((String) key, state));
                        return state;
                      }

                      @Override
                      public Object build(Object context, TSList tsList) {
                        TSMap out = new TSMap();
                        for (ROPair<String, BaseStateSingle> e : data) {
                          out.put(e.first, e.second.build(context, errors));
                        }
                        return out;
                      }
                    });
              }
            };
      } else throw new Assertion();
      fields.put(parameter.getName(), prototype);
    }
    this.constructor = constructor;
    this.argOrder = argOrder;
    this.fields = fields;
  }

  private BaseStateSingle createSingleState(Type type) {
    if (type == int.class) {
      return new StateInt();
    } else if (type == String.class) {
      return new StateString();
    } else throw new Assertion();
  }
}
