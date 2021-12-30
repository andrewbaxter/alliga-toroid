package com.zarbosoft.alligatoroid.compiler.inout.graph;

import com.zarbosoft.alligatoroid.compiler.inout.tree.TreeSerializable;
import com.zarbosoft.luxem.write.Writer;

public class SemiserialValue implements TreeSerializable {
  public static final String KEY_TYPE = "type";
  public static final String KEY_DATA = "data";
  public final SemiserialRef type;
  public final SemiserialSubvalue data;

  public SemiserialValue(SemiserialRef type, SemiserialSubvalue data) {
    this.type = type;
    this.data = data;
  }

  @Override
  public void treeSerialize(Writer writer) {
    writer.recordBegin();
    writer.primitive(KEY_TYPE);
    type.treeSerialize(writer);
    writer.primitive(KEY_DATA);
    data.treeSerialize(writer);
    writer.recordEnd();
  }
}
