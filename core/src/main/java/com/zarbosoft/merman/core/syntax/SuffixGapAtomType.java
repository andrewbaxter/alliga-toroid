package com.zarbosoft.merman.core.syntax;

import com.zarbosoft.merman.core.MultiError;
import com.zarbosoft.merman.core.syntax.back.BackArraySpec;
import com.zarbosoft.merman.core.syntax.back.BackFixedPrimitiveSpec;
import com.zarbosoft.merman.core.syntax.back.BackFixedRecordSpec;
import com.zarbosoft.merman.core.syntax.back.BackFixedTypeSpec;
import com.zarbosoft.merman.core.syntax.back.BackIdSpec;
import com.zarbosoft.merman.core.syntax.back.BackKeySpec;
import com.zarbosoft.merman.core.syntax.back.BackPrimitiveSpec;
import com.zarbosoft.merman.core.syntax.back.BackSpec;
import com.zarbosoft.merman.core.syntax.back.BackSpecData;
import com.zarbosoft.merman.core.syntax.back.BaseBackArraySpec;
import com.zarbosoft.merman.core.syntax.back.BaseBackPrimitiveSpec;
import com.zarbosoft.merman.core.syntax.error.GapHasExtraField;
import com.zarbosoft.merman.core.syntax.error.GapPrimitiveCantHavePattern;
import com.zarbosoft.merman.core.syntax.error.GapPrimitiveHasBadId;
import com.zarbosoft.merman.core.syntax.error.SuffixGapPrecedingHasBadId;
import com.zarbosoft.merman.core.syntax.front.FrontArraySpec;
import com.zarbosoft.merman.core.syntax.front.FrontArraySpecBase;
import com.zarbosoft.merman.core.syntax.front.FrontPrimitiveSpec;
import com.zarbosoft.merman.core.syntax.front.FrontSpec;
import com.zarbosoft.merman.core.syntax.symbol.Symbol;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROMap;
import com.zarbosoft.rendaw.common.TSList;

import java.util.function.Function;

public class SuffixGapAtomType extends BaseGapAtomType {
  public static final String PRECEDING_KEY = "preceding";
  public static final BackSpec jsonBack =
          new BackFixedRecordSpec(
              new BackFixedRecordSpec.Config(
                  new TSList<BackSpec>(
                      new BackKeySpec(
                          new BackFixedPrimitiveSpec("type"),
                          new BackFixedPrimitiveSpec("suffix_gap")),
                      new BackKeySpec(
                          new BackFixedPrimitiveSpec("primitive"),
                          new BackPrimitiveSpec(
                              new BaseBackPrimitiveSpec.Config(GapAtomType.PRIMITIVE_KEY))),
                      new BackKeySpec(
                          new BackFixedPrimitiveSpec("preceding"),
                          new BackArraySpec(
                              new BaseBackArraySpec.Config(
                                  SuffixGapAtomType.PRECEDING_KEY, null, ROList.empty))))));
  public final String backType;

  public SuffixGapAtomType(Config config) {
    super(
        new AtomType.Config(
            config.id,
            config.back == null
                ? new BackFixedTypeSpec(
                    new BackFixedTypeSpec.Config(
                        config.backType,
                        new BackFixedRecordSpec(
                            new BackFixedRecordSpec.Config(
                                new TSList<BackSpec>(
                                    new BackKeySpec(
                                        new BackFixedPrimitiveSpec("text"),
                                        new BackPrimitiveSpec(
                                            new BaseBackPrimitiveSpec.Config(
                                                GapAtomType.PRIMITIVE_KEY))),
                                    new BackKeySpec(
                                        new BackFixedPrimitiveSpec("preceding"),
                                        new BackArraySpec(
                                            new BaseBackArraySpec.Config(
                                                SuffixGapAtomType.PRECEDING_KEY,
                                                null,
                                                ROList.empty))))))))
                : config.back,
            new TSList<FrontSpec>()
                .add(
                    new FrontArraySpec(
                        new FrontArraySpec.Config(PRECEDING_KEY, config.frontArrayConfig)))
                .addAll(config.frontPrefix)
                .add(
                    new FrontPrimitiveSpec(
                        new FrontPrimitiveSpec.Config(PRIMITIVE_KEY)
                            .firstAlignmentId(config.firstAlignmentId)
                            .firstSplitAlignmentId(config.firstSplitAlignmentId)
                            .hardSplitAlignmentId(config.hardSplitAlignmentId)
                            .softSplitAlignmentId(config.softSplitAlignmentId)
                            .meta(config.primitiveMeta)))
                .addAll(config.frontSuffix)));
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
                if (GapAtomType.PRIMITIVE_KEY.equals(id)) {
                  if (((BaseBackPrimitiveSpec) backSpec).pattern != null) {
                    checkErrors.add(new GapPrimitiveCantHavePattern(SuffixGapAtomType.this.id));
                  }
                } else {
                  checkErrors.add(new GapPrimitiveHasBadId(SuffixGapAtomType.this.id, id));
                }
              } else if (backSpec instanceof BaseBackArraySpec) {
                if (SuffixGapAtomType.PRECEDING_KEY.equals(id)) {
                  // nop
                } else {
                  checkErrors.add(new SuffixGapPrecedingHasBadId(SuffixGapAtomType.this.id, id));
                }
              } else if (backSpec instanceof BackIdSpec) {
                // nop
              } else {
                checkErrors.add(new GapHasExtraField(SuffixGapAtomType.this.id, id));
              }
            }
            return true;
          }
        });
    checkErrors.raise();
  }

  @Override
  public String name() {
    return "Suffix gap";
  }

  public static class Config {
    public String id = "__suffix_gap";
    public String backType = "__suffix_gap";
    public BackSpec back;
    public FrontArraySpecBase.Config frontArrayConfig = new FrontArraySpecBase.Config();
    public ROList<FrontSpec> frontSuffix = ROList.empty;
    public ROList<FrontSpec> frontPrefix = ROList.empty;
    public Symbol gapPlaceholderSymbol;
    public String firstAlignmentId;
    public String firstSplitAlignmentId;
    public String hardSplitAlignmentId;
    public String softSplitAlignmentId;
    public ROMap<String, Object> primitiveMeta;

    public Config() {}

    public Config primitiveMeta(ROMap<String, Object> meta) {
      this.primitiveMeta = meta;
      return this;
    }

    public Config back(BackSpec back) {
      this.back = back;
      return this;
    }

    public Config frontSuffix(ROList<FrontSpec> specs) {
      this.frontSuffix = specs;
      return this;
    }

    public Config frontPrefix(ROList<FrontSpec> specs) {
      this.frontPrefix = specs;
      return this;
    }

    public Config frontArrayConfig(FrontArraySpecBase.Config config) {
      this.frontArrayConfig = config;
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
  }
}
