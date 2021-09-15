package org.interview.jorge.playback.server

import android.app.Service.START_STICKY
import android.content.Intent
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
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

  @Test
  fun startsSticky() {
    val intent = mock<Intent>()

    assertSame(START_STICKY, subject.onStartCommand(intent, 0, 0))

    verifyNoMoreInteractions(intent)
  }
}
