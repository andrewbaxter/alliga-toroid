package com.zarbosoft.merman.core.syntax.builder;

import com.zarbosoft.merman.core.syntax.back.BackDiscardKeySpec;
import com.zarbosoft.merman.core.syntax.back.BackFixedPrimitiveSpec;
import com.zarbosoft.merman.core.syntax.back.BackFixedRecordSpec;
import com.zarbosoft.merman.core.syntax.back.BackKeySpec;
import com.zarbosoft.merman.core.syntax.back.BackSpec;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSOrderedMap;
import com.zarbosoft.rendaw.common.TSSet;

public class BackFixedRecordSpecBuilder {
  private final TSOrderedMap<String, BackSpec> pairs = new TSOrderedMap<>();
  private final TSSet<String> discard = new TSSet<>();

  public BackFixedRecordSpecBuilder field(String key, BackSpec spec) {
    pairs.putNew(key, spec);
    return this;
  }

  public BackFixedRecordSpecBuilder discardField(String key) {
    discard.addNew(key);
    return this;
  }

  public BackSpec build() {
    TSList<BackSpec> newPairs = new TSList<>();
    for (ROPair<String, BackSpec> pair : pairs) {
      newPairs.add(new BackKeySpec(new BackFixedPrimitiveSpec(pair.first), pair.second));
    }
    for (String key : discard) {
      newPairs.add(new BackDiscardKeySpec(new BackFixedPrimitiveSpec(key)));
    }
    return new BackFixedRecordSpec(new BackFixedRecordSpec.Config(newPairs));
  }
}
