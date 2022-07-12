package org.interview.jorge.playback.datasource.applehlstest

import android.net.Uri
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import org.interview.jorge.playback.datasource.MediaItemRetriever
import java.util.concurrent.ExecutorService

internal class HardcodedAppleHlsTestStreamMediaItemRetriever(executorService: ExecutorService) :
  MediaItemRetriever<AppleHlsTestStream>(executorService) {

  override fun createRetrievalRunnable(source: AppleHlsTestStream) = Runnable {
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
