package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ErrorValue;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.Common;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSOrderedMap;

public class ScopeState {
  public final ScopeState parent;
  private final TSOrderedMap<Object, Binding> data;
  // Null key == block, non-null key = label
  public final TSList<ROPair<String, JumpKey>> labels;
  public boolean error = false;

  private ScopeState(
      ScopeState parent,
      TSOrderedMap<Object, Binding> data,
      TSList<ROPair<String, JumpKey>> labels) {
    this.parent = parent;
    this.data = data;
    this.labels = labels;
  }

  public ScopeState forkChild() {
    TSOrderedMap<Object, Binding> newData = new TSOrderedMap<>();
    for (ROPair<Object, Binding> datum : data) {
      newData.put(datum.first, datum.second.fork());
    }
    return createChild(
        new ScopeState(
            parent.forkChild(),
            newData,
            /* Labels can be reused because it won't be used until forks removed */
            labels));
  }

  public static ScopeState create() {
    return new ScopeState(null, new TSOrderedMap<>(), new TSList<>());
  }

  public static ScopeState createChild(ScopeState parent) {
    return new ScopeState(parent, new TSOrderedMap<>(), new TSList<>());
  }

  public void put(Object key, Binding binding) {
    data.putNew(key, binding);
  }

  public Binding remove(Object key) {
    return data.removeGet(key);
  }

  public Binding get(Object key) {
    if (error) {
      return ErrorValue.binding;
    }
    ScopeState at = this;
    while (at != null) {
      Binding out = at.data.getOpt(key);
      if (out != null) {
        return out;
      }
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

  public void merge(
      EvaluationContext context, Location location, ROPair<Location, ScopeState> other) {
    if (error) {
      return;
    }
    if (other.second.error) {
      error = true;
      return;
    }
    if (data.size() != other.second.data.size()) {
      throw new Assertion();
    }
    for (int i = 0; i < data.size(); i += 1) {
      if (!data.get(i).merge(context, location, other.second.data.get(i), other.first)) {
        error = true;
      }
    }
    if ((parent == null) != (other.second.parent == null)) {
      throw new Assertion();
    }
    parent.merge(context, location, new ROPair<>(other.first, other.second.parent));
  }
}
