package com.jcedar.chartbills.activity;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.jcedar.chartbills.R;
import com.jcedar.chartbills.helper.FormatUtils;
import com.jcedar.chartbills.provider.ChartBillContract;

import java.util.Calendar;


/**
 * Created by OLUWAPHEMMY on 3/15/2017.
 */

public class BillDetailsActivity extends AppCompatActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = BillDetailsActivity.class.getSimpleName();


    private ImageView imBack, imDel, imDone;
    private TextInputEditText desc, dueDate, amount, memo, tietRepeat;
    Spinner repeatSpinner;
    TextInputLayout tilRepeat;
    LinearLayout spinnerLayout;

    String[] repeatFreq = {"Monthly", "Weekly", "Yearly", "Does not repeat"};
    String selectedSpinnerFreq;
    String daySel, monthSel, weekee, yearSel;
    long detailID;
    String viewType, detailDesc;
    Uri dataUri;
    boolean dateIsSet = false;
    private static final int DETAIL_LOADER = 0;

    private static final String STATUS = "upcoming";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);


        imBack = (ImageView) findViewById(R.id.ivBack);
        imDel = (ImageView) findViewById(R.id.ivDelete);
        imDone = (ImageView) findViewById(R.id.ivDone);

        desc = (TextInputEditText) findViewById(R.id.tietDesc);
        dueDate = (TextInputEditText) findViewById(R.id.tIetDueDate);
        amount = (TextInputEditText) findViewById(R.id.tIetAmount);
        memo = (TextInputEditText) findViewById(R.id.tIetMemo);
        tietRepeat = (TextInputEditText) findViewById(R.id.tIetRepeat);
        tilRepeat = (TextInputLayout) findViewById(R.id.tilRepeat);/*
        spinnerLayout = (LinearLayout) findViewById(R.id.repeatSpinnerLayout);
        repeatSpinner = (Spinner) findViewById(R.id.repeat_spinner);*/

        spinnerLayout.setVisibility(View.GONE);
        tilRepeat.setVisibility(View.VISIBLE);
        imDel.setVisibility(View.VISIBLE);

            dataUri = ChartBillContract.Bills.buildCurrencyUri(detailID);
            getSupportLoaderManager().restartLoader(DETAIL_LOADER, null, this);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, repeatFreq);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        repeatSpinner.setAdapter(spinnerAdapter);

        imBack.setOnClickListener(this);
        imDone.setOnClickListener(this);
        imDel.setOnClickListener(this);
        desc.setOnClickListener(this);
        dueDate.setOnClickListener(this);
        amount.setOnClickListener(this);
        memo.setOnClickListener(this);

        repeatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedSpinnerFreq = adapterView.getSelectedItem().toString();
                Log.e(TAG, "onItemSelected: " + selectedSpinnerFreq);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



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
                Toast.makeText(this, detailDesc + " successfully deleted", Toast.LENGTH_SHORT).show();
                finish();

                break;
            case R.id.ivDone:

                String description = desc.getText().toString();
                String due_date = dueDate.getText().toString();
                String amountStr = amount.getText().toString();
                String memoStr = memo.getText().toString();

                boolean cancel = false;
                View focusView = null;

                if (TextUtils.isEmpty(description)) {
                    desc.setError("Description cannot be empty");
                    focusView = desc;
                    cancel = true;
                } else if (TextUtils.isEmpty(due_date)) {
                    dueDate.setError("Due Date cannot be empty");
                    focusView = dueDate;
                    cancel = true;
                } else if (TextUtils.isEmpty(amountStr)) {
                    amount.setError("Amount cannot be empty");
                    focusView = amount;
                    cancel = true;
                }
                if (cancel) {
                    focusView.requestFocus();
                } else {
                    long mDueDate = FormatUtils.dbDate(due_date);
                    String mId = String.valueOf(detailID);
                    dateIsSet = splitAndCompareDate(due_date);
                    if (dateIsSet) {
                        updateDb(description, mDueDate, amountStr, selectedSpinnerFreq, memoStr, daySel, weekee, monthSel, yearSel, mId);
                        desc.setText("");
                        dueDate.setText("");
                        amount.setText("");
                        memo.setText("");
                        desc.requestFocus();
                    }

                }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    private void updateDb (String desc, long dueDate, String amount, String repeatFreq,
                           String memo, String daySel, String weekSel, String monthSel, String yearSel, String itemId) {

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
        int currentMonth = c.get(Calendar.MONTH);
        int currentDay = c.get(Calendar.DAY_OF_MONTH);

        Log.e(TAG, "splitAndCompareDate: month = " + currentMonth);

        if (year < currentYear) {
            dueDate.setError("");
            Toast.makeText(this, "Due Date cannot be in the past", Toast.LENGTH_SHORT).show();
            dateSet = false;
        } else if ((year >= currentYear) && (month >= currentMonth)) {

            if ((month == (currentMonth)) && (day < currentDay)) {
                dueDate.setError("");
                Toast.makeText(this, "Due Date cannot be in the past", Toast.LENGTH_SHORT).show();
                dateSet = false;
            } else {

                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DAY_OF_MONTH, day);

                int weekSel = cal.get(Calendar.WEEK_OF_MONTH);
                int dayMontSel = cal.get(Calendar.DAY_OF_MONTH);
                int weekFirstDay = cal.getFirstDayOfWeek();
                Log.e(TAG, "compareDate: long db date " + dayMontSel + " selected date week = " + day + weekFirstDay);

                daySel = String.valueOf(dayMontSel);
                weekee = String.valueOf(weekSel);
                monthSel = String.valueOf(month + 1);
                yearSel = String.valueOf(year);

                String daye = FormatUtils.dayFormatHelper(day);
                String monthee = FormatUtils.monthFormatHelper(month);

                dueDate.setError(null);
                dueDate.setText(daye + "/" + monthee + "/" + year);
                dateSet = true;

            }
        } else {
            dueDate.setError("");
            Toast.makeText(this, "Due Date cannot be in the past", Toast.LENGTH_SHORT).show();
            dateSet = false;
        }
        return dateSet;
    }


}
