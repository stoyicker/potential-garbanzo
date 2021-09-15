package org.interview.jorge.playback.server

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import javax.inject.Inject

internal class PlaybackService : Service() {
  @Inject
  @JvmField
  var player: Player? = null
  @Inject
  lateinit var playerNotificationManager: PlayerNotificationManager

  override fun onCreate() {
    super.onCreate()
    DaggerPlaybackServiceComponent.factory().create(this).inject(this)
    player?.let {
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
}

internal const val PLAYBACK_SERVICE_FOREGROUND_NOTIFICATION_ID = 1
