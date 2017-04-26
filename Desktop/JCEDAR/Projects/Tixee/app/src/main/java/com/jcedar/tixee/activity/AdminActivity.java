package com.jcedar.tixee.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.jcedar.tixee.R;
import com.jcedar.tixee.fragment.CampaignFragment;
import com.jcedar.tixee.fragment.PrimaryFragment;
import com.jcedar.tixee.fragment.TicketFragment;

/**
 * Created by OLUWAPHEMMY on 2/5/2017.
 */

public class AdminActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        Bundle args = getIntent().getExtras();
        String bundle = args.getString("adminCamp");
        int camTickAva = args.getInt("pryUserAva");
        int camTickUsed = args.getInt("pryUserUsed");
        String camTickName = args.getString("pryUserCamName");

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (bundle.equalsIgnoreCase("ADMIN_CAMPAIGN")) {

            if (toolbar != null) {
                toolbar.setTitle("Admin Panel - Campaigns");
                setSupportActionBar(toolbar);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });

            }
            CampaignFragment campaignFragment = new CampaignFragment();
            transaction.replace(R.id.frame, campaignFragment, "Campaign");
            transaction.commit();
        }
        else if (bundle.equalsIgnoreCase("ADMIN_TICKET")) {
            if (toolbar != null) {
            toolbar.setTitle("Admin Panel - Tickets");
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

        }
            TicketFragment fragment = new TicketFragment();
            transaction.replace(R.id.frame, fragment, "Ticket");
            transaction.commit();
        }
        else if (bundle.equalsIgnoreCase("PRIMARY_LAYOUT")) {
            if (toolbar != null) {
            toolbar.setTitle("Tickets");
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

        }
            PrimaryFragment fragment = new PrimaryFragment();
            Bundle bb = new Bundle();
            bb.putString("pryCamName", camTickName);
            bb.putInt("pryCamAva", camTickAva);
            bb.putInt("pryCamUsed", camTickUsed);
            fragment.setArguments(bb);
            transaction.replace(R.id.frame, fragment, "primaryTicket");
            transaction.commit();
        }

    }
}
