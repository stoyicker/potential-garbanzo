package org.interview.jorge.playback.server

import android.content.Intent
import org.junit.Assert.assertNull
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyNoMoreInteractions

internal class PlaybackServiceTest {
  private val subject = PlaybackService()

  @Test
  fun bindingNotAllowed() {
    val intent = mock<Intent>()

    assertNull(subject.onBind(intent))

    verifyNoMoreInteractions(intent)
  }
}
