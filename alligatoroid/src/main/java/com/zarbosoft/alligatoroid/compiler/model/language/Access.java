package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.model.ids.ModuleId;
import com.zarbosoft.alligatoroid.compiler.mortar.LanguageElement;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROSetRef;
import com.zarbosoft.rendaw.common.TSMap;
import com.zarbosoft.rendaw.common.TSSet;

import java.util.function.BiFunction;

public class Access extends LanguageElement {
  @Param public LanguageElement base;
  @Param public LanguageElement key;

  @Override
  protected boolean innerHasLowerInSubtree() {
    return hasLowerInSubtree(base, key);
  }

  @Override
  public EvaluateResult evaluate(EvaluationContext context) {
    EvaluateResult.Context ectx = new EvaluateResult.Context(context, id);
    final Value base = ectx.evaluate(this.base);
    ROList<String> stringFields = base.traceFields(context, id);
    context.moduleContext.compileContext.traceModuleStringFields.compute(
        context.moduleContext.importId.moduleId,
        new BiFunction<
            ModuleId, TSMap<Location, ROSetRef<String>>, TSMap<Location, ROSetRef<String>>>() {
          @Override
          public TSMap<Location, ROSetRef<String>> apply(
              ModuleId moduleId, TSMap<Location, ROSetRef<String>> entries) {
            if (entries == null) entries = new TSMap<>();
            final ROSetRef<String> existingFields = entries.getOpt(id);
            if (existingFields == null) {
              entries.put(id, stringFields.toSet());
            } else {
              TSSet<String> unionExistingFields = new TSSet<>();
              for (String field : stringFields) {
                if (existingFields.contains(field)) {
                  unionExistingFields.add(field);
                }
              }
              entries.putReplace(id, unionExistingFields);
            }
            return entries;
          }
        });
    return ectx.build(ectx.record(base.access(context, id, ectx.evaluate(this.key))));
  }
}
