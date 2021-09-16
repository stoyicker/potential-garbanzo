package org.interview.jorge.playback.server

import android.app.Service
import android.content.Context
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.C.WAKE_MODE_NETWORK
import com.google.android.exoplayer2.RenderersFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.ExtractorsFactory
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.util.NotificationUtil
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Reusable
import org.interview.jorge.R
import javax.inject.Qualifier

@Module
internal abstract class PlaybackServiceModule {
  @Binds
  @Reusable
  @Local
  abstract fun context(service: Service): Context

  companion object {
    @Provides
    @Reusable
    @Local
    fun renderersFactory(@Local context: Context): RenderersFactory =
      MediaCodecAudioRenderersFactory(context)

    @Provides
    @Reusable
    @Local
    fun extractorsFactory(): ExtractorsFactory = ExtractorsFactory.EMPTY

    @Provides
    @PlaybackServiceComponent.Scoped
    fun player(
      @Local context: Context,
      @Local renderersFactory: RenderersFactory,
      @Local extractorsFactory: ExtractorsFactory
    ): Player = SimpleExoPlayer.Builder(context, renderersFactory, extractorsFactory)
      .setLooper(Looper.getMainLooper())
      .setWakeMode(WAKE_MODE_NETWORK)
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
      .setChannelNameResourceId(R.string.playback_service_notification_channel_name)
      .setChannelImportance(NotificationUtil.IMPORTANCE_HIGH)
      .build().apply {
        setUsePreviousAction(false)
        setUseNextAction(false)
        setUseRewindAction(false)
        setUseFastForwardAction(false)
        setPriority(NotificationCompat.PRIORITY_MAX)
      }
  }

  @Retention(AnnotationRetention.RUNTIME)
  @Qualifier
  private annotation class Local
}
