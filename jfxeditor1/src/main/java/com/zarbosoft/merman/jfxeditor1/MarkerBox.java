package com.zarbosoft.merman.jfxeditor1;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.display.Group;
import com.zarbosoft.merman.core.wall.Attachment;

public class MarkerBox implements Attachment {
    Group group;
    @Override
    public void setConverse(Context context, double converse) {
        group.setConverse(converse);
    }

    @Override
    public void setTransverse(Context context, double transverse) {
        group.setTransverse(transverse, false);
    }

    @Override
    public void destroy(Context context) {
        context.background.removeNode(group);
    }
}
