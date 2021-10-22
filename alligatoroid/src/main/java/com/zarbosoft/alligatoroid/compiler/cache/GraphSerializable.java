package com.zarbosoft.alligatoroid.compiler.cache;

import com.zarbosoft.alligatoroid.compiler.mortar.Record;

public interface GraphSerializable {
  // Must also implement static (thistype) graphDeserialize(Record data)

  /**
   * Presents the object structure as a record so it can be walked by the graph serializer.
   * @return
   */
  Record graphSerialize();
}
