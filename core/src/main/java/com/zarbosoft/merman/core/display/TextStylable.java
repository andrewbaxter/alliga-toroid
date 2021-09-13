package com.zarbosoft.merman.core.display;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.syntax.style.ModelColor;

public interface TextStylable {
    void setColor(Context context, ModelColor color);
    void setFont(Context context, Font font);
}
