package org.interview.jorge.playback.datasource

internal enum class TestTrack(id: String) {
  TRACK_0("5463901a");

  val metadataDocumentUrl = "https://quc-test-source.s3.amazonaws.com/client-test/$id/play"
}
