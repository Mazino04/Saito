package ani.saito.notifications.subscription

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ani.saito.notifications.AlarmManagerScheduler
import ani.saito.notifications.TaskScheduler
import ani.saito.settings.saving.PrefManager
import ani.saito.settings.saving.PrefName
import ani.saito.util.Logger
import kotlinx.coroutines.runBlocking

class SubscriptionNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Logger.log("SubscriptionNotificationReceiver: onReceive")
        runBlocking {
            SubscriptionNotificationTask().execute(context)
        }
        val subscriptionInterval =
            SubscriptionNotificationWorker.checkIntervals[PrefManager.getVal(PrefName.SubscriptionNotificationInterval)]
        AlarmManagerScheduler(context).scheduleRepeatingTask(
            TaskScheduler.TaskType.SUBSCRIPTION_NOTIFICATION,
            subscriptionInterval
        )
    }
}