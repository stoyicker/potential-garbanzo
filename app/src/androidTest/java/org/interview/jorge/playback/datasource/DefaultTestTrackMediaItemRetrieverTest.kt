package org.interview.jorge.playback.datasource

import android.net.Uri
import com.google.android.exoplayer2.MediaItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

internal class DefaultTestTrackMediaItemRetrieverTest {
  private val subject = DefaultTestTrackMediaItemRetriever(Executors.newSingleThreadExecutor())

  @Test
  fun retrieveRealMediaItem() {
    // This is a real end-to-end test, which relies on how things look server-side. Because of this,
    // check before actually running the test for a better shot at a relevant result, and skip the
    // test if the assumption is not met
    assumeTrue(getResponseCodeForTestTrack0() == 200)
    val callback = mock(TestTrackMediaItemRetriever.MediaItemRequestCallback::class.java)
    subject.mediaItemRequestCallback = callback
    val testTrack = TestTrack.TRACK_0

    subject.retrieveMediaItem(testTrack).get()

    // https://stackoverflow.com/questions/52389727/mockitos-argthat-returning-null-when-in-kotlin
    // Prettier resolution possible (e.g. with other libraries), but not the focus
    val arguments = Mockito.mockingDetails(callback).invocations.single {
      it.method.name.contentEquals("onMediaItemRetrieved")
    }.arguments
    assertSame(testTrack, arguments[0])
    val mediaItem = arguments[1] as MediaItem
    assertEquals("5463901a", mediaItem.playbackProperties!!.tag)
    assertEquals("The greatest song", mediaItem.mediaMetadata.title)
    assertEquals(
      Uri.parse("https://quc-test-source.s3.amazonaws.com/client-test/5463901a/playlist.m3u8"),
      mediaItem.playbackProperties!!.uri
    )
  }

  private fun getResponseCodeForTestTrack0(): Int {
    val urlConnection = URL(
      "https://quc-test-source.s3.amazonaws.com/client-test/5463901a/play"
    ).openConnection() as HttpURLConnection
    try {
      return urlConnection.responseCode
    } finally {
      urlConnection.disconnect()
    }
  }
}
