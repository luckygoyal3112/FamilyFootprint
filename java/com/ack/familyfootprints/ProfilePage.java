package com.ack.familyfootprints;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ack.familyfootprints.DBHelper.otpCntHelper;
import com.ack.familyfootprints.GCMClientApp.Globals;
import com.ack.familyfootprints.GCMClientApp.QuickstartPreferences;
import com.ack.familyfootprints.model.PingerKeys;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Lucky Goyal on 12/23/2016.
 */
public class ProfilePage extends AppCompatActivity {

    public static boolean canBack = true;
    public static final String TAG = ProfilePage.class.getSimpleName();
    private static final int PERMISSION_REQUEST_CODE = 1;
    int rc_1=0;
    int rc_2=0;
    AtomicInteger msgId = new AtomicInteger();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilesettings);
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        setToolBar();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int hasLocationPermission = checkSelfPermission( Manifest.permission.ACCESS_FINE_LOCATION );
           // int hasSMSPermission = checkSelfPermission(Manifest.permission.SEND_SMS);
            int hasContactPermission = checkSelfPermission(Manifest.permission.READ_CONTACTS);
            int hasStoragePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            
            List<String> permissions = new ArrayList<String>();
            if( hasContactPermission != PackageManager.PERMISSION_GRANTED ) {
                permissions.add( Manifest.permission.READ_CONTACTS );
            }

            if( hasLocationPermission != PackageManager.PERMISSION_GRANTED ) {
                permissions.add( Manifest.permission.ACCESS_FINE_LOCATION );
            }

            /*if( hasSMSPermission != PackageManager.PERMISSION_GRANTED ) {
                permissions.add(Manifest.permission.SEND_SMS);
            }*/

            if( hasStoragePermission != PackageManager.PERMISSION_GRANTED ) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }

            if( !permissions.isEmpty() ) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), PERMISSION_REQUEST_CODE);
            }

        }
        final EditText editText_user_name = findViewById(R.id.editText_user_name);

        final EditText editPhone = findViewById(R.id.editPhone);
        Button button_ok =  findViewById(R.id.button_ok);
        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (editText_user_name.getText().toString().equals("") || editPhone.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(),
                            "Please provide your Name and Phone to move past this page.",
                            Toast.LENGTH_SHORT).show();
                } else if (!isValidPhoneNumber(editPhone.getText().toString())) {
                    Toast.makeText(getApplicationContext(),
                            "Please enter valid 10 digit phone number", Toast.LENGTH_SHORT).show();
                } else {
                    validate_phone(view, getApplicationContext(), editPhone.getText().toString(), sharedPreferences, editText_user_name, editPhone);
                }
            }
        });
    }
    private int validate_phone( final View view, final Context context, final String phNo, final SharedPreferences sharedPreferences, final EditText editText_user_name, final EditText editPhone) {
        Random rNo = new Random();
        final int code = rNo.nextInt((99999 - 10000) + 1) + 10000;
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Verify Phone Number");
        builder.setMessage("An sms will be sent to the number " + phNo + " for verification. Charges will apply as per your plan");
        AlertDialog.Builder builder1 = builder;
        builder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        String sms = "Your Timeline OTP Code is " + code;
                            sendSMS(getApplicationContext(), phNo, code, sms);
                        rc_2 = dialog_otplayout(view, phNo, sharedPreferences, editText_user_name, editPhone);
                    }
                }
        );
        builder1.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }
        );
        builder.show();
        return rc_2;
    }
    // Construct a request for phone numbers and show the picker
    private void requestHint() {
        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();

        PendingIntent intent = Auth.CredentialsApi.getHintPickerIntent(
                apiClient, hintRequest);
        startIntentSenderForResult(intent.getIntentSender(),
                RESOLVE_HINT, null, 0, 0, 0);
    }
    private int dialog_otplayout(final View view, final String phNo, final SharedPreferences sharedPreferences, final EditText editText_user_name, final EditText editPhone) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Validate OTP");
        builder.setMessage("Enter OTP Here");
        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for ack, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);
        //builder.setMessage("An sms will be sent to the number " + phNo + " for verification. Charges will apply as per your plan");
        AlertDialog.Builder builder1 = builder;
        builder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    String otp_db = "";
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        //code to send sms here with the code value
                        String otp_user = input.getText().toString();
                        otpCntHelper otpCntHelper = new otpCntHelper(getBaseContext());
                        Cursor otpCursor = otpCntHelper.getPhoneotp(phNo);
                        if (otpCursor != null) {
                            if (otpCursor.moveToFirst()) {
                                otp_db = (otpCursor.getString(otpCursor.getColumnIndex("otp")));
                            }
                        }
                        if (otp_user.equals(otp_db)) {
                            rc_1 = 1;
                            Toast.makeText(ProfilePage.this, "OTP Validated!", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                            //Starting a new activity
                            sharedPreferences.edit().putString(QuickstartPreferences.PROFILE_NAME, editText_user_name.getText().toString()).apply();
                            sharedPreferences.edit().putString(QuickstartPreferences.PROFILE_PHONE, editPhone.getText().toString()).apply();
                            loadNewContacts(editText_user_name.getText().toString(), editPhone.getText().toString());
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            ProfilePage.this.finish();
                        } else {
                            rc_1 = 0;
                            Toast.makeText(ProfilePage.this, "Wrong OTP. Please Try Again", Toast.LENGTH_LONG).show();
                        }
                    }
                }

        );
        builder1.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }
        );
        builder.show();
        return rc_1;
    }
    private void loadNewContacts(String profileName, String profilePhone) {
        Log.w(TAG, "Sending request for loading the New contact " + profileName + profilePhone);
        FirebaseMessaging.getInstance().send(
                new RemoteMessage.Builder(Globals.GCM_SENDER_ID + "@gcm.googleapis.com")
                        .setMessageId(Integer.toString(msgId.incrementAndGet()))
                        .addData(PingerKeys.ACTION, Globals.REGISTER_NEW_CLIENT)
                        .addData(Globals.REGISTRATION_TOKEN, FirebaseInstanceId.getInstance().getToken())
                        .addData(Globals.NAME, profileName)
                        .addData(Globals.MPHONE, profilePhone)
                        .addData(Globals.PICTURE_URL, PingerKeys.DP)
                        .addData(Globals.CONNECTION_STATUS, "NA")
                        .build());
        FirebaseMessaging.getInstance().subscribeToTopic("newclient");
    }
    boolean readSMS(Intent intent, int code) {
        try {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody();
                    if (message.contains(String.valueOf(code)))
                        return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
    public void sendSMS(Context context, String incomingNumber, int code, String sms) {
        //DateTimeFormatter dtfOut = DateTimeFormat.forPattern("YYYY-MM-dd HH:MM:SS");
        SmsManager smsManager = SmsManager.getDefault();                                      //send sms
        try {
            ArrayList<String> parts = smsManager.divideMessage(sms);
            smsManager.sendMultipartTextMessage(incomingNumber, null, parts, null, null);

            otpCntHelper otpCntHelper = new otpCntHelper(context);
            String uts = getTodayTime();
            otpCntHelper.insertRecord( incomingNumber, String.valueOf(code), uts);
            Log.i(TAG, "Sms to be sent is " + sms);
        } catch (Exception e) {
            Toast.makeText(context, "SMS sending failed...",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                android.Manifest.permission.SEND_SMS,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.READ_CONTACTS,}, 101);
    }

    private boolean checkIfAlreadyhaveSMSPermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
    private boolean checkIfAlreadyhaveLocPermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch ( requestCode ) {
            case PERMISSION_REQUEST_CODE: {
                for( int i = 0; i < permissions.length; i++ ) {
                    if( grantResults[i] == PackageManager.PERMISSION_GRANTED ) {
                    } else if( grantResults[i] == PackageManager.PERMISSION_DENIED ) {
                    }
                }
            }
            break;
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }
    private boolean isValidPhoneNumber(CharSequence phoneNumber) {
        if (!TextUtils.isEmpty(phoneNumber)) {
            return Patterns.PHONE.matcher(phoneNumber).matches();
        }
        return false;
    }
    private void setToolBar() {
        Toolbar actionBarToolBar = (Toolbar) findViewById(R.id.ToolBarView);
        TextView mTitle = (TextView) actionBarToolBar.findViewById(R.id.toolbar_title);
        /*mTitle.setTypeface(
                Typer.set(this).getFont(Font.ROBOTO_BOLD));*/
     }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
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
    private static String getTodayTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String uts = dateFormat.format(new Date()); // Find todays date
        return uts;
    }
}