package com.zarbosoft.alligatoroid.compiler.inout.utils.treeauto;

import com.zarbosoft.alligatoroid.compiler.inout.utils.classstate.ClassInfo;
import com.zarbosoft.alligatoroid.compiler.inout.utils.classstate.StateClassBody;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.BaseStateSingle;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.StateRecord;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.luxem.read.path.LuxemPathBuilder;
import com.zarbosoft.luxem.write.Writer;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROMap;
import com.zarbosoft.rendaw.common.ROOrderedMap;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.ROSetRef;
import com.zarbosoft.rendaw.common.TSList;

import java.lang.reflect.Parameter;
import java.util.Map;

import static com.zarbosoft.rendaw.common.Common.uncheck;

class AutoInfoClass implements AutoInfo {
  public final ClassInfo info;
  private final AutoTreeMeta meta;

  AutoInfoClass(AutoTreeMeta meta, Class klass) {
    this.meta = meta;
    this.info = new ClassInfo(klass.getCanonicalName());
  }

  @Override
  public BaseStateSingle create(TSList<Error> errors, LuxemPathBuilder luxemPath) {
    return new StateRecord(new StateClassBody(luxemPath, info));
  }

  @Override
  public void write(Writer writer, Object object) {
    final int paramCount = info.constructor.getParameters().length;
    writer.recordBegin();
    for (int i = 0; i < paramCount; i++) {
      Parameter parameter = info.constructor.getParameters()[i];
      String fieldName = parameter.getName();
      writer.primitive(parameter.getName());
      writeParam(
          writer,
          TypeInfo.fromParam(parameter),
          uncheck(() -> object.getClass().getField(fieldName).get(object)));
    }
    writer.recordEnd();
  }

  private void writeParam(Writer writer, TypeInfo info, Object data) {
    if (ROList.class.isAssignableFrom(info.klass)) {
      writer.arrayBegin();
      for (Object e : ((ROList) data)) {
        writeParam(writer, info.genericArgs[0], e);
      }
      writer.arrayEnd();
    } else if (ROSetRef.class.isAssignableFrom(info.klass)) {
      writer.arrayBegin();
      for (Object e : ((ROSetRef) data)) {
        writeParam(writer, info.genericArgs[0], e);
      }
      writer.arrayEnd();
    } else if (ROMap.class.isAssignableFrom(info.klass)) {
      writer.recordBegin();
      for (Map.Entry e : ((ROMap<?, ?>) data)) {
        writer.primitive((String) e.getKey());
        writeParam(writer, info.genericArgs[1], e.getValue());
      }
      writer.recordEnd();
    } else if (ROOrderedMap.class.isAssignableFrom(info.klass)) {
      writer.recordBegin();
      for (ROPair<?, ?> e : ((ROOrderedMap<?, ?>) data)) {
        writer.primitive((String) e.first);
        writeParam(writer, info.genericArgs[1], e.second);
      }
      writer.recordEnd();
    } else {
      meta.infos.get(info.klass).write(writer, data);
    }
  }
}
