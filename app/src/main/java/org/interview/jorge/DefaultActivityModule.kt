package org.interview.jorge

import android.content.Context
import android.content.Intent
import com.google.android.exoplayer2.util.Util
import dagger.Module
import dagger.Provides
import dagger.Reusable

@Module
internal object DefaultActivityModule {
  @Provides
  @Reusable
  fun startsForegroundService() = object : StartsForegroundService {
    override fun invoke(context: Context, intent: Intent) {
      Util.startForegroundService(context, intent)
    }
  }
}
