package org.interview.jorge.playback.datasource.tidalinterview

import org.interview.jorge.playback.datasource.Stream

internal enum class TestStream(id: String) : Stream {
  STREAM_0("5463901a"), // The greatest song
  STREAM_1("625ea122"), // Another great song
  STREAM_2("85b03a0f"), // My tail appears missing
  STREAM_3("aa74e9a1"), // All good
  STREAM_4("c12afbb7"), // Pointing at something forbidden
  STREAM_5("eda70b22"); // (Description forbidden)

  val metadataDocumentUrl = "https://quc-test-source.s3.amazonaws.com/client-test/$id/play"
}
