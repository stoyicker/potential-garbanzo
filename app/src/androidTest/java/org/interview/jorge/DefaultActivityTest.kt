package org.interview.jorge

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import dagger.BindsInstance
import dagger.Component
import org.interview.jorge.playback.server.PlaybackService
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockingDetails

internal class DefaultActivityTest {
  /**
   * This could be lateinit, but if a test crashes before initializing the field, the exception
   * reported will be the one from trying to call close() on the uninitialized field and not the
   * actual cause of the issue. Making the field nullable instead tackles this problem
   */
  private var activityScenario: ActivityScenario<DefaultActivity>? = null
  private lateinit var defaultComponentF: () -> DefaultActivityComponent
  private val startsForegroundService = mock(StartsForegroundService::class.java)

  @Before
  fun beforeEach() {
    defaultComponentF = DefaultActivity.componentF
  }

  @After
  fun afterEach() {
    DefaultActivity.componentF = defaultComponentF
    activityScenario?.close()
  }

  @Test
  fun starts_playbackService() {
    DefaultActivity.componentF = {
      DaggerDefaultActivityTest_ReplacementDefaultActivityComponent.factory()
        .create(startsForegroundService)
    }

    activityScenario = ActivityScenario.launch(DefaultActivity::class.java)

    // https://stackoverflow.com/questions/52389727/mockitos-argthat-returning-null-when-in-kotlin
    // Prettier resolution possible (e.g. with other libraries), but not the focus
    val invocation = mockingDetails(startsForegroundService).invocations.single {
      it.method.name.contentEquals("invoke")
    }
    assertTrue(invocation.arguments[0] != null)
    assertTrue(
      (invocation.arguments[1] as Intent).component!!.className.contentEquals(
        PlaybackService::class.java.name
      )
    )
  }

  @Component
  interface ReplacementDefaultActivityComponent : DefaultActivityComponent {
    @Component.Factory
    interface Factory {
      fun create(
        @BindsInstance startsForegroundService: StartsForegroundService
      ): ReplacementDefaultActivityComponent
    }
  }
}
