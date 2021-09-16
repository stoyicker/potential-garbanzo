package org.interview.jorge.playback.server

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.util.Log
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.MediaSourceFactory
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import org.interview.jorge.playback.datasource.TestTrack
import org.interview.jorge.playback.datasource.TestTrackMediaItemRetriever
import java.util.concurrent.Future
import javax.inject.Inject

internal class PlaybackService
  : Service(), TestTrackMediaItemRetriever.MediaItemRequestCallback, Player.Listener {
  @Inject
  @JvmField
  var player: ExoPlayer? = null
  @Inject
  lateinit var playerNotificationManager: PlayerNotificationManager
  @Inject
  lateinit var testTrackMetadataRetriever: TestTrackMediaItemRetriever
  @Inject
  lateinit var mainLooperHandler: Handler
  @Inject
  lateinit var mediaSourceFactory: MediaSourceFactory
  private var mediaItemRetrievalFuture: Future<*>? = null

  override fun onCreate() {
    super.onCreate()
    DaggerPlaybackServiceComponent.factory().create(this).inject(this)
    player?.let {
      it.addListener(this)
      playerNotificationManager.setPlayer(it)
      testTrackMetadataRetriever.mediaItemRequestCallback = this
      TestTrack.values().forEach { track ->
        mediaItemRetrievalFuture = testTrackMetadataRetriever.retrieveMediaItem(track)
      }
      it.prepare()
    }
  }

  override fun onBind(intent: Intent?): IBinder? = null

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int) = START_STICKY

  override fun onDestroy() {
    player?.removeListener(this)
    mediaItemRetrievalFuture?.cancel(true)
    testTrackMetadataRetriever.mediaItemRequestCallback = null
    playerNotificationManager.setPlayer(null)
    player?.release()
    player = null
    super.onDestroy()
  }

  override fun onMediaItemRetrieved(mediaItem: MediaItem) {
    mainLooperHandler.post {
      player?.addMediaSource(mediaSourceFactory.createMediaSource(mediaItem))
    }
  }

  override fun onMediaItemRetrievalError(testTrack: TestTrack, cause: Throwable) {
    Log.e(javaClass.name, "MediaItem retrieval error for track ${testTrack.name}", cause)
  }

  override fun onIsPlayingChanged(isPlaying: Boolean) {
    if (!isPlaying && player?.playbackState == Player.STATE_ENDED) {
      stopSelf()
    }
  }
}

internal const val PLAYBACK_SERVICE_FOREGROUND_NOTIFICATION_ID = 1
