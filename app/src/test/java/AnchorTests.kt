package org.interview.jorge

import org.junit.Assert
import org.junit.Test

internal class AnchorTests {
  @Test
  fun passes() = Unit

  @Test(expected = AssertionError::class)
  fun fails() {
    Assert.fail()
  }
}
