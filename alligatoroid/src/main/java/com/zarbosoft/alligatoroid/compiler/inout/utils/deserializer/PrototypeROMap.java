package com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer;

import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.luxem.read.path.LuxemPathBuilder;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;

public class PrototypeROMap implements ProtoType {
  private final ProtoType key;
  private final ProtoType value;

  public PrototypeROMap(ProtoType key, ProtoType value) {
    this.key = key;
    this.value = value;
  }

  @Override
  public BaseStateSingle create(TSList<Error> errors, LuxemPathBuilder luxemPath) {
    return new StateRecord(
        new BaseStateRecordBody() {
          final TSList<ROPair<Object, State>> elements = new TSList<>();

          @Override
          public BaseStateSingle createKeyState(
              Object context, TSList tsList, LuxemPathBuilder luxemPath) {
            return key.create(errors, luxemPath);
          }

          @Override
          public BaseStateSingle createValueState(
              Object context, TSList tsList, LuxemPathBuilder luxemPath, Object key) {
            final BaseStateSingle valueState = value.create(errors, luxemPath);
            elements.add(new ROPair<>(key, valueState));
            return valueState;
          }

          @Override
          public Object build(Object context, TSList tsList) {
            TSMap out = new TSMap();
            boolean bad = false;
            for (ROPair<Object, State> element : elements) {
              final Object value = element.second.build(context, tsList);
              if (value == null) {
                  bad = true;
              }
              out.put(element.first, value);
            }
            if (bad) {
                return null;
            }
            return out;
          }
        });
  }
}
