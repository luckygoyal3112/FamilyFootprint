package com.ack.familyfootprints.timelineTab;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class postBootServiceStarter extends BroadcastReceiver {
    private static final String TAG = postBootServiceStarter.class.getSimpleName();
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Inside postBootServiceStarter");
            if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
                Log.d(TAG, "About to call TrackbookLocService");
//                TrackbookLocService.enqueueWork(context, new Intent());
                Intent pushIntent = new Intent(context, TrackbookLocService.class);
                context.startService(pushIntent);
            }
        }
}
