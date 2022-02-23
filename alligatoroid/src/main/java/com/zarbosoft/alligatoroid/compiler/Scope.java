package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ErrorValue;
import com.zarbosoft.rendaw.common.Common;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSOrderedMap;

public class Scope {
  public final Scope parent;
  private final TSOrderedMap<Object, Binding> data = new TSOrderedMap<>();
  public boolean error = false;

  public Scope(Scope parent) {
    this.parent = parent;
  }

  public void put(Object key, Binding binding) {
    data.putNew(key, binding);
  }

  public Binding remove(Object key) {
    return data.removeGet(key);
  }

  public Binding get(Object key) {
    if (error) return ErrorValue.binding;
    Scope at = this;
    while (at != null) {
      Binding out = at.data.getOpt(key);
      if (out != null) return out;
      at = at.parent;
    }
    return null;
  }

  public ROList<Binding> atLevel() {
    TSList<Binding> out = new TSList<>();
    for (ROPair<Object, Binding> e : Common.iterable(data.iterator())) {
      out.add(e.second);
    }
    return out;
  }

  public Iterable<ROPair<Object, Binding>> keys() {
    return data;
  }
}
