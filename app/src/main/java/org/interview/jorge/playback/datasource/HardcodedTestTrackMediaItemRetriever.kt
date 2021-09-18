package org.interview.jorge.playback.datasource

import android.net.Uri
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import java.util.concurrent.ExecutorService

/**
 * A [TestTrackMediaItemRetriever] that creates the result by using the item description hardcoded
 * as copy-pasted from requests performed manually. This allows the app to add all the items that it
 * normally would by downloading their info from the Internet without Internet connection, which
 * comes in handy when trying to test offline playback by starting the app while offline, given that
 * loading said items is a single-attempt operation.
 */
internal class HardcodedTestTrackMediaItemRetriever(executorService: ExecutorService) :
  TestTrackMediaItemRetriever(executorService) {

  override fun createRetrievalRunnable(testTrack: TestTrack) = Runnable {
    try {
      mediaItemRequestCallback?.onMediaItemRetrieved(
        testTrack, HardcodedTestTrack.values()[TestTrack.values().indexOf(testTrack)].asMediaItem()
      )
    } catch (throwable: Throwable) {
      mediaItemRequestCallback?.onMediaItemRetrievalError(testTrack, throwable)
    }
  }

  enum class HardcodedTestTrack(private val id: String, private val title: String) {
    TRACK_0("5463901a", "The greatest song"),
    TRACK_1("625ea122", "Another great song"),
    TRACK_2("85b03a0f", "My tail appears missing"),
    TRACK_3("aa74e9a1", "All good"),
    TRACK_4("c12afbb7", "Pointing at something forbidden"),
    TRACK_5("eda70b22", "(Description forbidden)") {
      override fun asMediaItem() = throw Error("The real item descriptor throws 403")
    };

    open fun asMediaItem() = MediaItem.Builder()
      .setTag(id)
      .setUri(Uri.parse("https://quc-test-source.s3.amazonaws.com/client-test/$id/playlist.m3u8"))
      .setMediaMetadata(MediaMetadata.Builder().setTitle(title).build())
      .build()
  }
}
