package org.interview.jorge.playback.server

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.media.MediaBrowserServiceCompat

internal class PlaybackService : Service() {
  override fun onBind(ignored: Intent?): IBinder? = null
}
