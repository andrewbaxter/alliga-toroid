package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.ROMap;
import com.zarbosoft.rendaw.common.TSList;

import java.util.Map;

public class RecordBinding implements Binding {
private final ROMap<Object, Binding> children;

    public RecordBinding(ROMap<Object, Binding> children) {
        this.children = children;
    }

    @Override
    public EvaluateResult load(EvaluationContext context, Location location) {
    }

    @Override
    public TargetCode dropCode(EvaluationContext context, Location location) {
    TSList<TargetCode> code = new TSList<>();
    for (Map.Entry<Object, Binding> child : children) {
        code.add(child.getValue().dropCode(context, location));
    }
    return context.target.merge(context,location,code);
    }
}
