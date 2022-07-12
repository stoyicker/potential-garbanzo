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
import org.interview.jorge.playback.datasource.tidalinterview.TestStream
import org.interview.jorge.playback.datasource.MediaItemRetriever
import org.interview.jorge.playback.datasource.Stream
import org.interview.jorge.playback.datasource.tidalinterview.ActualTestStreamMediaItemRetriever
import org.interview.jorge.playback.datasource.tidalinterview.HardcodedTestStreamMediaItemRetriever
import java.util.concurrent.Future
import javax.inject.Inject

internal class PlaybackService
  : Service(), MediaItemRetriever.MediaItemRequestCallback<TestStream>, Player.Listener {
  @Inject
  @JvmField
  var player: ExoPlayer? = null

  @Inject
  lateinit var playerNotificationManager: PlayerNotificationManager

  @Inject
  lateinit var mediaItemRetriever: ActualTestStreamMediaItemRetriever

  @Inject
  lateinit var hardcodedTestStreamMediaItemRetriever: HardcodedTestStreamMediaItemRetriever

  @Inject
  lateinit var mainLooperHandler: Handler

  @Inject
  lateinit var mediaSourceFactory: MediaSourceFactory

  @Inject
  lateinit var cache: Cache
  private val mediaItemRetrievalFutures = mutableMapOf<Stream, Future<*>>()

  override fun onCreate() {
    super.onCreate()
    DaggerPlaybackServiceComponent.factory().create(this).inject(this)
    player?.let {
      it.addListener(this)
      playerNotificationManager.setPlayer(it)
      mediaItemRetriever.mediaItemRequestCallback = this
      hardcodedTestStreamMediaItemRetriever.mediaItemRequestCallback = this
      TestStream.values().forEach { track ->
        mediaItemRetrievalFutures[track] = mediaItemRetriever.retrieveMediaItem(track)
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
    mediaItemRetriever.mediaItemRequestCallback = null
    hardcodedTestStreamMediaItemRetriever.mediaItemRequestCallback = null
    playerNotificationManager.setPlayer(null)
    player?.release()
    player = null
    super.onDestroy()
  }

  override fun onMediaItemRetrieved(source: TestStream, mediaItem: MediaItem) {
    mainLooperHandler.post {
      player?.addMediaSource(mediaSourceFactory.createMediaSource(mediaItem))
      // Important that we post the removal of the Future to the UI thread since we also add to and
      // iterate over the map from there, so modifying it from another thread may cause an exception
      // to be thrown
      mediaItemRetrievalFutures.remove(source)
    }
  }

  override fun onMediaItemRetrievalError(source: TestStream, cause: Throwable) {
    Log.e(javaClass.name, "MediaItem retrieval error for track ${source.name}", cause)
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
