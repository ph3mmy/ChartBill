package com.jcedar.chartbills.helper;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.preference.DialogPreference;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by OLUWAPHEMMY on 3/16/2017.
 */

public class TimePreference extends DialogPreference {

    private int lastHour;
    private int lastMinute;
    private TimePicker picker;

    public TimePreference(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        setDialogTitle("");
        setPositiveButtonText(ctx.getString(android.R.string.ok));
        setNegativeButtonText(ctx.getString(android.R.string.cancel));
    }

    @Override
    protected View onCreateDialogView() {
        picker = new TimePicker(getContext());
        picker.setIs24HourView(false);
        return picker;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            picker.setCurrentHour(lastHour);
            picker.setCurrentMinute(lastMinute);
        } else {
            picker.setHour(lastHour);
            picker.setMinute(lastMinute);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (positiveResult) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                lastHour = picker.getCurrentHour();
                lastMinute = picker.getCurrentMinute();
            } else {
                lastHour = picker.getHour();
                lastMinute = picker.getMinute();
            }
            String time = String.valueOf(lastHour) + ":" + String.valueOf(lastMinute);
            if (callChangeListener(time)) {
                persistString(time);
            }
        }
        setSummary(getSummary());
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        String time;
        if (restoreValue) {
            if (defaultValue == null) {
                time = getPersistedString("00:00");
            } else {
                time = getPersistedString(defaultValue.toString());
            }
        } else {
            time = defaultValue.toString();
        }
        lastHour = getHour(time);
        lastMinute = getMinute(time);
        setSummary(getSummary());
    }

    public static int getHour(String time) {
        String[] pieces = time.split(":");
        return Integer.parseInt(pieces[0]);
    }

    public static int getMinute(String time) {
        String[] pieces = time.split(":");
        return Integer.parseInt(pieces[1]);
    }

    @Override
    public CharSequence getSummary() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, lastHour, lastMinute);
        return DateFormat.getTimeFormat(getContext()).format(new Date(cal.getTimeInMillis()));
    }

}