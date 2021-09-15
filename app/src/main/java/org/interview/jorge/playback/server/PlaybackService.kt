package org.interview.jorge.playback.server

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import org.interview.jorge.R
import javax.inject.Inject

internal class PlaybackService : Service() {
  @Inject
  @JvmField
  var player: Player? = null
  @Inject
  lateinit var notificationManagerCompat: NotificationManagerCompat
  @Inject
  lateinit var playerNotificationManager: PlayerNotificationManager

  override fun onCreate() {
    super.onCreate()
    DaggerPlaybackServiceComponent.factory().create(this).inject(this)
    player?.let {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        createNotificationChannel()
      }
      playerNotificationManager.setPlayer(it)
      it.prepare()
    }
  }

  override fun onBind(intent: Intent?): IBinder? = null

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int) = START_STICKY

  override fun onDestroy() {
    playerNotificationManager.setPlayer(null)
    player?.release()
    player = null
    super.onDestroy()
  }

  @RequiresApi(Build.VERSION_CODES.O)
  private fun createNotificationChannel() {
    notificationManagerCompat
      .createNotificationChannel(NotificationChannel(
        getString(R.string.playback_service_notification_channel_id),
        getString(R.string.playback_service_notification_channel_name),
        NotificationManager.IMPORTANCE_DEFAULT
      ))
  }
}

internal const val PLAYBACK_SERVICE_FOREGROUND_NOTIFICATION_ID = 1
