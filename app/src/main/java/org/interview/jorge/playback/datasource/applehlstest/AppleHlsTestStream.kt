package org.interview.jorge.playback.datasource.applehlstest

import org.interview.jorge.playback.datasource.Stream

/**
 * @see <a href=https://developer.apple.com/streaming/examples/>Apple HLS examples</a>
 */
internal enum class AppleHlsTestStream(id: CharSequence) : Stream {
  BIPBOP_16x9("bipbop_16x9");

  val playlistUrl = "https://devstreaming-cdn.apple.com/videos/streaming/examples/$id/${id}_variant.m3u8"
}
