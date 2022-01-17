package com.zarbosoft.alligatoroid.compiler.inout.utils.treeauto;

import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.BaseStateSingle;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.DefaultStateSingle;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.luxem.read.path.LuxemPathBuilder;
import com.zarbosoft.luxem.write.Writer;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;

class AutoInfoEnum implements AutoInfo {
  private final AutoTreeMeta autoTreeMeta;
  private final TSMap<Class, String> classToKey;
  private final TSMap<String, Class> keyToClass;

  public AutoInfoEnum(
      AutoTreeMeta autoTreeMeta, TSMap<String, Class> keyToClass, TSMap<Class, String> classToKey) {
    this.autoTreeMeta = autoTreeMeta;
    this.classToKey = classToKey;
    this.keyToClass = keyToClass;
  }

  @Override
  public BaseStateSingle create(TSList<Error> errors, LuxemPathBuilder luxemPath) {
    return new DefaultStateSingle() {
      private BaseStateSingle state;

      @Override
      protected BaseStateSingle innerEatType(
          Object context, TSList tsList, LuxemPathBuilder luxemPath, String name) {
        state = autoTreeMeta.infos.get(keyToClass.get(name)).create(errors, luxemPath);
        return state;
      }

      @Override
      public Object build(Object context, TSList tsList) {
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
