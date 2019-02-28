package com.ack.familyfootprints;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.ack.familyfootprints.DBHelper.GroupDBHelper;

public class SendInvite extends AppCompatActivity {
    public static boolean canBack = true;
    private ListAdapter mAdapter;
    ListView mListView;
    private static final String TAG = SendInvite.class.getSimpleName();

private static final int REQUEST_INVITE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_invite);
        setToolBar();
        setInviteList();
        CardView InviteBtn = (CardView) findViewById(R.id.InviteBtn);
        InviteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendInvites();
            }
        });
    }

    private void setInviteList() {
        Log.d(TAG, "Populate List");
        GroupDBHelper groupDb_inv = new GroupDBHelper(this);
        Cursor groupCursor_inv = groupDb_inv.getTobeInvitedContacts();
        int row = groupCursor_inv.getCount();
        if (row > 0 ) {
            try {
                if (groupCursor_inv.moveToFirst()) {
                    do {
                        String contactName = groupCursor_inv.getString(groupCursor_inv.getColumnIndex("contactName"));
                        String contactNumber =  groupCursor_inv.getString(groupCursor_inv.getColumnIndex("contactNumber"));

                        Log.i(TAG, "contactName list is " + contactName);
                        Log.i(TAG, "contactNumber is list is" + contactNumber);
                    }while (groupCursor_inv.moveToNext());
                }
            } finally {
                if (groupCursor_inv != null && !groupCursor_inv.isClosed())
                    groupCursor_inv.close();
            }
            Log.i(TAG, "No More Groups Now");
        }
        if (row > 0 ) {
            mAdapter = new SimpleCursorAdapter(this,
                    R.layout.list_view_contactinvite,
                    //android.R.layout.simple_list_item_1,
                    groupCursor_inv,
                    new String[]{"name","invStatus"},
                    new int[]{R.id.contact_name, R.id.invite_status}, 0);
            mListView = (ListView) findViewById(R.id.list_con_invite);
            mListView.setAdapter(mAdapter);
        }
    }
    private void sendInvites() {
       /* Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
                .setCustomImage(Uri.parse(getString(R.string.invitation_custom_image)))
                .setCallToActionText(getString(R.string.invitation_cta))
                .build();*/

               Intent intent = new AppInviteInvitation.IntentBuilder(("TrackBook Invite"))
                .setMessage("Please accept Lucky on Timeline They would access to your locations everyday")
                //.setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
                //.setCustomImage(Uri.parse(getString(R.string.invitation_custom_image)))
                //.setCallToActionText(getString(R.string.invitation_cta))
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
    }
    //Get information back from the App Invites request

    protected  void OnActivityResult (int requestCode, int resultCode, Intent data)
    {
        //this.OnActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);
        if (requestCode == REQUEST_INVITE)
        {
            if ((int)resultCode == RESULT_OK)
            {
                //Check how many invitations were sent. You could optionally track this data as
                //the Ids will be consistent when you receive them
                //String[]  ids = AppInviteInvitation.getInvitationIds((int)resultCode, data);
                Toast.makeText(SendInvite.this, "Invites have been sent", Toast.LENGTH_SHORT).show();
            }
            else
            {
                // Sending failed or it was canceled, show failure message to the user
                Toast.makeText(SendInvite.this, "No Timeline Invites have been sent", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_send_invite, menu);
        return true;
    }
    private void setToolBar() {
        Toolbar actionBarToolBar = (Toolbar) findViewById(R.id.ToolBarView);
        setSupportActionBar(actionBarToolBar);
        //actionBarToolBar.setNavigationIcon(R.drawable.back_arrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(canBack);
        getSupportActionBar().setHomeButtonEnabled(canBack);
        //actionBarToolBar.setLogo(R.drawable.logo);
        //actionBarToolBar.setLogoDescription(getResources().getString(R.string.logo_desc));
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
