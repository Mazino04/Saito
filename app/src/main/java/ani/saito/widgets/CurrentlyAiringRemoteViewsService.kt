package ani.saito.widgets

import android.content.Intent
import android.widget.RemoteViewsService
import ani.saito.util.Logger

class CurrentlyAiringRemoteViewsService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        Logger.log("CurrentlyAiringRemoteViewsFactory onGetViewFactory")
        return CurrentlyAiringRemoteViewsFactory(applicationContext, intent)
    }
}
