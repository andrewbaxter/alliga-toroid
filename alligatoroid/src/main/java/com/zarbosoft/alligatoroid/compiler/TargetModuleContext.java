package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.value.LooseRecord;
import com.zarbosoft.alligatoroid.compiler.mortar.value.LooseTuple;

public interface TargetModuleContext {
  public TargetCode merge(
      EvaluationContext context, Location location, Iterable<TargetCode> values);

  Id id();

  TargetCode codeLand(JumpKey jumpKey);

  TargetCode codeJump(JumpKey jumpKey);

    EvaluateResult realizeRecord(EvaluationContext context, Location id, LooseRecord looseRecord);
  EvaluateResult realizeTuple(EvaluationContext context, Location id, LooseTuple looseTuple);

    boolean looseRecordCanCastTo(EvaluationContext context, LooseRecord looseRecord, AlligatorusType type);

  EvaluateResult looseRecordCastTo(EvaluationContext context, Location location, AlligatorusType type);

  EvaluateResult looseTupleCastTo(EvaluationContext context, Location location, AlligatorusType type);

  boolean looseTupleCanCastTo(EvaluationContext context, LooseTuple looseTuple, AlligatorusType type);

  public abstract static class Id {
    @Override
    public abstract String toString();
  }
}
