package com.zarbosoft.merman.webview.serialization;

import elemental2.core.JsArray;

public class JsonWriteArrayState implements JsonWriteState {
  public JsArray value = new JsArray();

  @Override
  public void value(Object value) {
    this.value.push(value);
  }
}
