package ani.saikou.widgets

import android.content.Intent
import android.widget.RemoteViewsService
import ani.saikou.util.Logger

class CurrentlyAiringRemoteViewsService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        Logger.log("CurrentlyAiringRemoteViewsFactory onGetViewFactory")
        return CurrentlyAiringRemoteViewsFactory(applicationContext, intent)
    }
}
