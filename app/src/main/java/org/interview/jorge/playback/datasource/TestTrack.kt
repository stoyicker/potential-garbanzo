package org.interview.jorge.playback.datasource

internal enum class TestTrack(id: String) {
  TRACK_0("5463901a"), // The greatest song
  TRACK_1("625ea122"), // Another great song
  TRACK_2("85b03a0f"), // My tail appears missing
  TRACK_3("aa74e9a1"), // All good
  TRACK_4("c12afbb7"), // Pointing at something forbidden
  TRACK_5("eda70b22"); // (Description forbidden)

  val metadataDocumentUrl = "https://quc-test-source.s3.amazonaws.com/client-test/$id/play"
}
