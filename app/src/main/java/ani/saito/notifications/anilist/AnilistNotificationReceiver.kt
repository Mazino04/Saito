package ani.saito.notifications.anilist

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ani.saito.notifications.AlarmManagerScheduler
import ani.saito.notifications.TaskScheduler
import ani.saito.settings.saving.PrefManager
import ani.saito.settings.saving.PrefName
import ani.saito.util.Logger
import kotlinx.coroutines.runBlocking

class AnilistNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Logger.log("AnilistNotificationReceiver: onReceive")
        runBlocking {
            AnilistNotificationTask().execute(context)
        }
        val anilistInterval =
            AnilistNotificationWorker.checkIntervals[PrefManager.getVal(PrefName.AnilistNotificationInterval)]
        AlarmManagerScheduler(context).scheduleRepeatingTask(
            TaskScheduler.TaskType.ANILIST_NOTIFICATION,
            anilistInterval
        )
    }
}