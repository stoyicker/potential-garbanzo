package org.interview.jorge

import android.content.Context
import android.content.Intent

internal interface StartsForegroundService : (Context, Intent) -> Unit {
  override operator fun invoke(context: Context, intent: Intent)
}
