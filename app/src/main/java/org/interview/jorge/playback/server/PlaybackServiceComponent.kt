package org.interview.jorge.playback.server

import android.app.Service
import dagger.BindsInstance
import dagger.Component
import javax.inject.Scope

@Component(modules = [PlaybackServiceModule::class])
@PlaybackServiceComponent.Scoped
internal interface PlaybackServiceComponent {
  fun inject(target: PlaybackService)

  @Component.Factory
  interface Factory {
    fun create(@BindsInstance service: Service): PlaybackServiceComponent
  }

  @Retention(AnnotationRetention.RUNTIME)
  @Scope
  annotation class Scoped
}
