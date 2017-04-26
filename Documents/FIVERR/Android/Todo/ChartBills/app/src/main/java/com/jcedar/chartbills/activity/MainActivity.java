package com.jcedar.chartbills.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.jcedar.chartbills.R;
import com.jcedar.chartbills.adapter.BillRecyclerAdapter;
import com.jcedar.chartbills.helper.FormatUtils;
import com.jcedar.chartbills.model.Bill;
import com.jcedar.chartbills.provider.ChartBillContract;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MainActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<Cursor>, BillRecyclerAdapter.ClickListener,
        BillRecyclerAdapter.RedButtonClickListener, BillRecyclerAdapter.GreenButtonClickListener, BillRecyclerAdapter.GreyButtonClickListener{

    private static final String TAG = MainActivity.class.getSimpleName();

    RecyclerView recyclerview;
    BillRecyclerAdapter mAdapter;

    TextView errorMsg;

    Toolbar toolbar;

    private AdView mBannerAd;
    public static final int BILL_LOADER = 0;
    private static final int THIS_MONTH_LOADER = 1;
    private static final int LAST_MONTH_LOADER = 2;
    private static final int NEXT_MONTH_LOADER = 3;
    private static final int TODAY_LOADER = 4;
    private static final int THIS_WEEK_LOADER = 5;
    List<Bill> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("This Month");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.my_gray));
        setSupportActionBar(toolbar);

        mList = new ArrayList<>();

        getSupportLoaderManager().initLoader(BILL_LOADER, null, this);
        getSupportLoaderManager().initLoader(THIS_MONTH_LOADER, null, this);
        getSupportLoaderManager().initLoader(LAST_MONTH_LOADER, null, this);
        getSupportLoaderManager().initLoader(NEXT_MONTH_LOADER, null, this);
        getSupportLoaderManager().initLoader(TODAY_LOADER, null, this);
        getSupportLoaderManager().initLoader(THIS_WEEK_LOADER, null, this);


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
        recyclerview = (RecyclerView) findViewById(R.id.recyclerChart);
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new BillRecyclerAdapter(this);
        mAdapter.setRClicklistener(this);
        mAdapter.setClicklistener(this);
        mAdapter.setGreenClicklistener(this);
        mAdapter.setGreyButtonClicklistener(this);
        recyclerview.setAdapter(mAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.mainFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addNewIntent = new Intent(MainActivity.this, AddNewBillActivity.class);
                addNewIntent.putExtra("data", "ADD_NEW");
                startActivity(addNewIntent);
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });


//        showBannerAd();

        int mm = mAdapter.getItemCount();

        if (mm != 0) {
            errorMsg.setVisibility(View.GONE);
        } else {
            errorMsg.setVisibility(View.VISIBLE);
        }

        
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
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
        PopupMenu pop = new PopupMenu(MainActivity.this, v);
        pop.getMenuInflater().inflate(R.menu.menu_date_filter, pop.getMenu());
        pop.show();
        pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_last_month:
                        toolbar.setTitle("Last Month");

//                        startActivity(new Intent(MainActivity.this, AndroidDatabaseManager.class));

                        getSupportLoaderManager().restartLoader(LAST_MONTH_LOADER, null, MainActivity.this);

                        break;
                    case R.id.action_next_month:
                        toolbar.setTitle("Next Month");
                        getSupportLoaderManager().restartLoader(NEXT_MONTH_LOADER, null, MainActivity.this);

                        break;
                    case R.id.action_this_month:
                        toolbar.setTitle("This Month");
                        getSupportLoaderManager().restartLoader(THIS_MONTH_LOADER, null, MainActivity.this);
                        break;
                    case R.id.action_this_week:
                        toolbar.setTitle("This Week");
                        getSupportLoaderManager().restartLoader(THIS_WEEK_LOADER, null, MainActivity.this);

                        break;
                    case R.id.action_today:
                        toolbar.setTitle("Today");
                        getSupportLoaderManager().restartLoader(TODAY_LOADER, null, MainActivity.this);
                        break;

                        default:
                            break;
                }
                return true;
            }
        });
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = ChartBillContract.Bills.CONTENT_URI;

        if (id == TODAY_LOADER) {
            String mmDate = FormatUtils.getTodaysDate();
            String selection1 = ChartBillContract.Bills.BILL_DUE_DATE + "=?";
            String[] arg = new String[]{mmDate};

            return new CursorLoader(
                    this,
                    uri,
                    ChartBillContract.Bills.PROJECTION_ALL,
                    selection1,    // selection
                    arg,           // arguments
                    ChartBillContract.Bills.BILL_STATUS + " ASC");
        }
        else if (id == THIS_WEEK_LOADER) {
            Calendar cal = Calendar.getInstance();
            String year = String.valueOf(cal.get(Calendar.YEAR));
            String month = String.valueOf(cal.get(Calendar.MONTH) + 1);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            String weekMonth = String.valueOf(cal.get(Calendar.WEEK_OF_MONTH));

            String selection1 = ChartBillContract.Bills.BILL_DUE_WEEK + "=?" + " and " + ChartBillContract.Bills.BILL_DUE_MONTH + "=?" +
                    " and " + ChartBillContract.Bills.BILL_DUE_YEAR + "=?";
            String[] arg = new String[]{weekMonth,month, year};

            return new CursorLoader(
                    this,
                    uri,
                    ChartBillContract.Bills.PROJECTION_ALL,
                    selection1,    // selection
                    arg,           // arguments
                    ChartBillContract.Bills.BILL_STATUS + " ASC");
        }
        else if (id == THIS_MONTH_LOADER) {

            Calendar cal = Calendar.getInstance();
            String year = String.valueOf(cal.get(Calendar.YEAR));
            String month = String.valueOf(cal.get(Calendar.MONTH) + 1);

            String selection1 = ChartBillContract.Bills.BILL_DUE_MONTH + "=?" + " AND " + ChartBillContract.Bills.BILL_DUE_YEAR + "=?";
            String[] arg = new String[]{month, year};

            return new CursorLoader(
                    this,
                    uri,
                    ChartBillContract.Bills.PROJECTION_ALL,
                    selection1,    // selection
                    arg,           // arguments
                    ChartBillContract.Bills.BILL_STATUS + " ASC");
        }
        else if (id == LAST_MONTH_LOADER) {

            Calendar cal = Calendar.getInstance();
            String year = String.valueOf(cal.get(Calendar.YEAR));
            String month = String.valueOf((cal.get(Calendar.MONTH)));

            String selection1 = ChartBillContract.Bills.BILL_DUE_MONTH + "=?" + " AND " + ChartBillContract.Bills.BILL_DUE_YEAR + "=?";
            String[] arg = new String[]{month, year};

            return new CursorLoader(
                    this,
                    uri,
                    ChartBillContract.Bills.PROJECTION_ALL,
                    selection1,    // selection
                    arg,           // arguments
                    ChartBillContract.Bills.BILL_STATUS + " ASC");
        }
        else if (id == NEXT_MONTH_LOADER) {

            Calendar cal = Calendar.getInstance();
            String year = String.valueOf(cal.get(Calendar.YEAR));
            String month = String.valueOf((cal.get(Calendar.MONTH)) + 2);

            String selection1 = ChartBillContract.Bills.BILL_DUE_MONTH + "=?" + " AND " + ChartBillContract.Bills.BILL_DUE_YEAR + "=?";
            String[] arg = new String[]{month, year};

            return new CursorLoader(
                    this,
                    uri,
                    ChartBillContract.Bills.PROJECTION_ALL,
                    selection1,    // selection
                    arg,           // arguments
                    ChartBillContract.Bills.BILL_STATUS + " ASC");
        }
        else {
/*            return new CursorLoader(
                    this,
                    uri,
                    ChartBillContract.Bills.PROJECTION_ALL,
                    null,    // selection
                    null,           // arguments
                    ChartBillContract.Bills.BILL_STATUS + " ASC");*/

            Calendar cal = Calendar.getInstance();
            String year = String.valueOf(cal.get(Calendar.YEAR));
            String month = String.valueOf(cal.get(Calendar.MONTH) + 1);

            String selection1 = ChartBillContract.Bills.BILL_DUE_MONTH + "=?" + " AND " + ChartBillContract.Bills.BILL_DUE_YEAR + "=?";
            String[] arg = new String[]{month, year};

            return new CursorLoader(
                    this,
                    uri,
                    ChartBillContract.Bills.PROJECTION_ALL,
                    selection1,    // selection
                    arg,           // arguments
                    ChartBillContract.Bills.BILL_STATUS + " ASC");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        while (data.moveToNext()) {
            String mstat = data.getString(data.getColumnIndex(ChartBillContract.Bills.BILL_STATUS));
            if ("paid".equalsIgnoreCase(mstat))
            mList = getPaidList(data);
        }
        Log.e(TAG, "onCreate: List loaded = " + mList.size());
        mAdapter.swapCursor(data);
        if (data.moveToNext()) {
            errorMsg.setVisibility(View.GONE);
        } else {
            errorMsg.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(BILL_LOADER, null, this);
        if (mAdapter.getItemCount() != 0) {
            errorMsg.setVisibility(View.GONE);
        } else {
            errorMsg.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void RedButtonItemClickListener(View view, int position) {

        Cursor c = mAdapter.getItem(position);
        String symbi = c.getString(c.getColumnIndex(ChartBillContract.Bills._ID));
        ContentValues mValue = new ContentValues();
        mValue.put(ChartBillContract.Bills.BILL_STATUS, "upcoming");
        getContentResolver().update(ChartBillContract.Bills.CONTENT_URI, mValue, ChartBillContract.Bills._ID + "=?",
                new String[]{symbi});
        mAdapter.notifyDataSetChanged();
        getSupportLoaderManager().restartLoader(BILL_LOADER, null, MainActivity.this);
    }

    @Override
    public void GreenButtonItemClickListener(View view, int position) {
        Cursor c = mAdapter.getItem(position);
        String symbi = c.getString(c.getColumnIndex(ChartBillContract.Bills._ID));
        long dueDate = Long.parseLong(c.getString(c.getColumnIndex(ChartBillContract.Bills.BILL_DUE_DATE)));
        long todayDate = Long.parseLong(FormatUtils.getTodaysDate());

        ContentValues mValue = new ContentValues();
        if (dueDate < todayDate) {
            mValue.put(ChartBillContract.Bills.BILL_STATUS, "overdue");
        } else {

            mValue.put(ChartBillContract.Bills.BILL_STATUS, "upcoming");
        }
        getContentResolver().update(ChartBillContract.Bills.CONTENT_URI, mValue, ChartBillContract.Bills._ID + "=?",
                new String[]{symbi});
        mAdapter.notifyDataSetChanged();
        getSupportLoaderManager().restartLoader(BILL_LOADER, null, MainActivity.this);
    }

    @Override
    public void GreyButtonItemClickListener(View view, int position) {
        Cursor c = mAdapter.getItem(position);
        String symbi = c.getString(c.getColumnIndex(ChartBillContract.Bills._ID));
        ContentValues mValue = new ContentValues();
        mValue.put(ChartBillContract.Bills.BILL_STATUS, "paid");
        getContentResolver().update(ChartBillContract.Bills.CONTENT_URI, mValue, ChartBillContract.Bills._ID + "=?",
                new String[]{symbi});
        mAdapter.notifyDataSetChanged();
        getSupportLoaderManager().restartLoader(BILL_LOADER, null, MainActivity.this);
    }

    @Override
    public void itemClickListener(View view, int position) {
        Cursor c = mAdapter.getItem(position);
        long symbi = Long.parseLong(c.getString(c.getColumnIndex(ChartBillContract.Bills._ID)));
        long alaId = Long.parseLong(c.getString(c.getColumnIndex(ChartBillContract.Bills.BILL_ALARM_ID)));
        Intent intent = new Intent(MainActivity.this, AddNewBillActivity.class);
        intent.putExtra("recyclerItem", symbi);
        intent.putExtra("alarmId", alaId);
        intent.putExtra("data", "CLICKED_ITEM");
        startActivity(intent);

    }

    private List<Bill> getPaidList (Cursor c) {
        List<Bill> paidList = new ArrayList<>();
        String mm = "paid";
/*        String selection1 = ChartBillContract.Bills.BILL_STATUS + "=?";
        final String[] arg = new String[]{mm};
        CursorLoader cursorLoader = new CursorLoader(NewBillActivity.this, ChartBillContract.Bills.CONTENT_URI,
                ChartBillContract.Bills.PROJECTION_ALL,
                selection1,
                arg,
                null);
        Cursor c = cursorLoader.loadInBackground();*/

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
}
