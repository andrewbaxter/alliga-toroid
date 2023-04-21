package com.zarbosoft.alligatoroid.compiler.modules.modulediskcache;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "module")
public class TableModule {
  @DatabaseField(generatedId = true)
  public long id;

  @DatabaseField(unique = true, dataType = DataType.BYTE_ARRAY)
  public byte[] spec;

  @DatabaseField(dataType = DataType.BYTE_ARRAY)
  public byte[] outputHash;

  @DatabaseField(dataType = DataType.BYTE_ARRAY)
  public byte[] output;
}
