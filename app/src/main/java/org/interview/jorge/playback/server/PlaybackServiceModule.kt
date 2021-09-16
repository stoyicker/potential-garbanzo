package org.interview.jorge.playback.server

import android.app.Service
import android.content.Context
import android.os.Looper
import com.google.android.exoplayer2.Player
import androidx.core.app.NotificationManagerCompat
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Reusable
import org.interview.jorge.R
import java.util.concurrent.Executors
import javax.inject.Qualifier

@Module
internal abstract class PlaybackServiceModule {
  @Binds
  @Reusable
  @Local
  abstract fun context(service: Service): Context

  companion object {
    @Provides
    @PlaybackServiceComponent.Scoped
    fun player(@Local context: Context): Player = SimpleExoPlayer.Builder(context)
      .setLooper(Looper.getMainLooper())
      .build()

    @Provides
    @Reusable
    fun notificationManagerCompat(@Local context: Context) = NotificationManagerCompat.from(context)

    @Provides
    @Reusable
    @Local
    fun notificationListener(service: Service): PlayerNotificationManager.NotificationListener =
      PlaybackServiceNotificationListener(service)

    @Provides
    @PlaybackServiceComponent.Scoped
    fun playerNotificationManager(
      @Local context: Context,
      @Local notificationListener: PlayerNotificationManager.NotificationListener
    ) = PlayerNotificationManager.Builder(
      context,
      PLAYBACK_SERVICE_FOREGROUND_NOTIFICATION_ID,
      context.getString(R.string.playback_service_notification_channel_id)
    ).setNotificationListener(notificationListener)
      .build().apply {
        setUsePreviousAction(false)
        setUseNextAction(false)
        setUseRewindAction(false)
        setUseFastForwardAction(false)
      }
  }

  @Retention(AnnotationRetention.RUNTIME)
  @Qualifier
  private annotation class Local
}