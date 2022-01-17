package com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto;

import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialBool;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialInt;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialString;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialSubvalue;
import com.zarbosoft.rendaw.common.ROMap;
import com.zarbosoft.rendaw.common.TSMap;

public class Pregen {
  public static final ROMap<Class, AutoBuiltinExportableType.InlineExportableType> graphAuxConverters;

  static {
    //// Graph id/primitive type conversions
    // =============================
    /// Prepare type converters for non-value, non-collection types
    TSMap<Class, AutoBuiltinExportableType.InlineExportableType> graphAuxConverters0 = new TSMap<>();

    /// Simple types
    AutoBuiltinExportableType.InlineExportableType intConverter =
        new AutoBuiltinExportableType.InlineExportableType() {
          @Override
          public Object desemiserialize(SemiserialSubvalue data) {
            return data.dispatch(
                new SemiserialSubvalue.DefaultDispatcher<Object>() {
                  @Override
                  public Object handleInt(SemiserialInt s) {
                    return s.value;
                  }
                });
          }

          @Override
          public SemiserialSubvalue semiserialize(Object data) {
            return new SemiserialInt((Integer) data);
          }
        };
    graphAuxConverters0.put(Integer.class, intConverter);
    graphAuxConverters0.put(int.class, intConverter);
    AutoBuiltinExportableType.InlineExportableType boolConverter =
        new AutoBuiltinExportableType.InlineExportableType() {
          @Override
          public Object desemiserialize(SemiserialSubvalue data) {
            return data.dispatch(
                new SemiserialSubvalue.DefaultDispatcher<>() {
                  @Override
                  public Object handleBool(SemiserialBool s) {
                    return s.value;
                  }
                });
          }

          @Override
          public SemiserialSubvalue semiserialize(Object data) {
            return new SemiserialBool((Boolean) data);
          }
        };
    graphAuxConverters0.put(Boolean.class, boolConverter);
    graphAuxConverters0.put(boolean.class, boolConverter);
    AutoBuiltinExportableType.InlineExportableType stringConverter =
        new AutoBuiltinExportableType.InlineExportableType() {
          @Override
          public Object desemiserialize(SemiserialSubvalue data) {
            return data.dispatch(
                new SemiserialSubvalue.DefaultDispatcher<>() {
                  @Override
                  public Object handleString(SemiserialString s) {
                    return s.value;
                  }
                });
          }

          @Override
          public SemiserialSubvalue semiserialize(Object data) {
            return new SemiserialString((String) data);
          }
        };
    graphAuxConverters0.put(String.class, stringConverter);
    graphAuxConverters = graphAuxConverters0;
  }
}
