package org.interview.jorge.playback.datasource

import com.google.android.exoplayer2.MediaItem
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future

internal abstract class TestTrackMediaItemRetriever(private val executorService: ExecutorService) {
  var mediaItemRequestCallback: MediaItemRequestCallback? = null

  fun retrieveMediaItem(testTrack: TestTrack): Future<*> =
    executorService.submit(createRetrievalRunnable(testTrack))

  protected abstract fun createRetrievalRunnable(testTrack: TestTrack): Runnable

  interface MediaItemRequestCallback {
    fun onMediaItemRetrieved(mediaItem: MediaItem)

    fun onMediaItemRetrievalError(testTrack: TestTrack, cause: Throwable)
  }
}
