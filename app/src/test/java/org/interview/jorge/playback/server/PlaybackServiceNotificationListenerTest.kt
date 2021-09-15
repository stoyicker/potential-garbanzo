package org.interview.jorge.playback.server

import android.app.Notification
import android.app.Service
import org.junit.Test
import org.mockito.Mockito.verify
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyNoMoreInteractions

internal class PlaybackServiceNotificationListenerTest {
  private val service = mock<Service>()
  private val subject = PlaybackServiceNotificationListener(service)

  @Test
  fun onPlaybackServiceNotificationCancelledStopsSelf() {
    subject.onNotificationCancelled(PLAYBACK_SERVICE_FOREGROUND_NOTIFICATION_ID, false)

    verify(service).stopSelf()
  }

  @Test
  fun onNonPlaybackServiceNotificationCancelledDoesNothing() {
    subject.onNotificationCancelled(PLAYBACK_SERVICE_FOREGROUND_NOTIFICATION_ID + 1, false)

    verifyNoMoreInteractions(service)
  }

  @Test
  fun onPlaybackServiceNotificationPostedStartsForeground() {
    val notification = mock<Notification>()

    subject.onNotificationPosted(PLAYBACK_SERVICE_FOREGROUND_NOTIFICATION_ID, notification, false)

    verify(service).startForeground(PLAYBACK_SERVICE_FOREGROUND_NOTIFICATION_ID, notification)
  }

  @Test
  fun onNonPlaybackServiceNotificationPostedDoesNothing() {
    val notification = mock<Notification>()

    subject.onNotificationPosted(
      PLAYBACK_SERVICE_FOREGROUND_NOTIFICATION_ID + 1, notification, false
    )

    verifyNoMoreInteractions(service)
  }
}
