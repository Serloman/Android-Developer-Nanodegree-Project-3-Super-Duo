package barqsoft.footballscores.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by Serloman on 09/08/2015.
 */
public class StackWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StackRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

