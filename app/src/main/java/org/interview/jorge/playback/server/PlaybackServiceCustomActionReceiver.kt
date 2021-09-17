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
import org.interview.jorge.R

internal class PlaybackServiceCustomActionReceiver
  : PlayerNotificationManager.CustomActionReceiver {
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
    ).build()
  )

  override fun getCustomActions(player: Player): List<String> {
    if (player !is ExoPlayer) {
      return emptyList()
    }
    return listOf(CUSTOM_ACTION_SHUFFLE)
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
        else -> throw IllegalArgumentException("Unsupported custom action $action")
      }
    }
  }

  companion object {
    @VisibleForTesting
    const val CUSTOM_ACTION_SHUFFLE = "CUSTOM_ACTION_SHUFFLE"
  }
}
