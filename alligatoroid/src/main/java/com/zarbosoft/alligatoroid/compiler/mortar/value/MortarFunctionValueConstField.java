package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.AlligatorusType;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.StaticAutogen;
import com.zarbosoft.rendaw.common.ROPair;

public class MortarFunctionValueConstField implements Value {
    public MortarFunctionValueConstField(StaticAutogen.FuncInfo funcInfo, Object base) {
    }

    @Override
    public EvaluateResult vary(EvaluationContext context, Location id) {
        TODO();
    }

    @Override
    public boolean canCastTo(AlligatorusType type) {
        TODO();
    }

    @Override
    public EvaluateResult castTo(EvaluationContext context, Location location, AlligatorusType type) {
        TODO();
    }

    @Override
    public Value unfork(EvaluationContext context, Location location, ROPair<Location, Value> other) {
        TODO();
    }

    @Override
    public EvaluateResult realize(EvaluationContext context, Location id) {
        TODO();
    }
}
