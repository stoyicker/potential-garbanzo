package org.interview.jorge.playback.datasource

import android.net.Uri
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import org.json.JSONObject
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future

/**
 * Class that retrieves info for a [MediaItem] from the provided metadata document uri. Could be
 * prettier if we used coroutines, RxJava, OkHttp, Retrofit, a more powerful JSON deserializer
 * or whatever else, but I didn't feel like this was a part of the test relevant enough to invest
 * more time on it.
 *
 * The submitted [Runnable] checks for a thread interrupt before slow operations to try and avoid
 * them if they're not necessary. It's very much like checking if the callback is set, only that
 * this way we support the scenario where the [Runnable] is not started if [Future.cancel] is called
 * early enough.
 */
internal class DefaultTestTrackMediaItemRetriever(executorService: ExecutorService) :
  TestTrackMediaItemRetriever(executorService) {
  override fun createRetrievalRunnable(testTrack: TestTrack) = object : Runnable {
    override fun run() {
      try {
        if (Thread.interrupted()) {
          return
        }
        val urlConnection =
          URL(testTrack.metadataDocumentUrl).openConnection() as HttpURLConnection
        try {
          if (Thread.interrupted()) {
            return
          }
          val metadata = BufferedReader(urlConnection.inputStream.reader()).use { it.readText() }
          val jsonObject = JSONObject(metadata)
          val mediaItem = MediaItem.Builder()
            .setTag(jsonObject.getString(METADATA_DOCUMENT_KEY_ID))
            .setUri(Uri.parse(jsonObject.getString(METADATA_DOCUMENT_KEY_MEDIA_URI)))
            .setMediaMetadata(
              MediaMetadata.Builder()
                .setTitle(jsonObject.getString(METADATA_DOCUMENT_KEY_TITLE))
                .build()
            )
            .build()
          mediaItemRequestCallback?.onMediaItemRetrieved(mediaItem)
        } finally {
          urlConnection.disconnect()
        }
      } catch (throwable: Throwable) {
        mediaItemRequestCallback?.onMediaItemRetrievalError(testTrack, throwable)
      }
    }
  }

  companion object {
    private const val METADATA_DOCUMENT_KEY_ID = "id"
    private const val METADATA_DOCUMENT_KEY_TITLE = "title"
    private const val METADATA_DOCUMENT_KEY_MEDIA_URI = "url"
  }
}
