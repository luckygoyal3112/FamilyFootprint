package com.ack.familyfootprints.trackerTab;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.ack.familyfootprints.GCMClientApp.FriendlyPingUtil;
import com.ack.familyfootprints.GCMClientApp.GcmAction;
import com.ack.familyfootprints.R;
import com.ack.familyfootprints.model.PingerKeys;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

public class SendInvite extends AppCompatActivity {
    public static boolean canBack = true;
    private ListAdapter mAdapter;
    ListView mListView;
    private static final String TAG = SendInvite.class.getSimpleName();
    private SharedPreferences mDefaultSharedPreferences;

private static final int REQUEST_INVITE = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        
        String name = intent.getStringExtra("name");
        String phone = intent.getStringExtra("phone");
        String regId = intent.getStringExtra("regId");
        Log.i(TAG, "contactName is " + name);
        Log.i(TAG, "contactNumber is" + phone);
        Log.i(TAG, "regid is" + regId);
        mDefaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        //setToolBar();
        inviteContact(name, phone, regId);
        /*
        CardView InviteBtn = (CardView) findViewById(R.id.InviteBtn);4
        4
        InviteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendInvites();
            }
        });k
        */
    }
    private void inviteContact(String name, String phone, String regId)
    {
        Bundle data = new Bundle();
        data.putString(PingerKeys.ACTION, GcmAction.INVITE_REQUEST);
        data.putString(PingerKeys.TO, regId);
        data.putString(PingerKeys.SENDER,
                mDefaultSharedPreferences.getString(GcmAction.TOKEN, null));
        try {
            Log.w(TAG, "Sending FootPrints Invite to ."+ name);
            GoogleCloudMessaging.getInstance(this)
                    .send(FriendlyPingUtil.getServerUrl(this),
                            String.valueOf(System.currentTimeMillis()), data);
            //AnalyticsHelper.send(context, TrackingEvent.PING_SENT);
        } catch (IOException e) {
            Log.w(TAG, "Could not ping client.", e);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_send_invite, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
