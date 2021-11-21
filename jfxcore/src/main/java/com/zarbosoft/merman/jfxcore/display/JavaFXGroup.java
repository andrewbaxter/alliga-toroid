package com.zarbosoft.merman.jfxcore.display;

import com.zarbosoft.merman.core.display.DisplayNode;
import com.zarbosoft.merman.core.display.Group;
import javafx.scene.Node;

public class JavaFXGroup extends JavaFXFreeDisplayNode implements Group {
  protected JavaFXGroup(JavaFXDisplay display) {
    super(display, new javafx.scene.Group());
  }

  @Override
  public void add(final int index, final DisplayNode node) {
    ((javafx.scene.Group) this.node).getChildren().add(index, (Node) node.inner_());
  }

  @Override
  public void removeAt(final int index, final int count) {
    ((javafx.scene.Group) this.node).getChildren().subList(index, index + count).clear();
  }

  @Override
  public void removeNode(final DisplayNode node) {
    ((javafx.scene.Group) this.node).getChildren().remove(node.inner_());
  }

  @Override
  public int size() {
    return ((javafx.scene.Group) this.node).getChildren().size();
  }

  @Override
  public void clear() {
    ((javafx.scene.Group) this.node).getChildren().clear();
  }

  @Override
  public void setTransverse(double transverse, boolean animate) {
    this.transverse = transverse;
    fixPosition(animate);
  }
}
