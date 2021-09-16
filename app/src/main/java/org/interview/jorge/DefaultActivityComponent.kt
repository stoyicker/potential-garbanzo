package org.interview.jorge

import dagger.Component

@Component(modules = [DefaultActivityModule::class])
internal interface DefaultActivityComponent {
  fun inject(target: DefaultActivity)
}
