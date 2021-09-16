package org.interview.jorge.playback.server

import com.google.android.exoplayer2.ExoPlayer
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

internal class PlaybackServiceCustomActionReceiverTest {
  private val subject = PlaybackServiceCustomActionReceiver()

  @Test
  fun onCustomActionShuffleShuffleModeDisabled() {
    val player = mock<ExoPlayer> {
      on { shuffleModeEnabled } doReturn false
    }

    subject.onCustomAction(
      player, PlaybackServiceCustomActionReceiver.CUSTOM_ACTION_SHUFFLE, mock()
    )

    verify(player).shuffleModeEnabled = true
    verify(player).setShuffleOrder(any())
  }
}