package org.interview.jorge.playback.server

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.util.Log
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.MediaSourceFactory
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.cache.Cache
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
  lateinit var testTrackMediaItemRetriever: TestTrackMediaItemRetriever

  @Inject
  @PlaybackServiceModule.Hardcoded
  lateinit var hardcodedTestTrackMediaItemRetriever: TestTrackMediaItemRetriever

  @Inject
  lateinit var mainLooperHandler: Handler

  @Inject
  lateinit var mediaSourceFactory: MediaSourceFactory

  @Inject
  lateinit var cache: Cache
  private val mediaItemRetrievalFutures = mutableMapOf<TestTrack, Future<*>>()

  override fun onCreate() {
    super.onCreate()
    DaggerPlaybackServiceComponent.factory().create(this).inject(this)
    player?.let {
      it.addListener(this)
      playerNotificationManager.setPlayer(it)
      testTrackMediaItemRetriever.mediaItemRequestCallback = this
      hardcodedTestTrackMediaItemRetriever.mediaItemRequestCallback = this
      TestTrack.values().forEach { track ->
        mediaItemRetrievalFutures[track] = testTrackMediaItemRetriever.retrieveMediaItem(track)
      }
      it.prepare()
    }
  }

  override fun onBind(intent: Intent?): IBinder? = null

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int) = START_STICKY

  override fun onDestroy() {
    cache.release()
    player?.removeListener(this)
    mediaItemRetrievalFutures.forEach { it.value.cancel(true) }
    testTrackMediaItemRetriever.mediaItemRequestCallback = null
    hardcodedTestTrackMediaItemRetriever.mediaItemRequestCallback = null
    playerNotificationManager.setPlayer(null)
    player?.release()
    player = null
    super.onDestroy()
  }

  override fun onMediaItemRetrieved(testTrack: TestTrack, mediaItem: MediaItem) {
    mainLooperHandler.post {
      player?.addMediaSource(mediaSourceFactory.createMediaSource(mediaItem))
      // Important that we post the removal of the Future to the UI thread since we also add to and
      // iterate over the map from there, so modifying it from another thread may cause an exception
      // to be thrown
      mediaItemRetrievalFutures.remove(testTrack)
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

  override fun onPlayerError(error: PlaybackException) {
    player?.apply {
      if (hasNextWindow()) {
        seekToNextWindow()
        prepare()
        play()
      } else {
        stopSelf()
      }
    }
  }
}

internal const val PLAYBACK_SERVICE_FOREGROUND_NOTIFICATION_ID = 1
