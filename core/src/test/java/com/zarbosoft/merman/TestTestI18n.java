package com.zarbosoft.merman;

import com.zarbosoft.merman.core.Environment;
import com.zarbosoft.merman.helper.TestEnvironment;
import org.junit.Assert;
import org.junit.Test;

import static com.zarbosoft.merman.core.Environment.I18N_DONE;
import static org.hamcrest.CoreMatchers.equalTo;

public class TestTestI18n {
  @Test
  public void testWordSplits() {
    TestEnvironment e = new TestEnvironment();
    // 0-5 7-10 11-16 17
    String text = "these  are three ";
    Environment.WordWalker walker = e.wordWalker(text);
    int[] precedingStart = new int[text.length() + 1];
    int[] precedingEnd = new int[text.length() + 1];
    int[] followingStart = new int[text.length() + 1];
    int[] followingEnd = new int[text.length() + 1];
    for (int i = 0; i <= text.length(); ++i) {
      precedingStart[i] = walker.startBefore(i);
      precedingEnd[i] = walker.endBefore(i);
      followingStart[i] = walker.startAfter(i);
      followingEnd[i] = walker.endAfter(i);
    }
    Assert.assertThat(
        precedingStart,
        equalTo(new int[] {I18N_DONE, 0, 0, 0, 0, 0, 0, 0, 7, 7, 7, 7, 11, 11, 11, 11, 11, 11}));
    Assert.assertThat(
        precedingEnd,
        equalTo(new int[] {I18N_DONE, 0, 0, 0, 0, 0, 5, 5, 5, 5, 5, 10, 10, 10, 10, 10, 10, 16}));
    Assert.assertThat(
        followingStart,
        equalTo(
            new int[] {7, 7, 7, 7, 7, 7, 7, 11, 11, 11, 11, 17, 17, 17, 17, 17, 17, I18N_DONE}));
    Assert.assertThat(
        followingEnd,
        equalTo(
            new int[] {5, 5, 5, 5, 5, 10, 10, 10, 10, 10, 16, 16, 16, 16, 16, 16, 17, I18N_DONE}));
  }
}
