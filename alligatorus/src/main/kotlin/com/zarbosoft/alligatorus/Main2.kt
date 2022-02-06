package com.zarbosoft.alligatorus

import com.zarbosoft.alligatorus.model.*
import com.zarbosoft.alligatorus.model.Int
import com.zarbosoft.alligatorus.model.String

typealias EvalContext = Int

fun eval(ctx: EvalContext, node: LanguageType) {
  return when (node) {
    is Access -> {
      val ectx = createEvalContext(ctx)
      val target: ValueT = ectx.record(eval(ctx, node.target))
      val key = ectx.record(eval(ctx, node.argument))
      if (target == Error) return ctx.targetContext.error
      return target.access(ctx, key)
    }
    is Bind -> TODO()
    is Block -> TODO()
    is Builtin -> TODO()
    is Call -> TODO()
    is Int -> TODO()
    is Lower -> TODO()
    is Reconst -> TODO()
    is Record -> TODO()
    is RecordElement -> TODO()
    is Scope -> TODO()
    is Stage -> TODO()
    is String -> TODO()
    is Tuple -> TODO()
    is Vary -> TODO()
  }
}
