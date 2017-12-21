package uk.me.asbridge.calendarcountdown;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by AsbridgeD on 19-Dec-17.
 */

public class MyWidgetRemoteViewsService extends RemoteViewsService {

    private static final String TAG = LogHelper.makeLogTag(MyWidgetRemoteViewsService.class);

    @Override
    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
        LogHelper.i(TAG, "onGetViewFactory");
        return new MyWidgetRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}