package org.interview.jorge.playback.server

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.VisibleForTesting
import androidx.core.app.NotificationCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ShuffleOrder
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import dagger.Lazy
import org.interview.jorge.R
import org.interview.jorge.playback.datasource.tidalinterview.HardcodedTestStreamMediaItemRetriever
import org.interview.jorge.playback.datasource.tidalinterview.TestStream
import java.util.Collections

internal class PlaybackServiceCustomActionReceiver(
  private val hardcodedTestStreamMediaItemRetrieverLazy: Lazy<HardcodedTestStreamMediaItemRetriever>
) : PlayerNotificationManager.CustomActionReceiver {
  private var hasRequestedHardcodedMediaItems = false

  @SuppressLint("InlinedApi") // FLAG_IMMUTABLE is a constant inlined at compile time
  override fun createCustomActions(
    context: Context,
    instanceId: Int
  ) = mapOf(
    CUSTOM_ACTION_SHUFFLE to NotificationCompat.Action.Builder(
      R.drawable.ic_shuffle,
      context.getString(R.string.playback_service_custom_command_title_shuffle),
      PendingIntent.getBroadcast(
        context,
        0,
        Intent(CUSTOM_ACTION_SHUFFLE).setPackage(context.packageName),
        PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
      )
    ).build(),
    CUSTOM_ACTION_ADD_HARDCODED_MEDIA_ITEMS to NotificationCompat.Action.Builder(
      R.drawable.ic_save,
      context.getString(R.string.playback_service_custom_command_title_add_hardcoded_media_items),
      PendingIntent.getBroadcast(
        context,
        0,
        Intent(CUSTOM_ACTION_ADD_HARDCODED_MEDIA_ITEMS).setPackage(context.packageName),
        PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
      )
    ).build()
  )

  override fun getCustomActions(player: Player): List<String> {
    val mutableList = mutableListOf<String>()
    if (player is ExoPlayer && player.mediaItemCount > 1) {
      mutableList.add(CUSTOM_ACTION_SHUFFLE)
    }
    if (!hasRequestedHardcodedMediaItems && player.mediaItemCount == 0) {
      mutableList.add(CUSTOM_ACTION_ADD_HARDCODED_MEDIA_ITEMS)
    }
    return Collections.unmodifiableList(mutableList)
  }

  override fun onCustomAction(player: Player, action: String, intent: Intent) {
    (player as? ExoPlayer)?.apply {
      when (action) {
        CUSTOM_ACTION_SHUFFLE -> {
          if (!shuffleModeEnabled) {
            shuffleModeEnabled = true
          }
          setShuffleOrder(ShuffleOrder.DefaultShuffleOrder(mediaItemCount))
        }
        CUSTOM_ACTION_ADD_HARDCODED_MEDIA_ITEMS -> {
          hasRequestedHardcodedMediaItems = true
          TestStream.values().forEach {
            // The future is ignored, but it has no side-effects, it's trivial and the callback
            // is cleared by the service, so it doesn't really matter
            hardcodedTestStreamMediaItemRetrieverLazy.get().retrieveMediaItem(it)
          }
        }
        else -> throw IllegalArgumentException("Unsupported custom action $action")
      }
    }
  }

  companion object {
    @VisibleForTesting
    const val CUSTOM_ACTION_SHUFFLE = "CUSTOM_ACTION_SHUFFLE"
    private const val CUSTOM_ACTION_ADD_HARDCODED_MEDIA_ITEMS =
      "CUSTOM_ACTION_ADD_HARDCODED_MEDIA_ITEMS"
  }
}
