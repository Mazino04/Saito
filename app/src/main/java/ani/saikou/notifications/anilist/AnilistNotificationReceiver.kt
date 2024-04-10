package ani.saikou.notifications.anilist

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ani.saikou.notifications.AlarmManagerScheduler
import ani.saikou.notifications.TaskScheduler
import ani.saikou.settings.saving.PrefManager
import ani.saikou.settings.saving.PrefName
import ani.saikou.util.Logger
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