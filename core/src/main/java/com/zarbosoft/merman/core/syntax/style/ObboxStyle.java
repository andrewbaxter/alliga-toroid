package com.zarbosoft.merman.core.syntax.style;

public class ObboxStyle {
  public final Integer padding;
  public final Boolean roundStart;
  public final Boolean roundEnd;
  public final Boolean roundOuterEdges;
  public final Boolean roundInnerEdges;
  public final Boolean roundConcave;
  public final Integer roundRadius;
  public final Boolean line;
  public final ModelColor lineColor;
  public final Double lineThickness;
  public final Boolean fill;
  public final ModelColor fillColor;

  public ObboxStyle(Config config) {
    this.padding = config.padding == null ? 4 : config.padding;
    this.roundStart = config.roundStart == null ? false : config.roundStart;
    this.roundEnd = config.roundEnd == null ? false : config.roundEnd;
    this.roundOuterEdges = config.roundOuterEdges == null ? false : config.roundOuterEdges;
    this.roundInnerEdges = config.roundInnerEdges == null ? false : config.roundInnerEdges;
    this.roundConcave = config.roundConcave == null ? false : config.roundConcave;
    this.roundRadius = config.roundRadius == null ? 0 : config.roundRadius;
    this.line = config.line == null ? true : config.line;
    this.lineColor = config.lineColor == null ? ModelColor.RGB.black : config.lineColor;
    this.lineThickness = config.lineThickness == null ? 1 : config.lineThickness;
    this.fill = config.fill == null ? false : config.fill;
    this.fillColor = config.fillColor == null ? ModelColor.RGB.white : config.fillColor;
  }

  public static class Config {
    public Integer padding;
    public Boolean roundStart;
    public Boolean roundEnd;
    public Boolean roundOuterEdges;
    public Boolean roundInnerEdges;
    public Boolean roundConcave;
    public Integer roundRadius;
    public Boolean line;
    public ModelColor lineColor;
    public Double lineThickness;
    public Boolean fill;
    public ModelColor fillColor;

    public Config roundStart(boolean b) {
      this.roundStart = b;
      return this;
    }

    public Config roundEnd(boolean b) {
      this.roundEnd = b;
      return this;
    }

    public Config roundConcave(boolean b) {
      this.roundConcave = b;
      return this;
    }

    public Config roundOuterEdges(boolean b) {
      this.roundOuterEdges = b;
      return this;
    }

    public Config roundInnerEdges(boolean b) {
      this.roundInnerEdges = b;
      return this;
    }

    public Config lineThickness(double d) {
      this.lineThickness = d;
      return this;
    }

    public Config roundRadius(int d) {
      this.roundRadius = d;
      return this;
    }

    public Config line(boolean b) {
      this.line = b;
      return this;
    }

    public Config fill(boolean b) {
      this.fill = b;
      return this;
    }

    public Config lineColor(ModelColor c) {
      this.lineColor = c;
      return this;
    }

    public Config fillColor(ModelColor c) {
      this.fillColor = c;
      return this;
    }
  }
}
