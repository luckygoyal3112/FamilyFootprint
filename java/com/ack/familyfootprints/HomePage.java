
package com.ack.familyfootprints;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.ack.familyfootprints.GCMClientApp.QuickstartPreferences;
import com.ack.familyfootprints.onboarding.LandingDemo;
import com.google.android.gms.ads.MobileAds;


public class HomePage extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGTH = 2000;
    private static final String TAG =  HomePage.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        String profileName = sharedPreferences.getString(QuickstartPreferences.PROFILE_NAME, null);
        String profilePhone = sharedPreferences.getString(QuickstartPreferences.PROFILE_PHONE, null);
        MobileAds.initialize(this, "ca-app-pub-7696570925854579~7847542265");
        if (profileName==null || profilePhone==null) {
            setContentView(R.layout.activity_home_page);

            TextView tv2 = findViewById(R.id.buttonOverview);
            Typeface face = Typeface.createFromAsset(getAssets(), "font/GoodDog1.ttf");
            tv2.setTypeface(face);

            Button button1 = findViewById(R.id.buttonOverview);
            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(HomePage.this, LandingDemo.class);
                    startActivity(intent);
                }
            });

            Button button2 = findViewById(R.id.begin);
            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(HomePage.this, ProfilePage.class);
                    startActivity(intent);
                    HomePage.this.finish();
                }
            });
            Button button3 = findViewById(R.id.terms);
            button3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showtermsinservicedialog();
                }
            });
        }else{
            setContentView(R.layout.activity_trans_page);
            new   Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                Intent intent = new Intent(HomePage.this, MainActivity.class);
                startActivity(intent);
                HomePage.this.finish();
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                }
            }, SPLASH_DISPLAY_LENGTH);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds item0s to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_page, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void showtermsinservicedialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("TERMS OF SERVICE");
                builder.setMessage
                ("Footprints provides the feature of inviting your family to join your Footprints circle. Once your invite is accepted, you can view each other's Location Timeline History. Please read our Terms of Service so you understand about your use of Footprints." +
                  "\n\nRegistration: You must register for our Services using accurate data, provide your current mobile phone number. You agree to receive text messages with codes to register for our Services." +
                  "\n\nLocation Timeline: Once you accept the invite of your family member(s) and join their circle on FootPrints, you would be able to view each other's Travel Timeline. "+
                        "Your Location data will not be stored on any other device except for your own mobile device and the mobile devices of the contacts who you invite or accept invitation from. The Location data can be approximate depending on the strength of the signals and sensors. In no condition, will Footprints be responsible for any misuse of the provided data." +
                  "\n\nFees and Taxes. You are responsible for all carrier data plan and other fees and taxes associated with your use of our Services." +
        "\n\nAcceptable use of our services: " +
                  "You must use our Services according to our Terms and posted policies. If we disable your account for a violation of our Terms, you will not create another account without our permission.");

         builder.setPositiveButton("OK", null);

        Dialog terms = builder.create();
        terms.show();
        terms.getWindow().getAttributes();

        TextView textView = (TextView) terms.findViewById(android.R.id.message);
        textView.setTextSize(12);
    }
}
