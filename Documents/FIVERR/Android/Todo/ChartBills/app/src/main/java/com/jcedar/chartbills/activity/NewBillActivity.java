package com.jcedar.chartbills.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.jcedar.chartbills.R;
import com.jcedar.chartbills.fragment.NewBillFragment;
import com.jcedar.chartbills.helper.FormatUtils;
import com.jcedar.chartbills.helper.LinearLayoutManagerWrapper;
import com.jcedar.chartbills.model.Bill;
import com.jcedar.chartbills.provider.ChartBillContract;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

/**
 * Created by OLUWAPHEMMY on 3/22/2017.
 */

public class NewBillActivity extends AppCompatActivity {

    private static final String TAG = NewBillActivity.class.getSimpleName();
    SectionedRecyclerViewAdapter sectionAdapter;
    RecyclerView recyclerView;
    TextView errorMsg;
    String time;
    private static final String UPCOMING_TAG = "upcoming";
    private static final String OVERDUE_TAG = "overdue";
    private static final String PAID_TAG = "paid";

    Toolbar toolbar;
    private AdView mBannerAd;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(NewBillActivity.this, SettingsActivity.class));
            return true;
        }
        if (id == R.id.action_date_filter) {
            popUp();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void popUp() {
        View v  = findViewById(R.id.action_date_filter);
        PopupMenu pop = new PopupMenu(NewBillActivity.this, v);
        pop.getMenuInflater().inflate(R.menu.menu_date_filter, pop.getMenu());
        pop.show();
        pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_last_month:
                        toolbar.setTitle("Last Month");
                        time = "last_month";
//                        String time4 = "last_month";
                        sectionAdapter.removeAllSections();
                        sectionAdapter.addSection(new BillSection(BillSection.UPCOMING, time));
                        sectionAdapter.addSection(new BillSection(BillSection.OVERDUE, time));
                        sectionAdapter.addSection(new BillSection(BillSection.PAID, time));
                        sectionAdapter.notifyDataSetChanged();
//                        startActivity(new Intent(NewBillActivity.this, AndroidDatabaseManager.class));

//                        getSupportLoaderManager().restartLoader(LAST_MONTH_LOADER, null, MainActivity.this);

                        break;
                    case R.id.action_next_month:
                        toolbar.setTitle("Next Month");
                        time = "next_month";

                        sectionAdapter.removeAllSections();
                        sectionAdapter.addSection(new BillSection(BillSection.UPCOMING, time));
                        sectionAdapter.addSection(new BillSection(BillSection.OVERDUE, time));
                        sectionAdapter.addSection(new BillSection(BillSection.PAID, time));
                        sectionAdapter.notifyDataSetChanged();

//                        getSupportLoaderManager().restartLoader(NEXT_MONTH_LOADER, null, MainActivity.this);

                        break;
                    case R.id.action_this_month:
                        toolbar.setTitle("This Month");

                        time = "this_month";
//                        String time1 = "this_month";
                        sectionAdapter.removeAllSections();
                        sectionAdapter.addSection(new BillSection(BillSection.UPCOMING, time));
                        sectionAdapter.addSection(new BillSection(BillSection.OVERDUE, time));
                        sectionAdapter.addSection(new BillSection(BillSection.PAID, time));
                        sectionAdapter.notifyDataSetChanged();
//                        getSupportLoaderManager().restartLoader(THIS_MONTH_LOADER, null, MainActivity.this);
                        break;
                    case R.id.action_this_week:
                        toolbar.setTitle("This Week");
                        time= "this_week";
//                        String time2 = "this_week";
                        sectionAdapter.removeAllSections();
                        sectionAdapter.addSection(new BillSection(BillSection.UPCOMING, time));
                        sectionAdapter.addSection(new BillSection(BillSection.OVERDUE, time));
                        sectionAdapter.addSection(new BillSection(BillSection.PAID, time));
                        sectionAdapter.notifyDataSetChanged();
//                        getSupportLoaderManager().restartLoader(THIS_WEEK_LOADER, null, MainActivity.this);

                        break;
                    case R.id.action_today:
                        toolbar.setTitle("Today");
                        time = "today";
//                        String time3 = "today";
                        sectionAdapter.removeAllSections();
                        sectionAdapter.addSection(new BillSection(BillSection.UPCOMING, time));
                        sectionAdapter.addSection(new BillSection(BillSection.OVERDUE, time));
                        sectionAdapter.addSection(new BillSection(BillSection.PAID, time));
                        sectionAdapter.notifyDataSetChanged();
//                        getSupportLoaderManager().restartLoader(TODAY_LOADER, null, MainActivity.this);
                        break;

                    default:
                        break;
                }
                return true;
            }
        });
    }



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_bill);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("This Month");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.my_gray));
        setSupportActionBar(toolbar);


        MobileAds.initialize(getApplicationContext(), "ca-app-pub-7164033483674077~1201302248");

        RelativeLayout comBannerAd = (RelativeLayout) findViewById(R.id.banner_AdView);

        AdView adView  = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(getResources().getString(R.string.ad_id_banner));
        comBannerAd.addView(adView);

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("FEC40D63F8CC61844CD940D15D76DEE6")
                .build();
        adView.loadAd(adRequest);


        errorMsg = (TextView) findViewById(R.id.errorMsg);
        if (savedInstanceState != null) {
            time = savedInstanceState.getString("savedTime");
//            Log.e(TAG, "onCreate: saved time = " + mm );
        }else {
            time = "this_month";
        }

//        time  = "this_month";
//        String timee  = "this_month";

        sectionAdapter = new SectionedRecyclerViewAdapter();
        sectionAdapter.addSection(UPCOMING_TAG, new BillSection(BillSection.UPCOMING, time));
        sectionAdapter.addSection(OVERDUE_TAG, new BillSection(BillSection.OVERDUE, time));
        sectionAdapter.addSection(PAID_TAG, new BillSection(BillSection.PAID, time));


        recyclerView = (RecyclerView) findViewById(R.id.recyclerviewNewBill);
        recyclerView.setLayoutManager(new LinearLayoutManagerWrapper(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(sectionAdapter);
//        recyclerView.notifyAll();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.mainFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addNewIntent = new Intent(NewBillActivity.this, AddNewBillActivity.class);
                addNewIntent.putExtra("data", "ADD_NEW");
                startActivity(addNewIntent);
            }
        });

        int mm = sectionAdapter.getItemCount();

        if (mm <= 3) {
            errorMsg.setVisibility(View.VISIBLE);
        } else {
            errorMsg.setVisibility(View.GONE);
        }
        int ms = sectionAdapter.getItemCount();
        Log.e(TAG, "onCreate: size of adapter kid = " + ms);

    }


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            if (message.equalsIgnoreCase("TRUE")){
                reloadRate();
            }
            Log.e(TAG, "broadcasted message : " + message);
        }
    };


    /**refresh rate fragment after db population*/
    public void reloadRate(){
        Fragment frg = getSupportFragmentManager().findFragmentByTag("new_bill_fragment");
        NewBillFragment ratee = (NewBillFragment) frg;
        @SuppressLint("CommitTransaction")
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.detach(ratee).commitNowAllowingStateLoss();
        @SuppressLint("CommitTransaction")
        FragmentTransaction fts = getSupportFragmentManager().beginTransaction();
        fts.attach(ratee).commitNowAllowingStateLoss();
    }



    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.e(TAG, "onRestoreInstanceState: time = " + savedInstanceState.getString("savedTime"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: time = " + time);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("savedTime", time);
    }

    class BillSection extends StatelessSection {

        final static int UPCOMING = 0;
        final static int OVERDUE = 1;
        final static int PAID = 2;
//        final static int SPORTS = 3;

        final int status;
        String time;

        String title, counter;
        int totalPrice;
        List<Bill> list;
        int imgPlaceholderResId;

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(NewBillActivity.this);
        String defCurrency = pref.getString(NewBillActivity.this.getResources().getString(R.string.pref_key_currency),
                NewBillActivity.this.getResources().getString(R.string.pref_currency_default));

        public BillSection(int status, String time) {
            super(R.layout.section_bill_header, R.layout.section_list_item);

            this.status = status;
            this.time = time;

            switch (status) {
                case UPCOMING:
                    this.title = getString(R.string.upcoming);
                    this.list = getUpcomingList(time);
                    this.counter = String.valueOf(getUpcomingList(time).size());
                    this.imgPlaceholderResId = R.drawable.grey_button1;
                    break;
                case OVERDUE:
                    this.title = getString(R.string.overdue);
                    this.list = getOverdueList(time);
                    this.counter = String.valueOf(getOverdueList(time).size());
                    this.imgPlaceholderResId = R.drawable.red_button1;
                    break;
                case PAID:
                    this.title = getString(R.string.paid);
                    this.list = getPaidList(time);
                    this.counter = String.valueOf(getPaidList(time).size());
                    this.imgPlaceholderResId = R.drawable.green_button;
                    break;
            }

        }

        private List<Bill> getUpcomingList (String times) {
            List<Bill> upcomingList = new ArrayList<>();
            String mm = "upcoming";
/*
            String selection1 = ChartBillContract.Bills.BILL_STATUS + "=?";
            final String[] arg = new String[]{mm};
*/


            Calendar cal = Calendar.getInstance();
            String year = String.valueOf(cal.get(Calendar.YEAR));
            String month;
            String selection1;
            String[] arg;
            if (times.equalsIgnoreCase("last_month")) {
                month = String.valueOf((cal.get(Calendar.MONTH)));
                selection1 = ChartBillContract.Bills.BILL_DUE_MONTH + "=?" + " AND " + ChartBillContract.Bills.BILL_DUE_YEAR + "=?"
                        + " and " + ChartBillContract.Bills.BILL_STATUS + "=?";
                 arg = new String[]{month, year, mm};
            }
            else if (times.equalsIgnoreCase("next_month")) {
                month = String.valueOf((cal.get(Calendar.MONTH)) + 2);
                selection1 = ChartBillContract.Bills.BILL_DUE_MONTH + "=?" + " AND " + ChartBillContract.Bills.BILL_DUE_YEAR + "=?"
                        + " and " + ChartBillContract.Bills.BILL_STATUS + "=?";
                arg = new String[]{month, year, mm};
            }
            else if (times.equalsIgnoreCase("this_week")) {
                SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(NewBillActivity.this);
                String userWeekStart = mPref.getString(getResources().getString(R.string.pref_key_week_start),
                        getResources().getString(R.string.pref_week_start_default));

//                Calendar wsCal = Calendar.getInstance();
                cal.setFirstDayOfWeek(FormatUtils.setWeekFirstDay(userWeekStart));

                month = String.valueOf(cal.get(Calendar.MONTH) + 1);
                String weekMonth = String.valueOf(cal.get(Calendar.WEEK_OF_MONTH));
                selection1 = ChartBillContract.Bills.BILL_DUE_WEEK + "=?" + " and " + ChartBillContract.Bills.BILL_DUE_MONTH + "=?" +
                        " and " + ChartBillContract.Bills.BILL_DUE_YEAR + "=?" + " and " + ChartBillContract.Bills.BILL_STATUS + "=?";
                arg = new String[]{weekMonth,month, year, mm};
            }
            else if (times.equalsIgnoreCase("today")) {
                String mmDate = FormatUtils.getTodaysDate();
                Log.e(TAG, "onCreateLoader: mmDate " + mmDate );
                selection1 = ChartBillContract.Bills.BILL_DUE_DATE + "=?" + " and " + ChartBillContract.Bills.BILL_STATUS + "=?";
                arg = new String[]{mmDate, mm};
            } else {
                month = String.valueOf((cal.get(Calendar.MONTH))+1);
                selection1 = ChartBillContract.Bills.BILL_DUE_MONTH + "=?" + " AND " + ChartBillContract.Bills.BILL_DUE_YEAR + "=?"
                        + " and " + ChartBillContract.Bills.BILL_STATUS + "=?";
                arg = new String[]{month, year, mm};
            }

            CursorLoader cursorLoader = new CursorLoader(NewBillActivity.this, ChartBillContract.Bills.CONTENT_URI,
                    ChartBillContract.Bills.PROJECTION_ALL,
                    selection1,
                    arg,
                    null);
            Cursor c = cursorLoader.loadInBackground();

            while (c.moveToNext()) {
//                String status = c.getString(c.getColumnIndex(ChartBillContract.Bills.BILL_STATUS));

                    String amt = c.getString(c.getColumnIndex(ChartBillContract.Bills.BILL_AMOUNT));
                    String memo = c.getString(c.getColumnIndex(ChartBillContract.Bills.BILL_MEMO));
                    upcomingList.add(new Bill(c.getString(c.getColumnIndex(ChartBillContract.Bills._ID)),
                            c.getString(c.getColumnIndex(ChartBillContract.Bills.BILL_DUE_DATE)),
                            c.getString(c.getColumnIndex(ChartBillContract.Bills.BILL_DESCRIPTION)),
                            c.getString(c.getColumnIndex(ChartBillContract.Bills.BILL_REPEAT_FREQ)),
                            amt,
                            memo,
                            c.getString(c.getColumnIndex(ChartBillContract.Bills.BILL_STATUS)),
                            c.getString(c.getColumnIndex(ChartBillContract.Bills.BILL_DUE_WEEK)),
                            c.getString(c.getColumnIndex(ChartBillContract.Bills.BILL_DUE_MONTH)),
                            c.getString(c.getColumnIndex(ChartBillContract.Bills.BILL_DUE_YEAR)),
                            c.getString(c.getColumnIndex(ChartBillContract.Bills.BILL_ALARM_ID))
                            ));

            }
            return upcomingList;
        }
        private List<Bill> getOverdueList (String times) {
            List<Bill> overdueList = new ArrayList<>();
            String mm = "overdue";
/*            String selection1 = ChartBillContract.Bills.BILL_STATUS + "=?";
            final String[] arg = new String[]{mm};*/


            Calendar cal = Calendar.getInstance();
            String year = String.valueOf(cal.get(Calendar.YEAR));
            String month;
            String selection1;
            String[] arg;
            if (times.equalsIgnoreCase("last_month")) {
                month = String.valueOf((cal.get(Calendar.MONTH)));
                selection1 = ChartBillContract.Bills.BILL_DUE_MONTH + "=?" + " AND " + ChartBillContract.Bills.BILL_DUE_YEAR + "=?"
                        + " and " + ChartBillContract.Bills.BILL_STATUS + "=?";
                arg = new String[]{month, year, mm};
            }
            else if (times.equalsIgnoreCase("next_month")) {
                month = String.valueOf((cal.get(Calendar.MONTH)) + 2);
                selection1 = ChartBillContract.Bills.BILL_DUE_MONTH + "=?" + " AND " + ChartBillContract.Bills.BILL_DUE_YEAR + "=?"
                        + " and " + ChartBillContract.Bills.BILL_STATUS + "=?";
                arg = new String[]{month, year, mm};
            }
            else if (times.equalsIgnoreCase("this_week")) {
                SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(NewBillActivity.this);
                String userWeekStart = mPref.getString(getResources().getString(R.string.pref_key_week_start),
                        getResources().getString(R.string.pref_week_start_default));

//                Calendar wsCal = Calendar.getInstance();
                cal.setFirstDayOfWeek(FormatUtils.setWeekFirstDay(userWeekStart));
                month = String.valueOf(cal.get(Calendar.MONTH) + 1);
                String weekMonth = String.valueOf(cal.get(Calendar.WEEK_OF_MONTH));
                selection1 = ChartBillContract.Bills.BILL_DUE_WEEK + "=?" + " and " + ChartBillContract.Bills.BILL_DUE_MONTH + "=?" +
                        " and " + ChartBillContract.Bills.BILL_DUE_YEAR + "=?" + " and " + ChartBillContract.Bills.BILL_STATUS + "=?";
                arg = new String[]{weekMonth,month, year, mm};
            }
            else if (times.equalsIgnoreCase("today")) {
                String mmDate = FormatUtils.getTodaysDate();
                Log.e(TAG, "onCreateLoader: mmDate " + mmDate );
                selection1 = ChartBillContract.Bills.BILL_DUE_DATE + "=?" + " and " + ChartBillContract.Bills.BILL_STATUS + "=?";
                arg = new String[]{mmDate, mm};
            } else {
                month = String.valueOf((cal.get(Calendar.MONTH))+1);
                selection1 = ChartBillContract.Bills.BILL_DUE_MONTH + "=?" + " AND " + ChartBillContract.Bills.BILL_DUE_YEAR + "=?"
                        + " and " + ChartBillContract.Bills.BILL_STATUS + "=?";
                arg = new String[]{month, year, mm};
            }


            CursorLoader cursorLoader = new CursorLoader(NewBillActivity.this, ChartBillContract.Bills.CONTENT_URI,
                    ChartBillContract.Bills.PROJECTION_ALL,
                    selection1,
                    arg,
                    null);
            Cursor c = cursorLoader.loadInBackground();

            while (c.moveToNext()) {
                    String amt = c.getString(c.getColumnIndex(ChartBillContract.Bills.BILL_AMOUNT));
                    String memo = c.getString(c.getColumnIndex(ChartBillContract.Bills.BILL_MEMO));
                    overdueList.add(new Bill(c.getString(c.getColumnIndex(ChartBillContract.Bills._ID)),
                            c.getString(c.getColumnIndex(ChartBillContract.Bills.BILL_DUE_DATE)),
                            c.getString(c.getColumnIndex(ChartBillContract.Bills.BILL_DESCRIPTION)),
                            c.getString(c.getColumnIndex(ChartBillContract.Bills.BILL_REPEAT_FREQ)),
                            amt,
                            memo,
                            c.getString(c.getColumnIndex(ChartBillContract.Bills.BILL_STATUS)),
                            c.getString(c.getColumnIndex(ChartBillContract.Bills.BILL_DUE_WEEK)),
                            c.getString(c.getColumnIndex(ChartBillContract.Bills.BILL_DUE_MONTH)),
                            c.getString(c.getColumnIndex(ChartBillContract.Bills.BILL_DUE_YEAR)),
                            c.getString(c.getColumnIndex(ChartBillContract.Bills.BILL_ALARM_ID))
                            ));

            }
            return overdueList;
        }

        private List<Bill> getPaidList (String times) {
            List<Bill> paidList = new ArrayList<>();
            String mm = "paid";
/*            String selection1 = ChartBillContract.Bills.BILL_STATUS + "=?";
            final String[] arg = new String[]{mm};*/


            Calendar cal = Calendar.getInstance();
            String year = String.valueOf(cal.get(Calendar.YEAR));
            String month;
            String selection1;
            String[] arg;
            if (times.equalsIgnoreCase("last_month")) {
                month = String.valueOf((cal.get(Calendar.MONTH)));
                selection1 = ChartBillContract.Bills.BILL_DUE_MONTH + "=?" + " AND " + ChartBillContract.Bills.BILL_DUE_YEAR + "=?"
                        + " and " + ChartBillContract.Bills.BILL_STATUS + "=?";
                arg = new String[]{month, year, mm};
            }
            else if (times.equalsIgnoreCase("next_month")) {
                month = String.valueOf((cal.get(Calendar.MONTH)) + 2);
                selection1 = ChartBillContract.Bills.BILL_DUE_MONTH + "=?" + " AND " + ChartBillContract.Bills.BILL_DUE_YEAR + "=?"
                        + " and " + ChartBillContract.Bills.BILL_STATUS + "=?";
                arg = new String[]{month, year, mm};
            }
            else if (times.equalsIgnoreCase("this_week")) {
                SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(NewBillActivity.this);
                String userWeekStart = mPref.getString(getResources().getString(R.string.pref_key_week_start),
                        getResources().getString(R.string.pref_week_start_default));

//                Calendar wsCal = Calendar.getInstance();
                cal.setFirstDayOfWeek(FormatUtils.setWeekFirstDay(userWeekStart));
                month = String.valueOf(cal.get(Calendar.MONTH) + 1);
                String weekMonth = String.valueOf(cal.get(Calendar.WEEK_OF_MONTH));
                selection1 = ChartBillContract.Bills.BILL_DUE_WEEK + "=?" + " and " + ChartBillContract.Bills.BILL_DUE_MONTH + "=?" +
                        " and " + ChartBillContract.Bills.BILL_DUE_YEAR + "=?" + " and " + ChartBillContract.Bills.BILL_STATUS + "=?";
                arg = new String[]{weekMonth,month, year, mm};
            }
            else if (times.equalsIgnoreCase("today")) {
                String mmDate = FormatUtils.getTodaysDate();
                Log.e(TAG, "onCreateLoader: mmDate " + mmDate );
                selection1 = ChartBillContract.Bills.BILL_DUE_DATE + "=?" + " and " + ChartBillContract.Bills.BILL_STATUS + "=?";
                arg = new String[]{mmDate, mm};
            } else {
                month = String.valueOf((cal.get(Calendar.MONTH))+1);
                selection1 = ChartBillContract.Bills.BILL_DUE_MONTH + "=?" + " AND " + ChartBillContract.Bills.BILL_DUE_YEAR + "=?"
                        + " and " + ChartBillContract.Bills.BILL_STATUS + "=?";
                arg = new String[]{month, year, mm};
            }
            CursorLoader cursorLoader = new CursorLoader(NewBillActivity.this, ChartBillContract.Bills.CONTENT_URI,
                    ChartBillContract.Bills.PROJECTION_ALL,
                    selection1,
                    arg,
                    ChartBillContract.Bills.SORT_ORDER_DEFAULT);
            Cursor c = cursorLoader.loadInBackground();

            while (c.moveToNext()) {
                    String amt = c.getString(c.getColumnIndex(ChartBillContract.Bills.BILL_AMOUNT));
                    String memo = c.getString(c.getColumnIndex(ChartBillContract.Bills.BILL_MEMO));

                    paidList.add(new Bill(c.getString(c.getColumnIndex(ChartBillContract.Bills._ID)),
                            c.getString(c.getColumnIndex(ChartBillContract.Bills.BILL_DUE_DATE)),
                            c.getString(c.getColumnIndex(ChartBillContract.Bills.BILL_DESCRIPTION)),
                            c.getString(c.getColumnIndex(ChartBillContract.Bills.BILL_REPEAT_FREQ)),
                            amt,
                            memo,
                            c.getString(c.getColumnIndex(ChartBillContract.Bills.BILL_STATUS)),
                            c.getString(c.getColumnIndex(ChartBillContract.Bills.BILL_DUE_WEEK)),
                            c.getString(c.getColumnIndex(ChartBillContract.Bills.BILL_DUE_MONTH)),
                            c.getString(c.getColumnIndex(ChartBillContract.Bills.BILL_DUE_YEAR)),
                            c.getString(c.getColumnIndex(ChartBillContract.Bills.BILL_ALARM_ID))
                            ));
            }
            return paidList;
        }

        @Override
        public int getContentItemsTotal() {
            return list.size();
        }

        @Override
        public RecyclerView.ViewHolder getItemViewHolder(View view) {
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindItemViewHolder(RecyclerView.ViewHolder holder, final int position) {

            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            itemHolder.setIsRecyclable(false);
            String headerStr = list.get(position).getDescription();
            itemHolder.tvHeader.setText(capitalizeFirstLetter(headerStr));
            if (!TextUtils.isEmpty( list.get(position).getAmount())) {
            itemHolder.tvAmount.setText(defCurrency + list.get(position).getAmount());
            } else {
                itemHolder.tvAmount.setText("-");
            }
            final String status = list.get(position).getStatus();
            String memo = list.get(position).getMemo();
            String repeatStr = list.get(position).getRepeatFreq();
            if ("upcoming".equalsIgnoreCase(status)){
                String formattedDate = FormatUtils.getUpcomingReadableDate(Long.parseLong(list.get(position).getDueDate()),
                        Integer.parseInt(list.get(position).getWeek()),
                        Integer.parseInt(list.get(position).getMonth()),
                        Integer.parseInt(list.get(position).getYear()));
                itemHolder.tvDate.setText(formattedDate);
                itemHolder.tvDate.setTextColor(ContextCompat.getColor(NewBillActivity.this,R.color.colorAccent));
            } else if ("overdue".equalsIgnoreCase(status)){
                String formattedDate = FormatUtils.getReadableDate(Long.parseLong(list.get(position).getDueDate()));
                itemHolder.tvDate.setText(formattedDate);
                itemHolder.tvDate.setTextColor(ContextCompat.getColor(NewBillActivity.this,R.color.red_button_color));
            } else if ("paid".equalsIgnoreCase(status)) {
                itemHolder.tvDate.setVisibility(View.GONE);
            }
            else if ("Does not repeat".equalsIgnoreCase(repeatStr)) {
                itemHolder.repeatImg.setVisibility(View.GONE);
            }
            hideMemoIcon(memo, itemHolder.memoImg);
            hideRepeatIcon(repeatStr, itemHolder.repeatImg);
            itemHolder.imgItem.setImageResource(imgPlaceholderResId);
            itemHolder.imgItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String symbi = list.get(position).getId();
                    String dueDate = list.get(position).getDueDate();
                    changeBillStatus(status, symbi, dueDate, list);
                }
            });


            itemHolder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    long symbi = Long.parseLong(list.get(position).getId());
                    long alaId = Long.parseLong(list.get(position).getAlarmId());
                    Log.e(TAG, "onClick: id od clicked item = " + list.get(position).getAlarmId());
                    Intent intent = new Intent(NewBillActivity.this, AddNewBillActivity.class);
                    intent.putExtra("recyclerItem", symbi);
                    intent.putExtra("alarmId", alaId);
                    intent.putExtra("data", "CLICKED_ITEM");
                    startActivity(intent);

                }
            });


        }

        private void changeBillStatus(String status, String symbi, String dueDat, List<Bill> mlist) {
            switch (status) {
                case "upcoming":
                    ContentValues mValue = new ContentValues();
                    mValue.put(ChartBillContract.Bills.BILL_STATUS, "paid");
                    getContentResolver().update(ChartBillContract.Bills.CONTENT_URI, mValue, ChartBillContract.Bills._ID + "=?",
                            new String[]{symbi});

                    recyclerView.getRecycledViewPool().clear();
                    mlist.clear();
                    sectionAdapter.removeAllSections();
                    sectionAdapter.notifyDataSetChanged();
                    sectionAdapter.addSection(UPCOMING_TAG, new BillSection(BillSection.UPCOMING, time));
                    sectionAdapter.addSection(OVERDUE_TAG, new BillSection(BillSection.OVERDUE, time));
                    sectionAdapter.addSection(PAID_TAG, new BillSection(BillSection.PAID, time));

//                    sectionAdapter.notifyDataSetChanged();

                    break;
                case "overdue":
                    ContentValues oValue = new ContentValues();
                    oValue.put(ChartBillContract.Bills.BILL_STATUS, "upcoming");
                    getContentResolver().update(ChartBillContract.Bills.CONTENT_URI, oValue, ChartBillContract.Bills._ID + "=?",
                            new String[]{symbi});

                    recyclerView.getRecycledViewPool().clear();
                    mlist.clear();
                    sectionAdapter.removeAllSections();
                    sectionAdapter.notifyDataSetChanged();
                    sectionAdapter.addSection(UPCOMING_TAG, new BillSection(BillSection.UPCOMING, time));
                    sectionAdapter.addSection(OVERDUE_TAG, new BillSection(BillSection.OVERDUE, time));
                    sectionAdapter.addSection(PAID_TAG, new BillSection(BillSection.PAID, time));
                    recyclerView.setAdapter(sectionAdapter);

                    break;
                case "paid":
//                    String symbi = c.getString(c.getColumnIndex(ChartBillContract.Bills._ID));
                    long dueDate = Long.parseLong(dueDat);
                    long todayDate = Long.parseLong(FormatUtils.getTodaysDate());

                    ContentValues pValue = new ContentValues();
                    if (dueDate < todayDate) {
                        pValue.put(ChartBillContract.Bills.BILL_STATUS, "overdue");
                    } else {

                        pValue.put(ChartBillContract.Bills.BILL_STATUS, "upcoming");
                    }
                    getContentResolver().update(ChartBillContract.Bills.CONTENT_URI, pValue, ChartBillContract.Bills._ID + "=?",
                            new String[]{symbi});


                    recyclerView.getRecycledViewPool().clear();
                    mlist.clear();
                    sectionAdapter.removeAllSections();
                    sectionAdapter.notifyDataSetChanged();
                    sectionAdapter.addSection(UPCOMING_TAG, new BillSection(BillSection.UPCOMING, time));
                    sectionAdapter.addSection(OVERDUE_TAG, new BillSection(BillSection.OVERDUE, time));
                    sectionAdapter.addSection(PAID_TAG, new BillSection(BillSection.PAID, time));
                    break;
            }
        }

        private void hideMemoIcon (String memo, ImageView holderImage) {
            if (TextUtils.isEmpty(memo)) {
                holderImage.setVisibility(View.GONE);
            } else {
                holderImage.setVisibility(View.VISIBLE);
            }
        }
        private void hideRepeatIcon (String repeatFreq, ImageView holderImage) {
            if (repeatFreq.equalsIgnoreCase("does not repeat")) {
                holderImage.setVisibility(View.GONE);
            } else {
                holderImage.setVisibility(View.VISIBLE);
            }
        }

        private String capitalizeFirstLetter (String str) {
            return str.toUpperCase().charAt(0) + str.substring(1);
        }

        @Override
        public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
            return new HeaderViewHolder(view);
        }

        @Override
        public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            if ((list.size() <= 0) && (title.equalsIgnoreCase("upcoming"))) {
//                headerHolder.root.setVisibility(View.INVISIBLE);
//                sectionAdapter.removeSection(UPCOMING_TAG);
            }else if ((list.size() <= 0) && (title.equalsIgnoreCase("overdue"))) {
//                headerHolder.root.setVisibility(View.INVISIBLE);
//                sectionAdapter.removeSection(OVERDUE_TAG);
            }else if ((list.size() <= 0) && (title.equalsIgnoreCase("paid"))) {
//                headerHolder.root.setVisibility(View.INVISIBLE);
//                sectionAdapter.removeSection(PAID_TAG);
            } else {
                headerHolder.root.setVisibility(View.VISIBLE);
            }

            headerHolder.tvTitle.setText(title);
            headerHolder.tvTitleCount.setText("( " + counter + " )" );
            if (title.equalsIgnoreCase("paid")) {
                headerHolder.tvTitle.setTextColor(ContextCompat.getColor(NewBillActivity.this, R.color.green_button_color));
                headerHolder.tvTitleCount.setTextColor(ContextCompat.getColor(NewBillActivity.this, R.color.green_button_color));
                headerHolder.tvTitlePrice.setTextColor(ContextCompat.getColor(NewBillActivity.this, R.color.green_button_color));
            }
            if (title.equalsIgnoreCase("overdue")) {
                headerHolder.tvTitle.setTextColor(ContextCompat.getColor(NewBillActivity.this, R.color.red_button_color));
                headerHolder.tvTitleCount.setTextColor(ContextCompat.getColor(NewBillActivity.this, R.color.red_button_color));
                headerHolder.tvTitlePrice.setTextColor(ContextCompat.getColor(NewBillActivity.this, R.color.red_button_color));
            }

            for (int i = 0; i < list.size(); i++) {
                String mm = list.get(i).getAmount();
                if (mm.equalsIgnoreCase("")) {
                    mm = "0";
                }
                totalPrice += Integer.parseInt(mm);
            }
            headerHolder.tvTitlePrice.setText(defCurrency + totalPrice);
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTitle, tvTitleCount, tvTitlePrice;
        View root;
        public HeaderViewHolder(View view) {
            super(view);

            root = view;
            tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            tvTitleCount = (TextView) view.findViewById(R.id.tvTitleCount);
            tvTitlePrice = (TextView) view.findViewById(R.id.tvTitlePrice);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        private final View rootView;
        private final ImageView imgItem, memoImg, repeatImg;
        private final TextView tvHeader;
        private final TextView tvDate;
        private final TextView tvAmount;

        public ItemViewHolder(View view) {
            super(view);

            rootView = view;
            imgItem = (ImageView) view.findViewById(R.id.tvSectionItemImage);
            memoImg = (ImageView) view.findViewById(R.id.ivRecyclerMemo);
            repeatImg = (ImageView) view.findViewById(R.id.ivRecyclerRepeat);
            tvHeader = (TextView) view.findViewById(R.id.tvSectionItemTitle);
            tvDate = (TextView) view.findViewById(R.id.tvSectionItemDate);
            tvAmount = (TextView) view.findViewById(R.id.tvSectionItemAmount);
        }
    }
}
