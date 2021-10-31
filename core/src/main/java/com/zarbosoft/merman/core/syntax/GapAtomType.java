package com.zarbosoft.merman.core.syntax;

import com.zarbosoft.merman.core.MultiError;
import com.zarbosoft.merman.core.syntax.back.BackFixedPrimitiveSpec;
import com.zarbosoft.merman.core.syntax.back.BackFixedRecordSpec;
import com.zarbosoft.merman.core.syntax.back.BackFixedTypeSpec;
import com.zarbosoft.merman.core.syntax.back.BackIdSpec;
import com.zarbosoft.merman.core.syntax.back.BackKeySpec;
import com.zarbosoft.merman.core.syntax.back.BackPrimitiveSpec;
import com.zarbosoft.merman.core.syntax.back.BackSpec;
import com.zarbosoft.merman.core.syntax.back.BackSpecData;
import com.zarbosoft.merman.core.syntax.back.BaseBackPrimitiveSpec;
import com.zarbosoft.merman.core.syntax.error.GapHasExtraField;
import com.zarbosoft.merman.core.syntax.error.GapPrimitiveCantHavePattern;
import com.zarbosoft.merman.core.syntax.error.GapPrimitiveHasBadId;
import com.zarbosoft.merman.core.syntax.front.FrontPrimitiveSpec;
import com.zarbosoft.merman.core.syntax.front.FrontSpec;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROMap;
import com.zarbosoft.rendaw.common.TSList;

import java.util.function.Function;

public class GapAtomType extends BaseGapAtomType {
  public final static BackSpec jsonBack =
          new BackFixedRecordSpec(
              new BackFixedRecordSpec.Config(
                  new TSList<>(
                      new BackKeySpec(
                          new BackFixedPrimitiveSpec("type"), new BackFixedPrimitiveSpec("gap")),
                      new BackKeySpec(
                          new BackFixedPrimitiveSpec("primitive"),
                          new BackPrimitiveSpec(
                              new BaseBackPrimitiveSpec.Config(GapAtomType.PRIMITIVE_KEY))))));
  public final String backType;

  public GapAtomType(Config config) {
    super(
        new AtomType.Config(
            config.id,
            config.back == null
                ? new BackFixedTypeSpec(
                    new BackFixedTypeSpec.Config(
                        config.backType,
                        new BackPrimitiveSpec(new BaseBackPrimitiveSpec.Config(PRIMITIVE_KEY))))
                : config.back,
            new TSList<FrontSpec>()
                .addAll(config.frontPrefix == null ? ROList.empty : config.frontPrefix)
                .add(
                    new FrontPrimitiveSpec(
                        new FrontPrimitiveSpec.Config(PRIMITIVE_KEY)
                            .firstAlignmentId(config.firstAlignmentId)
                            .firstSplitAlignmentId(config.firstSplitAlignmentId)
                            .hardSplitAlignmentId(config.hardSplitAlignmentId)
                            .softSplitAlignmentId(config.softSplitAlignmentId)
                            .meta(config.primitiveMeta)))
                .addAll(config.frontSuffix == null ? ROList.empty : config.frontSuffix)));
    backType = config.backType;
    MultiError checkErrors = new MultiError();
    BackSpec.walkTypeBack(
        back(),
        new Function<BackSpec, Boolean>() {
          @Override
          public Boolean apply(BackSpec backSpec) {
            if (backSpec instanceof BackSpecData) {
              String id = ((BackSpecData) backSpec).id;
              if (backSpec instanceof BaseBackPrimitiveSpec) {
                if (GapAtomType.PRIMITIVE_KEY.equals(((BaseBackPrimitiveSpec) backSpec).id)) {
                  if (((BaseBackPrimitiveSpec) backSpec).pattern != null) {
                    checkErrors.add(new GapPrimitiveCantHavePattern(GapAtomType.this.id));
                  }
                } else {
                  checkErrors.add(
                      new GapPrimitiveHasBadId(
                          GapAtomType.this.id, ((BaseBackPrimitiveSpec) backSpec).id));
                }
              } else if (backSpec instanceof BackIdSpec) {
                // nop
              } else {
                checkErrors.add(new GapHasExtraField(id, GapAtomType.this.id));
              }
            }
            return true;
          }
        });
    checkErrors.raise();
  }

  @Override
  public String name() {
    return "Gap";
  }

  public static class Config {
    public String id = "__gap";
    public String backType = "__gap";
    public BackSpec back;
    public ROList<FrontSpec> frontPrefix = null;
    public ROList<FrontSpec> frontSuffix = null;
    public String firstAlignmentId;
    public String firstSplitAlignmentId;
    public String hardSplitAlignmentId;
    public String softSplitAlignmentId;
    public ROMap<String, Object> primitiveMeta;

    public Config() {}

    public Config back(BackSpec back) {
      this.back = back;
      return this;
    }

    public Config frontPrefix(ROList<FrontSpec> frontPrefix) {
      this.frontPrefix = frontPrefix;
      return this;
    }

    public Config frontSuffix(ROList<FrontSpec> frontSuffix) {
      this.frontSuffix = frontSuffix;
      return this;
    }

    public Config splitAlignmentId(String id) {
      firstSplitAlignmentId = id;
      hardSplitAlignmentId = id;
      softSplitAlignmentId = id;
      return this;
    }

    public Config firstSplitAlignmentId(String id) {
      firstSplitAlignmentId = id;
      return this;
    }

    public Config hardSplitAlignmentId(String id) {
      hardSplitAlignmentId = id;
      return this;
    }

    public Config softSplitAlignmentId(String id) {
      softSplitAlignmentId = id;
      return this;
    }

    public Config primitiveMeta(ROMap<String, Object> meta) {
      this.primitiveMeta = meta;
      return this;
    }
  }
}
