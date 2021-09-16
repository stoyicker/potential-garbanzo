package org.interview.jorge.playback.server

import android.app.Service
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.exoplayer2.C.WAKE_MODE_NETWORK
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.RenderersFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.ExtractorsFactory
import com.google.android.exoplayer2.source.MediaSourceFactory
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.NotificationUtil
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Reusable
import org.interview.jorge.R
import org.interview.jorge.playback.datasource.TestTrackMediaItemRetriever
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
    @Reusable
    @Local
    fun mainLooper(): Looper = Looper.getMainLooper()

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
    @Reusable
    @Local
    fun trackSelector(@Local context: Context): TrackSelector = DefaultTrackSelector(context)

    @Provides
    @PlaybackServiceComponent.Scoped
    fun player(
      @Local context: Context,
      @Local mainLooper: Looper,
      @Local renderersFactory: RenderersFactory,
      @Local extractorsFactory: ExtractorsFactory,
      @Local trackSelector: TrackSelector
    ): ExoPlayer = SimpleExoPlayer.Builder(context, renderersFactory, extractorsFactory)
      .setLooper(mainLooper)
      .setTrackSelector(trackSelector)
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
        setUsePreviousAction(true)
        setUseNextAction(true)
        setUseRewindAction(false)
        setUseFastForwardAction(false)
        setPriority(NotificationCompat.PRIORITY_MAX)
      }

    @Provides
    @PlaybackServiceComponent.Scoped
    fun testTrackMediaItemRetriever() = TestTrackMediaItemRetriever(
      Executors.newSingleThreadExecutor()
    )

    @Provides
    @Reusable
    fun mainLooperHandler(@Local mainLooper: Looper) = Handler(mainLooper)

    @Provides
    @Reusable
    fun mediaSourceFactory(): MediaSourceFactory =
      HlsMediaSource.Factory(DefaultHttpDataSource.Factory())
  }

  @Retention(AnnotationRetention.RUNTIME)
  @Qualifier
  private annotation class Local
}
