package org.interview.jorge

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.annotation.RestrictTo
import org.interview.jorge.playback.server.PlaybackService
import javax.inject.Inject

internal class DefaultActivity : Activity() {
  @Inject
  lateinit var startsForegroundService: StartsForegroundService

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    componentF().inject(this)
    startsForegroundService(this, Intent(this, PlaybackService::class.java))
    finish()
    overridePendingTransition(0, 0)
  }

  companion object {
    @set:RestrictTo(RestrictTo.Scope.TESTS)
    @JvmStatic
    var componentF = { DaggerDefaultActivityComponent.create() }
  }
}
