package com.jcedar.chartbills.activity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.jcedar.chartbills.R;
import com.jcedar.chartbills.adapter.BillRecyclerAdapter;
import com.jcedar.chartbills.helper.FormatUtils;
import com.jcedar.chartbills.provider.ChartBillContract;

import java.util.Calendar;

/**
 * Created by OLUWAPHEMMY on 3/9/2017.
 */
public class AddNewBillActivity extends AppCompatActivity implements View.OnClickListener,
        DatePickerDialog.OnDateSetListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = AddNewBillActivity.class.getSimpleName();

    private ImageView imBack, imDel, imDone;
    private TextInputEditText desc, dueDate, amount, memo, repeat;
    TextView curSym;
    private Spinner repeatSpinner;
    AlertDialog alertDialog1;

    private static final int DETAIL_LOADER = 0;

    String[] repeatFreq = {"Monthly", "Weekly", "Yearly", "Does not repeat"};
    String selectedSpinnerFreq;
    String daySel, monthSel, weekee, yearSel;
    String daySelN, monthSelN, weekeeN, yearSelN;

    private static final String STATUS = "upcoming";
    long detailID, alarmUpdateId;
    String viewType, detailDesc;
    Uri dataUri;
    BillRecyclerAdapter mAdapter;
    boolean dateIsSet = false;
    boolean dateIsSetNew = false;
    int hour, min;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);


        Bundle b = getIntent().getExtras();
        if (b != null) {
            detailID = b.getLong("recyclerItem");
            alarmUpdateId = b.getLong("alarmId");
            viewType = b.getString("data");
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        }
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String hourMin = pref.getString(getResources().getString(R.string.pref_reminder_time), getResources().getString(R.string.pref_time_reminder_default));
        String[] timee = hourMin.split(":");
        hour = Integer.parseInt(timee[0]);
        min = Integer.parseInt(timee[1]);
/*
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-7164033483674077~1201302248");*/

        LinearLayout comBannerAd = (LinearLayout) findViewById(R.id.banner_AdView);

        AdView adView  = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(getResources().getString(R.string.ad_id_banner));
        comBannerAd.addView(adView);

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("FEC40D63F8CC61844CD940D15D76DEE6")
                .build();
        adView.loadAd(adRequest);

        mAdapter = new BillRecyclerAdapter(this);

        imBack = (ImageView) findViewById(R.id.ivBack);
        imDel = (ImageView) findViewById(R.id.ivDelete);
        imDone = (ImageView) findViewById(R.id.ivDone);

        desc = (TextInputEditText) findViewById(R.id.tietDesc);
        dueDate = (TextInputEditText) findViewById(R.id.tIetDueDate);
        amount = (TextInputEditText) findViewById(R.id.tIetAmount);
        memo = (TextInputEditText) findViewById(R.id.tIetMemo);
        repeat = (TextInputEditText) findViewById(R.id.tIetRepeat);

/*        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String symb = sp.getString(getResources().getString(R.string.pref_key_currency), getResources().getString(R.string.pref_currency_default));
        curSym.setText(symb);

        repeatSpinner = (Spinner) findViewById(R.id.repeat_spinner);*/

        if (viewType.equalsIgnoreCase("CLICKED_ITEM")) {
            imDel.setVisibility(View.VISIBLE);
            dataUri = ChartBillContract.Bills.buildCurrencyUri(detailID);
            getSupportLoaderManager().restartLoader(DETAIL_LOADER, null, this);

        }

/*        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, repeatFreq);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        repeatSpinner.setAdapter(spinnerAdapter);*/

        imBack.setOnClickListener(this);
        imDone.setOnClickListener(this);
        imDel.setOnClickListener(this);
        desc.setOnClickListener(this);
        dueDate.setOnClickListener(this);
        amount.setOnClickListener(this);
        memo.setOnClickListener(this);
        repeat.setOnClickListener(this);

/*        repeatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedSpinnerFreq = adapterView.getSelectedItem().toString();
                Log.e(TAG, "onItemSelected: " + selectedSpinnerFreq);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/

        String da = FormatUtils.getRecyclerReadableDate(1490227563402L);
        Log.e(TAG, "onCreate: readable date = " + da );

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                finish();
                break;
            case R.id.ivDelete:
                String itm = String.valueOf(detailID);
                getContentResolver().delete(ChartBillContract.Bills.CONTENT_URI, ChartBillContract.Bills._ID + "=?", new String[]{itm});
                mAdapter.notifyDataSetChanged();
                Toast.makeText(this, detailDesc + " successfully deleted", Toast.LENGTH_SHORT).show();
                finish();

                break;
            case R.id.ivDone:

                String description = desc.getText().toString();
                String due_date = dueDate.getText().toString();
                String amountStr = amount.getText().toString();
                String repeatStr = repeat.getText().toString();
                String memoStr = memo.getText().toString();
//                String repeatSpinStr = repeatSpinner.getText().toString();

                boolean cancel = false;
                View focusView = null;

                if (TextUtils.isEmpty(description)) {
                    desc.setError("Description cannot be empty");
                    focusView = desc;
                    cancel = true;
                }else if (TextUtils.isEmpty(due_date)) {
                    dueDate.setError("Due Date cannot be empty");
                    focusView = dueDate;
                    cancel = true;
                }else if (TextUtils.isEmpty(repeatStr)) {
                    repeat.setError("Repeat cannot be empty");
                    focusView = repeat;
                    cancel = true;
                }
                if (cancel) {
                    // There was an error; don't attempt login and focus the first
                    // form field with an error.
                    focusView.requestFocus();
                } else {

                        long mDueDate = FormatUtils.dbDate(due_date);
                        if (viewType.equalsIgnoreCase("ADD_NEW")) {
                            if (dateIsSetNew) {
                                long alarmId = System.currentTimeMillis();
                                insertBillToDb(description, mDueDate, amountStr, repeatStr, memoStr, daySelN, weekeeN, monthSelN, yearSelN, alarmId);
                            } else {
                                Toast.makeText(this, "Some fields are still empty", Toast.LENGTH_SHORT).show();
                            }
                        }else if (viewType.equalsIgnoreCase("CLICKED_ITEM")) {

                            dateIsSet = splitAndCompareDate(due_date);
                            if (dateIsSet) {
                                String mId = String.valueOf(detailID);
                                updateDb(description, mDueDate, amountStr,repeatStr, memoStr, daySel, weekee, monthSel, yearSel, mId, alarmUpdateId);
                            } else {
                                return;
                            }

                            }
                    }
                break;
            case R.id.tIetDueDate:
                pickDate(this);
                break;
            case R.id.tIetRepeat:
                chooseRepeatDialog();
                break;
            default:
                break;
        }

    }

    private void chooseRepeatDialog (){

        AlertDialog.Builder builder = new AlertDialog.Builder(AddNewBillActivity.this);

        builder.setTitle("Select Your Choice");

        builder.setSingleChoiceItems(repeatFreq, 0, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                Log.e(TAG, "onClick: repaet freq sele = " + repeatFreq[item]);
                repeat.setText(repeatFreq[item]);

                alertDialog1.dismiss();
            }
        });
        alertDialog1 = builder.create();
        alertDialog1.show();

    }

    private void pickDate (Context context) {
        DatePickerDialog datePickerDialog = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            datePickerDialog = new DatePickerDialog(context);
        } else {
            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            datePickerDialog = new DatePickerDialog(context, this, year, month, day);
        }
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

//        compareDate(i, i1, i2);
        String slashedDate = (i2 + "/" + i1 + "/" + i);
        dateIsSetNew = compareDate(i, i1, i2);
//        dateIsSet = splitAndCompareDate(slashedDate);
    }

    private boolean compareDate (int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        int currentYear = c.get(Calendar.YEAR);
        int currentMonth = c.get(Calendar.MONTH);
        int currentDay = c.get(Calendar.DAY_OF_MONTH);

        boolean dateSet = false;

        if (year < currentYear) {
            dueDate.setError("");
            Toast.makeText(this, "Due Date cannot be in the past", Toast.LENGTH_SHORT).show();
            dateSet = false;
        } else if ((year >= currentYear) && (month >= currentMonth)) {

            if ((month == currentMonth) && (day < currentDay)) {
                dueDate.setError("invalid Due date");
                Toast.makeText(this, "Due Date cannot be in the past", Toast.LENGTH_SHORT).show();
                dateSet = false;
            } else {

                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DAY_OF_MONTH, day);

                int weekSel = cal.get(Calendar.WEEK_OF_MONTH);
                int dayMontSel = cal.get(Calendar.DAY_OF_MONTH);

                String daye, monthee;

                daye = FormatUtils.dayFormatHelper(day);
                monthee = FormatUtils.monthFormatHelper(month);


                daySelN = String.valueOf(dayMontSel);
                weekeeN = String.valueOf(weekSel);
                monthSelN= String.valueOf(month + 1);
                yearSelN = String.valueOf(year);

                dueDate.setError(null);
                dueDate.setText(daye + "/" + monthee + "/" + year);
                dateSet = true;
            }
        } else {
            dueDate.setError("");
            Toast.makeText(this, "Due Date cannot be in the past", Toast.LENGTH_SHORT).show();
        }
        return dateSet;

    }



    private void insertBillToDb (String desc, long dueDate, String amount, String repeatFreq,
                                 String memo, String daySel, String weekSel, String monthSel, String yearSel, long dbAlarmId) {

        ContentValues value = new ContentValues();
        value.put(ChartBillContract.Bills.BILL_DESCRIPTION, desc);
        value.put(ChartBillContract.Bills.BILL_DUE_DATE, dueDate);
        value.put(ChartBillContract.Bills.BILL_REPEAT_FREQ, repeatFreq);
        value.put(ChartBillContract.Bills.BILL_AMOUNT, amount);
        value.put(ChartBillContract.Bills.BILL_MEMO, memo);
        value.put(ChartBillContract.Bills.BILL_STATUS, STATUS);
        value.put(ChartBillContract.Bills.BILL_DUE_DAY, daySel);
        value.put(ChartBillContract.Bills.BILL_DUE_MONTH, monthSel);
        value.put(ChartBillContract.Bills.BILL_DUE_WEEK, weekSel);
        value.put(ChartBillContract.Bills.BILL_DUE_YEAR, yearSel);
        value.put(ChartBillContract.Bills.BILL_ALARM_ID, dbAlarmId);

        getContentResolver().insert(ChartBillContract.Bills.CONTENT_URI, value);
        Toast.makeText(this, "Bill Successfully saved", Toast.LENGTH_SHORT).show();
        int day = Integer.parseInt(daySel);
        int month = Integer.parseInt(monthSel);
        int year = Integer.parseInt(yearSel);
        setUpNotification(dbAlarmId, repeatFreq, day, month, year, hour, min, desc);

    }

    private void updateDb (String desc, long dueDate, String amount, String repeatFreq,
                                 String memo, String daySel, String weekSel, String monthSel, String yearSel, String itemId, long dbAlarmId) {

        ContentValues value = new ContentValues();
        value.put(ChartBillContract.Bills.BILL_DESCRIPTION, desc);
        value.put(ChartBillContract.Bills.BILL_DUE_DATE, dueDate);
        value.put(ChartBillContract.Bills.BILL_REPEAT_FREQ, repeatFreq);
        value.put(ChartBillContract.Bills.BILL_AMOUNT, amount);
        value.put(ChartBillContract.Bills.BILL_MEMO, memo);
        value.put(ChartBillContract.Bills.BILL_STATUS, STATUS);
        value.put(ChartBillContract.Bills.BILL_DUE_DAY, daySel);
        value.put(ChartBillContract.Bills.BILL_DUE_MONTH, monthSel);
        value.put(ChartBillContract.Bills.BILL_DUE_WEEK, weekSel);
        value.put(ChartBillContract.Bills.BILL_DUE_YEAR, yearSel);

        getContentResolver().update(ChartBillContract.Bills.CONTENT_URI, value, ChartBillContract.Bills._ID + "=?", new String[]{itemId});
        int day = Integer.parseInt(daySel);
        int month = Integer.parseInt(monthSel);
        int year = Integer.parseInt(yearSel);
        updateNotification(dbAlarmId, repeatFreq, day, month, year, hour, min, desc);
        Toast.makeText(this, "Bill Successfully updated", Toast.LENGTH_SHORT).show();
        finish();

    }

    private boolean splitAndCompareDate (String slasshedDate) {

        boolean dateSet;

        String[] slashOps = slasshedDate.split("/");
        int day = Integer.parseInt(slashOps[0]);
        int month = Integer.parseInt(slashOps[1]);
        int year = Integer.parseInt(slashOps[2]);

        Calendar c = Calendar.getInstance();
        int currentYear = c.get(Calendar.YEAR);
        int currentMonth = ((c.get(Calendar.MONTH)) + 1);
        int currentDay = c.get(Calendar.DAY_OF_MONTH);

        Log.e(TAG, "splitAndCompareDate: month = " + currentMonth);

        if (year < currentYear) {
            dueDate.setError("");
            Toast.makeText(this, "Due Date cannot be in the past", Toast.LENGTH_SHORT).show();
            dueDate.setText("");
            dateSet = false;
        } else if ((year >= currentYear) && (month >= currentMonth)) {

            if ((month == (currentMonth)) && (day < currentDay)) {
                dueDate.setError("");
                Toast.makeText(this, "Due Date cannot be in the past", Toast.LENGTH_SHORT).show();
                dueDate.setText("");
                dateSet = false;
            } else {

                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month - 1);
                cal.set(Calendar.DAY_OF_MONTH, day);

                int weekSel = cal.get(Calendar.WEEK_OF_MONTH);
                int dayMontSel = cal.get(Calendar.DAY_OF_MONTH);
                int weekFirstDay = cal.getFirstDayOfWeek();
                Log.e(TAG, "compareDate: long db date " + dayMontSel + " selected date week = " + day + weekFirstDay);

                daySel = String.valueOf(dayMontSel);
                weekee = String.valueOf(weekSel);
                monthSel = String.valueOf(month);
                yearSel = String.valueOf(year);

                Log.e(TAG, "splitAndCompareDate: weeksel on update = " + weekee );

                String daye = FormatUtils.dayFormatHelper(day);
                String monthee = FormatUtils.monthFormatHelper(month);

                dueDate.setError(null);
                dueDate.setText(daye + "/" + monthee + "/" + year);
                dateSet = true;

            }
        } else {
            dueDate.setError("");
            Toast.makeText(this, "Due Date cannot be in the past", Toast.LENGTH_SHORT).show();
            dueDate.setText("");
            dateSet = false;
        }
        return dateSet;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.e(TAG, "onCreateLoader: details of addnew = " + dataUri );

        return new CursorLoader(this,
                dataUri,
                ChartBillContract.Bills.PROJECTION_ALL,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (!data.moveToFirst()) {
            return;
        }

        detailDesc = data.getString(data.getColumnIndex(ChartBillContract.Bills.BILL_DESCRIPTION));
        desc.setText(detailDesc);
        dueDate.setText(FormatUtils.getRecyclerReadableDate(Long.parseLong(data.getString(data.getColumnIndex(ChartBillContract.Bills.BILL_DUE_DATE)))));
        String amt =data.getString(data.getColumnIndex(ChartBillContract.Bills.BILL_AMOUNT));
        if (!TextUtils.isEmpty(amt)) {
            amount.setText(amt);
        }
        repeat.setText(data.getString(data.getColumnIndex(ChartBillContract.Bills.BILL_REPEAT_FREQ)));
        String mem = data.getString(data.getColumnIndex(ChartBillContract.Bills.BILL_MEMO));
        if (!TextUtils.isEmpty(mem)) {
            memo.setText(mem);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        getSupportLoaderManager().restartLoader(DETAIL_LOADER, null, this);
    }

    private void setUpNotification (long alarmsId, String repeatFreq, int day, int month, int year, int hour, int min, String description) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
        int alarmId = (int) alarmsId;
        notificationIntent.putExtra("message", description);
        notificationIntent.putExtra("reqCode", alarmId);
        notificationIntent.addCategory("android.intent.category.DEFAULT");
        //prevent receiver form being killed
/*        ComponentName receiver = new ComponentName(this, AlarmReceiver.class);
        PackageManager pm = this.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);*/

        boolean alarmRunning = (PendingIntent.getBroadcast(this, alarmId, notificationIntent, PendingIntent.FLAG_NO_CREATE) != null);

        if (!alarmRunning) {
            PendingIntent broadcast = PendingIntent.getBroadcast(this, alarmId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_MONTH, day);
            cal.set(Calendar.MONTH, month - 1);
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.HOUR_OF_DAY, hour);
            cal.set(Calendar.MINUTE, min);

            Log.e(TAG, "setUpNotification: addNew = " + " cal in mill " +FormatUtils.getRecyclerReadableDate(cal.getTimeInMillis()));
            Calendar today = Calendar.getInstance();
            long todaytime = today.getTimeInMillis();
            int newHour = today.get(Calendar.HOUR_OF_DAY) + 2;
            if (cal.getTimeInMillis() <= todaytime) {
                cal.set(Calendar.HOUR_OF_DAY, newHour);
                Log.e(TAG, "setUpNotification: new date and timme = "  + FormatUtils.getRecyclerReadableDate(cal.getTimeInMillis()));
            }

            if (repeatFreq.equalsIgnoreCase("do not repeat")) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);
            } else {
                int freq = 1;
                switch (repeatFreq) {
                    case "Weekly" :
                        freq = 7;
                        break;
                    case "Monthly":
                        freq = 30;
                        break;
                    case "Yearly":
                        freq = 365;
                        break;
                    default:
                        break;
                }
                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY * freq, broadcast);
            }
        }

        Intent homeIntent = new Intent(AddNewBillActivity.this, NewBillActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);



    }
    private void updateNotification (long alarmsId, String repeatFreq, int day, int month, int year, int hour, int min, String description) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
        int alarmId = (int) alarmsId;
        notificationIntent.putExtra("message", description);
        notificationIntent.putExtra("reqCode", alarmId);
        notificationIntent.addCategory("android.intent.category.DEFAULT");

        // If the alarm has been set, cancel it.
        if (alarmManager!= null) {
            alarmManager.cancel(PendingIntent.getBroadcast(this, alarmId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        }
        setUpNotification(alarmsId, repeatFreq, day, month, year, hour, min, description);
//        finish();
    }
}
