package org.interview.jorge.playback.server

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.metadata.Metadata
import com.google.android.exoplayer2.source.MediaSourceFactory
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.cache.Cache
import org.interview.jorge.playback.datasource.MediaItemRetriever
import org.interview.jorge.playback.datasource.Stream
import org.interview.jorge.playback.datasource.applehlstest.AppleHlsTestStream
import org.interview.jorge.playback.datasource.applehlstest.HardcodedAppleHlsTestStreamMediaItemRetriever
import java.util.concurrent.Future
import javax.inject.Inject
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

internal class PlaybackService
  : Service(), MediaItemRetriever.MediaItemRequestCallback<AppleHlsTestStream>, Player.Listener {
  @Inject
  @JvmField
  var player: ExoPlayer? = null

  @Inject
  lateinit var playerNotificationManager: PlayerNotificationManager

  @Inject
  lateinit var hardcodedAppleHlsTestStreamMediaItemRetriever: HardcodedAppleHlsTestStreamMediaItemRetriever

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
      hardcodedAppleHlsTestStreamMediaItemRetriever.mediaItemRequestCallback = this
      AppleHlsTestStream.values().forEach { track ->
        mediaItemRetrievalFutures[track] =
          hardcodedAppleHlsTestStreamMediaItemRetriever.retrieveMediaItem(track)
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
    hardcodedAppleHlsTestStreamMediaItemRetriever.mediaItemRequestCallback = null
    playerNotificationManager.setPlayer(null)
    player?.release()
    player = null
    super.onDestroy()
  }

  override fun onMediaItemRetrieved(source: AppleHlsTestStream, mediaItem: MediaItem) {
    mainLooperHandler.post {
      player?.addMediaSource(mediaSourceFactory.createMediaSource(mediaItem))
      // Important that we post the removal of the Future to the UI thread since we also add to and
      // iterate over the map from there, so modifying it from another thread may cause an exception
      // to be thrown
      mediaItemRetrievalFutures.remove(source)
    }
  }

  override fun onMediaItemRetrievalError(source: AppleHlsTestStream, cause: Throwable) {
    Toast.makeText(
      this,
      "MediaItem retrieval error for track ${source.name} - ${cause.javaClass}",
      Toast.LENGTH_SHORT
    ).show()
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

  override fun onMetadata(metadata: Metadata) {
    for (i in 0 until metadata.length()) {
      Log.d(javaClass.name, "onMetadata (i=$i): ${metadata.get(i).toStringByReflection()}")
    }
  }
}

internal const val PLAYBACK_SERVICE_FOREGROUND_NOTIFICATION_ID = 1

private fun Any.toStringByReflection(): String {
  val propsString = this::class.memberProperties
    .joinToString(", ") {
      val wasAccessible = it.isAccessible
      it.isAccessible = true
      val value = it.getter.call(this).toString()
      it.isAccessible = wasAccessible
      "${it.name}=${value}"
    };

  return "${this::class.simpleName} [${propsString}]"
}
