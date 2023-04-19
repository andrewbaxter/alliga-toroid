package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.value.LooseRecord;
import com.zarbosoft.rendaw.common.ROOrderedMap;

public interface TargetModuleContext {
  public TargetCode merge(
      EvaluationContext context, Location location, Iterable<TargetCode> values);

  Id id();

  TargetCode codeLand(JumpKey jumpKey);

  TargetCode codeJump(JumpKey jumpKey);

  EvaluateResult realizeRecord(EvaluationContext context, Location id, LooseRecord looseRecord);

  boolean looseRecordCanCastTo(
      EvaluationContext context, LooseRecord looseRecord, AlligatorusType type);

  EvaluateResult looseRecordCastTo(
          EvaluationContext context, Location location, ROOrderedMap<Object, EvaluateResult> data, AlligatorusType type);

    AlligatorusType looseRecordType(EvaluationContext context, LooseRecord looseRecord);

    public abstract static class Id {
    @Override
    public abstract String toString();
  }
}
