package com.zarbosoft.alligatoroid.compiler.inout.utils.treeauto;

import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.BaseStateSingle;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.DefaultStateSingle;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.StateErrorSingle;
import com.zarbosoft.alligatoroid.compiler.model.error.DeserializeUnknownType;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.luxem.read.path.LuxemPathBuilder;
import com.zarbosoft.luxem.write.Writer;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;

class AutoInfoEnum implements AutoInfo {
  private final AutoTreeMeta autoTreeMeta;
  private final Object fallback;
  private final TSMap<Class, String> classToKey;
  private final TSMap<String, Class> keyToClass;

  public AutoInfoEnum(
      AutoTreeMeta autoTreeMeta,
      TSMap<String, Class> keyToClass,
      TSMap<Class, String> classToKey,
      Object fallback) {
    this.autoTreeMeta = autoTreeMeta;
    this.classToKey = classToKey;
    this.keyToClass = keyToClass;
    this.fallback = fallback;
  }

  @Override
  public BaseStateSingle create(TSList<Error> errors, LuxemPathBuilder luxemPath) {
    return new DefaultStateSingle() {
      private BaseStateSingle state;

      @Override
      protected BaseStateSingle innerEatType(
          Object context, TSList tsList, LuxemPathBuilder luxemPath, String name) {
        final Class klass = keyToClass.getOpt(name);
        if (klass == null) {
          final TSList<String> known = new TSList<>();
          for (String key : classToKey.values()) {
            known.add(key);
          }
          errors.add(new DeserializeUnknownType(luxemPath.render(), name, known));
          return StateErrorSingle.state;
        }
        state = autoTreeMeta.infos.get(klass).create(errors, luxemPath);
        return state;
      }

      @Override
      public Object build(Object context, TSList tsList) {
        if (state == null) return fallback;
        return state.build(context, tsList);
      }
    };
  }

  @Override
  public void write(Writer writer, Object object) {
    writer.type(classToKey.get(object.getClass()));
    autoTreeMeta.serialize(writer, TypeInfo.fromClass(object.getClass()), object);
  }
}
