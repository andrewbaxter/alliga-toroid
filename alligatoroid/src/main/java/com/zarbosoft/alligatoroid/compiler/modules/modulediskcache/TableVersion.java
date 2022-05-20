package com.zarbosoft.alligatoroid.compiler.modules.modulediskcache;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "version")
public class TableVersion {
  @DatabaseField public int version;

  public TableVersion() {}

  public static TableVersion create(int version) {
    final TableVersion out = new TableVersion();
    out.version = version;
    return out;
  }
}
