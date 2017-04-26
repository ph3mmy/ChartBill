package com.jcedar.chartbills.activity;


import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.jcedar.chartbills.R;
import com.jcedar.chartbills.fragment.SettingsFragment;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.my_gray));
//        setupActionBar(toolbar);

        RelativeLayout comBannerAd = (RelativeLayout) findViewById(R.id.banner_AdView);

        AdView adView  = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(getResources().getString(R.string.ad_id_banner));
        comBannerAd.addView(adView);

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("FEC40D63F8CC61844CD940D15D76DEE6")
                .build();
        adView.loadAd(adRequest);

        //Display the fragment as the main content
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container_settings,  new SettingsFragment())
                    .commit();
        }
//        setupActionBar(toolbar);
    }

}
