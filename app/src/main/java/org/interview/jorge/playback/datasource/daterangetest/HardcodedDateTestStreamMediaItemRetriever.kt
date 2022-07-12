package org.interview.jorge.playback.datasource.daterangetest

import android.net.Uri
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import org.interview.jorge.playback.datasource.MediaItemRetriever
import java.util.concurrent.ExecutorService

internal class HardcodedDateTestStreamMediaItemRetriever(executorService: ExecutorService) :
  MediaItemRetriever<DateRangeTestStream>(executorService) {

  override fun createRetrievalRunnable(source: DateRangeTestStream) = Runnable {
    try {
      mediaItemRequestCallback?.onMediaItemRetrieved(
        source,
        MediaItem.Builder()
          .setTag(source.name)
          .setUri(Uri.parse(source.playlistUrl))
          .setMediaMetadata(MediaMetadata.Builder().setTitle(source.name).build())
          .build()
      )
    } catch (throwable: Throwable) {
      mediaItemRequestCallback?.onMediaItemRetrievalError(source, throwable)
    }
  }
}
