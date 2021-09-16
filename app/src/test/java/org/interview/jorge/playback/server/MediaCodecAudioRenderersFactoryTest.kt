package org.interview.jorge.playback.server

import android.content.Context
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock

internal class MediaCodecAudioRenderersFactoryTest {
  private val context = mock<Context>()
  private val subject = MediaCodecAudioRenderersFactory(context)

  @Test
  fun createRenderers() {
    val actual = subject.createRenderers(mock(), mock(), mock(), mock(), mock())

    assertEquals(1, actual.size)
  }
}
