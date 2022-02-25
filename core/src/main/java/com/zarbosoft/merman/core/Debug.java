package com.zarbosoft.merman.core;

import com.zarbosoft.merman.core.wall.Brick;
import com.zarbosoft.merman.core.wall.Course;
import com.zarbosoft.merman.core.wall.bricks.BrickLine;
import com.zarbosoft.merman.core.wall.bricks.BrickText;

public class Debug {
  public static String formatBrick(Brick b) {
    if (b instanceof BrickText) {
      return ((BrickText) b).text.text();
    } else if (b instanceof BrickLine) {
      return ((BrickLine) b).line.text;
    } else {
      return "(e)";
    }
  }

  public static String formatCourse(Course course) {
    StringBuilder out = new StringBuilder();
    for (Brick child : course.children) {
      out.append(formatBrick(child));
      out.append(" ");
    }
    return out.toString();
  }
}
