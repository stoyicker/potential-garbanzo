package org.interview.jorge.playback.server

import android.app.Notification
import android.app.Service
import com.google.android.exoplayer2.ui.PlayerNotificationManager

internal class PlaybackServiceNotificationListener(private val service: Service)
  : PlayerNotificationManager.NotificationListener {
  override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
    if (notificationId == PLAYBACK_SERVICE_FOREGROUND_NOTIFICATION_ID) {
      service.stopSelf()
    }
  }

  override fun onNotificationPosted(
    notificationId: Int,
    notification: Notification,
    ongoing: Boolean
  ) {
    if (notificationId == PLAYBACK_SERVICE_FOREGROUND_NOTIFICATION_ID) {
      service.startForeground(notificationId, notification)
    }
  }
}
