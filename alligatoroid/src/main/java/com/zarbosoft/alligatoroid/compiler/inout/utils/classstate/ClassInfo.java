package com.zarbosoft.alligatoroid.compiler.inout.utils.classstate;

import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.BaseStateRecordBody;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.BaseStateSingle;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.DefaultStateArrayBody;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.ProtoType;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.PrototypeInt;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.PrototypeString;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.StateArray;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.StateInt;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.StateRecord;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.StateString;
import com.zarbosoft.alligatoroid.compiler.inout.graph.AutoExporter;
import com.zarbosoft.alligatoroid.compiler.inout.utils.treeauto.TypeInfo;
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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

public class ClassInfo {
  // TODO move into derivation
  public final String luxemType;
  public Class klass;
  public ROMap<String, ProtoType> fields;
  public Object fallback;

  public ClassInfo(String luxemType) {
    this.luxemType = luxemType;
  }

  public void fill(Class klass) {
    TSMap<String, ProtoType> fields = new TSMap<>();
    for (Field field0 : klass.getFields()) {
      if (Modifier.isStatic(field0.getModifiers())) {
          continue;
      }
      if (field0.getAnnotation(AutoExporter.Param.class) == null) {
          continue;
      }
      final TypeInfo field = TypeInfo.fromField(field0);
      ProtoType prototype;
      if (field.klass == int.class) {
        prototype = PrototypeInt.instance;
      } else if (field.klass == String.class) {
        prototype = PrototypeString.instance;
      } else if (ROList.class.isAssignableFrom(field.klass)) {
        prototype =
            new ProtoType() {
              @Override
              public BaseStateSingle create(TSList<Error> errors, LuxemPathBuilder luxemPath) {
                return new StateArray(
                    new DefaultStateArrayBody() {
                      final TSList<BaseStateSingle> data = new TSList<>();

                      @Override
                      public BaseStateSingle createElementState(
                          Object context, TSList tsList, LuxemPathBuilder luxemPath) {
                        final BaseStateSingle state = createSingleState(field.genericArgs[0].klass);
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
      } else if (ROSetRef.class.isAssignableFrom(field.klass)) {
        prototype =
            new ProtoType() {
              @Override
              public BaseStateSingle create(TSList<Error> errors, LuxemPathBuilder luxemPath) {
                return new StateArray(
                    new DefaultStateArrayBody() {
                      final TSList<BaseStateSingle> data = new TSList<>();

                      @Override
                      public BaseStateSingle createElementState(
                          Object context, TSList tsList, LuxemPathBuilder luxemPath) {
                        final BaseStateSingle state = createSingleState(field.genericArgs[0].klass);
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
      } else if (ROMap.class.isAssignableFrom(field.klass)) {
        prototype =
            new ProtoType() {
              @Override
              public BaseStateSingle create(TSList<Error> errors, LuxemPathBuilder luxemPath) {
                return new StateRecord(
                    new BaseStateRecordBody() {
                      final TSList<ROPair<Object, BaseStateSingle>> data = new TSList<>();

                      @Override
                      public BaseStateSingle createKeyState(
                          Object context, TSList tsList, LuxemPathBuilder luxemPath) {
                          return createSingleState(field.genericArgs[0].klass);
                      }

                      @Override
                      public BaseStateSingle createValueState(
                          Object context, TSList tsList, LuxemPathBuilder luxemPath, Object key) {
                        final BaseStateSingle state = createSingleState(field.genericArgs[1].klass);
                        data.add(new ROPair<>(key, state));
                        return state;
                      }

                      @Override
                      public Object build(Object context, TSList tsList) {
                        TSMap out = new TSMap();
                        for (ROPair<Object, BaseStateSingle> e : data) {
                          out.put(e.first, e.second.build(context, errors));
                        }
                        return out;
                      }
                    });
              }
            };
      } else {
        prototype = new PrototypeAuto(field.klass);
      }
      fields.put(field0.getName(), prototype);
    }
    this.klass = klass;
    this.fields = fields;
  }

  private BaseStateSingle createSingleState(Type type) {
    if (type == int.class) {
      return new StateInt();
    } else if (type == String.class) {
      return new StateString();
    } else {
        throw new Assertion();
    }
  }
}
