package org.interview.jorge.playback.datasource

internal enum class TestTrack(id: String) {
  TRACK_0("5463901a"),
  TRACK_1("625ea122"),
  TRACK_2("85b03a0f"),
  TRACK_3("aa74e9a1"),
  TRACK_4("c12afbb7"),
  TRACK_5("eda70b22");

  val metadataDocumentUrl = "https://quc-test-source.s3.amazonaws.com/client-test/$id/play"
}
