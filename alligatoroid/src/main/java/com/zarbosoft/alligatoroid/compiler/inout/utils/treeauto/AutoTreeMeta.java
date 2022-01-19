package com.zarbosoft.alligatoroid.compiler.inout.utils.treeauto;

import com.zarbosoft.alligatoroid.compiler.Utils;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.BaseStateSingle;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.Prototype;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.PrototypeBool;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.PrototypeInt;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.PrototypeROList;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.PrototypeROMap;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.PrototypeROOrderedMap;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.PrototypeString;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.luxem.read.path.LuxemPathBuilder;
import com.zarbosoft.luxem.write.Writer;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROMap;
import com.zarbosoft.rendaw.common.ROOrderedMap;
import com.zarbosoft.rendaw.common.ROSetRef;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Map;

import static com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.DefaultStateBool.BOOL_FALSE;
import static com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.DefaultStateBool.BOOL_TRUE;
import static com.zarbosoft.rendaw.common.Common.uncheck;

public class AutoTreeMeta {
  private static final AutoInfo stringInfo =
      new AutoInfo() {
        @Override
        public BaseStateSingle create(TSList<Error> errors, LuxemPathBuilder luxemPath) {
          return PrototypeString.instance.create(errors, luxemPath);
        }

        @Override
        public void write(Writer writer, Object object) {
          writer.primitive((String) object);
        }
      };
  private static final AutoInfo intInfo =
      new AutoInfo() {
        @Override
        public BaseStateSingle create(TSList<Error> errors, LuxemPathBuilder luxemPath) {
          return PrototypeInt.instance.create(errors, luxemPath);
        }

        @Override
        public void write(Writer writer, Object object) {
          writer.primitive(Integer.toString((Integer) object));
        }
      };
  private static final AutoInfo boolInfo =
      new AutoInfo() {
        @Override
        public BaseStateSingle create(TSList<Error> errors, LuxemPathBuilder luxemPath) {
          return PrototypeBool.instance.create(errors, luxemPath);
        }

        @Override
        public void write(Writer writer, Object object) {
          writer.primitive(((Boolean) object) ? BOOL_TRUE : BOOL_FALSE);
        }
      };
  public final TSMap<Class, AutoInfo> infos = new TSMap<>();

  public AutoTreeMeta() {
    infos.put(String.class, stringInfo);
    infos.put(Integer.class, intInfo);
    infos.put(int.class, intInfo);
    infos.put(Boolean.class, boolInfo);
    infos.put(boolean.class, boolInfo);
  }

  public void serialize(Writer writer, Object object) {
    serialize(writer, TypeInfo.fromClass(object.getClass()), object);
  }

  public void serialize(Writer writer, TypeInfo rf, Object object) {
    if (ROMap.class.isAssignableFrom(object.getClass())) {
      writer.recordBegin();
      for (Map.Entry e : (Iterable<Map.Entry>) ((ROMap) object)) {
        writer.primitive((String) e.getKey());
        serialize(writer, rf.genericArgs[1], e.getValue());
      }
      writer.recordEnd();
    } else if (ROList.class.isAssignableFrom(object.getClass())) {
      writer.arrayBegin();
      for (Object e : ((ROList) object)) {
        serialize(writer, rf.genericArgs[0], e);
      }
      writer.arrayEnd();
    } else if (ROSetRef.class.isAssignableFrom(object.getClass())) {
      writer.arrayBegin();
      for (Object e : ((ROSetRef) object)) {
        serialize(writer, rf.genericArgs[0], e);
      }
      writer.arrayEnd();
    } else infos.get(rf.klass).write(writer, object);
  }

  public <C, T> BaseStateSingle<C, T> deserialize(
      TSList<Error> errors, LuxemPathBuilder luxemPath, Class<T> root) {
    return infos.get(root).create(errors, luxemPath);
  }

  public void scan(Class klass) {
    if (infos.has(klass)) return;
    if (Modifier.isAbstract(klass.getModifiers()) || Modifier.isInterface(klass.getModifiers())) {
      Class[] elements;
      try {
        elements = (Class[]) klass.getField("SERIAL_UNION").get(null);
      } catch (IllegalAccessException e) {
        throw uncheck(e);
      } catch (NoSuchFieldException e) {
        throw Assertion.format(
            "Missing union field in %s: %s", klass.getCanonicalName(), e.toString());
      }
      String commonPrefix = elements[0].getSimpleName();
      while (commonPrefix.length() > 0) {
        boolean allMatch = false;
        for (Class element : elements) {
          if (!element.getSimpleName().startsWith(commonPrefix)) {
            allMatch = false;
            break;
          }
        }
        if (allMatch) {
          break;
        }
        commonPrefix = commonPrefix.substring(0, commonPrefix.length() - 1);
      }
      TSMap<String, Class> keyToClass = new TSMap<>();
      TSMap<Class, String> classToKey = new TSMap<>();
      for (Class element : elements) {
        String key = Utils.toUnderscore(element.getSimpleName().substring(commonPrefix.length()));
        keyToClass.put(key, element);
        classToKey.put(element, key);
      }

      infos.put(klass, new AutoInfoEnum(this, keyToClass, classToKey));
      for (Class element : elements) {
        scan(element);
      }
    } else {
      final AutoInfoClass info = new AutoInfoClass(this, klass);
      infos.put(klass, info);
      Constructor constructor = klass.getConstructors()[0];
      TSMap<String, Prototype> fields = new TSMap<>();
      TSMap<String, Integer> argOrder = new TSMap<>();
      for (int i = 0; i < constructor.getParameters().length; i++) {
        Parameter parameter = constructor.getParameters()[i];
        TypeInfo paramInfo = TypeInfo.fromParam(parameter);
        argOrder.put(parameter.getName(), i);
        Prototype prototype;
        if (ROList.class.isAssignableFrom(parameter.getType())) {
          final Class genericArg = paramInfo.genericArgs[0].klass;
          scan(genericArg);
          prototype = new PrototypeROList(new PrototypeAutoRef(this, genericArg));
        } else if (ROSetRef.class.isAssignableFrom(parameter.getType())) {
          final Class genericArg = paramInfo.genericArgs[0].klass;
          scan(genericArg);
          prototype = new PrototypeROList(new PrototypeAutoRef(this, genericArg));
        } else if (ROMap.class.isAssignableFrom(parameter.getType())) {
          final Class genericArg = paramInfo.genericArgs[1].klass;
          scan(genericArg);
          prototype =
              new PrototypeROMap(PrototypeString.instance, new PrototypeAutoRef(this, genericArg));
        } else if (ROOrderedMap.class.isAssignableFrom(parameter.getType())) {
          final Class genericArg = paramInfo.genericArgs[1].klass;
          scan(genericArg);
          prototype =
              new PrototypeROOrderedMap(
                  PrototypeString.instance, new PrototypeAutoRef(this, genericArg));
        } else {
          scan(parameter.getType());
          prototype = new PrototypeAutoRef(this, parameter.getType());
        }
        fields.put(parameter.getName(), prototype);
      }
      info.info.constructor = constructor;
      info.info.argOrder = argOrder;
      info.info.fields = fields;
    }
  }
}
