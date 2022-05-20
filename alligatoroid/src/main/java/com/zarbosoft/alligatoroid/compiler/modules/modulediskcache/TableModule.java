package com.zarbosoft.alligatoroid.compiler.modules.modulediskcache;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "module")
public class TableModule {
  @DatabaseField(generatedId = true)
  public long id;

  @DatabaseField(unique = true)
  public byte[] spec;

  @DatabaseField public byte[] outputHash;
  @DatabaseField public byte[] output;
}
