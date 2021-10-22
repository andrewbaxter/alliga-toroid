package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.cache.GraphSerializable;
import com.zarbosoft.alligatoroid.compiler.mortar.Record;
import com.zarbosoft.luxem.write.Writer;
import com.zarbosoft.rendaw.common.TSMap;

public final class Location implements TreeSerializable, GraphSerializable {
  public static final String GRAPH_KEY_MODULE = "module";
  public static final String GRAPH_KEY_ID = "id";
  public final ModuleId module;
  public final int id;

  public Location(ModuleId module, int id) {
    this.module = module;
    this.id = id;
  }

  public static Location graphDeserialize(Record data) {
    return new Location(
        (ModuleId) data.data.get(GRAPH_KEY_MODULE), (Integer) data.data.get(GRAPH_KEY_ID));
  }

  @Override
  public void treeSerialize(Writer writer) {
    writer.recordBegin().primitive("module");
    module.treeSerialize(writer);
    writer.primitive("id").primitive(Integer.toString(id)).recordEnd();
  }

  @Override
  public Record graphSerialize() {
    return new Record(new TSMap<>().put(GRAPH_KEY_MODULE, module).put(GRAPH_KEY_ID, id));
  }
}
