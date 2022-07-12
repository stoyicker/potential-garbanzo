package org.interview.jorge.playback.datasource.tidalinterview

import android.net.Uri
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import org.interview.jorge.playback.datasource.MediaItemRetriever
import java.util.concurrent.ExecutorService

/**
 * A [MediaItemRetriever] for [TestStream] that creates the result by using the item description
 * hardcoded as copy-pasted from requests performed manually. This allows the app to add all the
 * items that it normally would by downloading their info from the Internet without Internet
 * connection, which comes in handy when trying to test offline playback by starting the app while
 * offline, given that loading said items is a single-attempt operation.
 */
internal class HardcodedTestStreamMediaItemRetriever(executorService: ExecutorService) :
  MediaItemRetriever<TestStream>(executorService) {

  override fun createRetrievalRunnable(source: TestStream) = Runnable {
    try {
      mediaItemRequestCallback?.onMediaItemRetrieved(
        source, HardcodedTestStream.values()[TestStream.values().indexOf(source)].asMediaItem()
      )
    } catch (throwable: Throwable) {
      mediaItemRequestCallback?.onMediaItemRetrievalError(source, throwable)
    }
  }

  enum class HardcodedTestStream(private val id: String, private val title: String) {
    STREAM_0("5463901a", "The greatest song"),
    STREAM_1("625ea122", "Another great song"),
    STREAM_2("85b03a0f", "My tail appears missing"),
    STREAM_3("aa74e9a1", "All good"),
    STREAM_4("c12afbb7", "Pointing at something forbidden"),
    STREAM_5("eda70b22", "(Description forbidden)") {
      override fun asMediaItem() = throw Error("The real item descriptor throws 403")
    };

    open fun asMediaItem() = MediaItem.Builder()
      .setTag(id)
      .setUri(Uri.parse("https://quc-test-source.s3.amazonaws.com/client-test/$id/playlist.m3u8"))
      .setMediaMetadata(MediaMetadata.Builder().setTitle(title).build())
      .build()
  }
}
