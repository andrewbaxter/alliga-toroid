package com.zarbosoft.merman.syntax.back;

import com.zarbosoft.merman.document.values.Value;
import com.zarbosoft.merman.document.values.ValueArray;
import com.zarbosoft.merman.editor.Path;
import com.zarbosoft.merman.misc.TSMap;
import com.zarbosoft.merman.syntax.InvalidSyntax;
import com.zarbosoft.merman.syntax.Syntax;
import com.zarbosoft.merman.syntax.front.FrontArraySpecBase;

import java.util.Set;

public abstract class BaseBackArraySpec extends BackSpecData{
  public String type;
  public FrontArraySpecBase front;

  public Path getPath(final ValueArray value, final int actualIndex) {
    return value.getSyntaxPath().add(String.format("%d", actualIndex));
  }

  @Override
  public void finish(final Set<String> allTypes, final Set<String> scalarTypes) {
    if (type != null && !allTypes.contains(type))
      throw new InvalidSyntax(String.format("Unknown type [%s].", type));
  }

  @Override
  public Value create(final Syntax syntax) {
    return new ValueArray(this);
  }

  public ValueArray get(final TSMap<String, Value> data) {
    return (ValueArray) data.get(id);
  }
}
