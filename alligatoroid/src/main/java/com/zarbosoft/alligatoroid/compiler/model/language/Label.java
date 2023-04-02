package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.JumpKey;
import com.zarbosoft.alligatoroid.compiler.UnreachableValue;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinAutoExportableType;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeLand;
import com.zarbosoft.alligatoroid.compiler.mortar.LanguageElement;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetCode;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;

import java.util.Map;

public class Label extends LanguageElement {
  @BuiltinAutoExportableType.Param public String key;
  @BuiltinAutoExportableType.Param public LanguageElement value;

  @Override
  protected boolean innerHasLowerInSubtree() {
    return value.hasLowerInSubtree();
  }

  @Override
  public EvaluateResult evaluate(EvaluationContext context) {
    final EvaluateResult res = value.evaluate(context);
    TSList<Value> unfork = new TSList<>();
    TSMap<String, TSList<ROPair<JumpKey, Value>>> jumpValues = new TSMap<>();
    TSList<JumpKey> jumpKeys = new TSList<>();
    for (Map.Entry<String, ROList<ROPair<JumpKey, Value>>> e : res.namedJumps) {
      if (e.getKey().equals(key)) {
        for (ROPair<JumpKey, Value> pair : e.getValue()) {
          jumpKeys.add(pair.first);
          unfork.add(pair.second);
        }
      } else {
        jumpValues.put(e.getKey(), e.getValue().mut());
      }
    }
    if (res.value != UnreachableValue.value) {
      unfork.add(res.value);
    }
    unfork.reverse();
    final EvaluateResult.Context ectx = new EvaluateResult.Context(context, id);
    ectx.recordPre(new MortarTargetCode(new JavaBytecodeLand(jumpKeys)));
    ectx.preEffect.add(res.preEffect);
    ectx.postEffect.add(res.postEffect);
    ectx.jumps.addAll(res.jumps);
    ectx.namedJumps.putAll(jumpValues);
    if (unfork.isEmpty()) {
      return ectx.build(UnreachableValue.value);
    } else {
      return ectx.build(ectx.record(unfork.get(0).unfork(context, id, unfork.subFrom(1))));
    }
  }
}
