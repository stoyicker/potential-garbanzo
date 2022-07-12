package org.interview.jorge.playback.datasource

import com.google.android.exoplayer2.MediaItem
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future

internal abstract class MediaItemRetriever<T : Stream>(
  private val executorService: ExecutorService
) {
  var mediaItemRequestCallback: MediaItemRequestCallback<T>? = null

  fun retrieveMediaItem(source: T): Future<*> =
    executorService.submit(createRetrievalRunnable(source))

  protected abstract fun createRetrievalRunnable(source: T): Runnable

  interface MediaItemRequestCallback<T> {
    fun onMediaItemRetrieved(source: T, mediaItem: MediaItem)

    fun onMediaItemRetrievalError(source: T, cause: Throwable)
  }
}
