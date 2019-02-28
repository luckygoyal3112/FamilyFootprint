package com.ack.familyfootprints;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import com.ack.familyfootprints.notificationTab.NotificationFragment;
import com.ack.familyfootprints.onboarding.LandingDemoMenu;
import com.ack.familyfootprints.timelineTab.TimelineFragment;
import com.ack.familyfootprints.trackerTab.TrackerFragment;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements
        TrackerFragment.OnFragmentInteractionListener,
        TimelineFragment.OnFragmentInteractionListener,
        NotificationFragment.OnFragmentInteractionListener{

    public static final String TAG = MapsActivity.class.getSimpleName();
    public static boolean canBack = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setToolBar();

        ViewPager viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabTextColors(ContextCompat.getColorStateList(this, R.color.lightText));
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.colorAccent));
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TrackerFragment(), "My Circle");
        adapter.addFragment(new TimelineFragment(), "My Timeline");
        adapter.addFragment(new NotificationFragment(), "Alerts");
        viewPager.setAdapter(adapter);
    }
    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void setToolBar() {
        Toolbar actionBarToolBar = findViewById(R.id.ToolBarView);
        setSupportActionBar(actionBarToolBar);
        actionBarToolBar.setLogo(R.drawable.logo);
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_home_page, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /*case R.id.profile:
                Intent intent = new Intent(MapsActivity.this, ProfileActivity.class);
                startActivity(intent);
                NavUtils.navigateUpFromSameTask(this);
                return true;*/
            case R.id.menu_demo:
                Intent intent = new Intent(MapsActivity.this, LandingDemoMenu.class);
                startActivity(intent);
                //NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.menu_tandc:
                showPrivacySetting();
                return true;
            case R.id.updatePhone:
                Intent intent_updPhone = new Intent(MapsActivity.this, UpdatePhoneNumber.class);
                startActivity(intent_updPhone);
                //NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showPrivacySetting() {
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
