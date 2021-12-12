package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.cache.GraphSerializable;
import com.zarbosoft.alligatoroid.compiler.mortar.Record;
import com.zarbosoft.alligatoroid.compiler.mortar.SimpleValue;
import com.zarbosoft.alligatoroid.compiler.mortar.Tuple;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSMap;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;

import static com.zarbosoft.rendaw.common.Common.uncheck;

public abstract class LanguageValue implements SimpleValue, GraphSerializable {
  public final Location location;
  public final boolean hasLowerInSubtree;

  public LanguageValue(Location id, boolean hasLowerInSubtree) {
    this.location = id;
    this.hasLowerInSubtree = hasLowerInSubtree;
  }

  protected static boolean hasLowerInSubtree(ROList<Value> values) {
    boolean out = false;
    for (Value value : values) {
      if (value instanceof LanguageValue) out = out || ((LanguageValue) value).hasLowerInSubtree;
    }
    return out;
  }

  protected static boolean hasLowerInSubtree(Value... values) {
    boolean out = false;
    for (Value value : values) {
      if (value instanceof LanguageValue) out = out || ((LanguageValue) value).hasLowerInSubtree;
    }
    return out;
  }

  @Override
  public abstract EvaluateResult evaluate(Context context);

  @Override
  public Location location() {
    return location;
  }

  @Override
  public Record graphSerialize() {
    TSMap<Object, Object> data = new TSMap<>();
    for (Parameter parameter : getClass().getConstructors()[0].getParameters()) {
      data.putNew(
          parameter.getName(),
          uncheck(
              () -> {
                String fieldName = parameter.getName();
                if ("id".equals(fieldName)) fieldName = "location";
                Object fieldData = this.getClass().getField(fieldName).get(this);
                if (fieldData instanceof ROList) {
                  fieldData = new Tuple((ROList<Object>) fieldData);
                }
                return fieldData;
              }));
    }
    return new Record(data);
  }
}
