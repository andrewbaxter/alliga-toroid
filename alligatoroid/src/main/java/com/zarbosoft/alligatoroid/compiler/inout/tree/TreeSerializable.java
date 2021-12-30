package com.zarbosoft.alligatoroid.compiler.inout.tree;

import com.zarbosoft.luxem.write.Writer;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROMap;

import java.util.Map;

public interface TreeSerializable {
  public static void treeSerialize(Writer writer, Object object) {
    if (object instanceof TreeSerializable) {
      ((TreeSerializable) object).treeSerialize(writer);
    } else if (ROMap.class.isAssignableFrom(object.getClass())) {
      writer.recordBegin();
      for (Map.Entry e : (Iterable<Map.Entry>) ((ROMap) object)) {
        writer.primitive((String) e.getKey());
        treeSerialize(writer, e.getValue());
      }
      writer.recordEnd();
    } else if (ROList.class.isAssignableFrom(object.getClass())) {
      writer.arrayBegin();
      for (Object e : ((ROList) object)) {
        treeSerialize(writer, e);
      }
      writer.arrayEnd();
    } else if (object.getClass() == String.class) {
      writer.primitive((String) object);
    } else if (object.getClass() == Integer.class) {
      writer.primitive(Integer.toString((Integer) object));
    } else throw new Assertion();
  }

  public void treeSerialize(Writer writer);

  public static class Url implements TreeSerializable {
    private final String url;

    public Url(String url) {
      this.url = url;
    }

    @Override
    public void treeSerialize(Writer writer) {
      writer.primitive(url);
    }
  }
}
