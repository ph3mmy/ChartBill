package com.jcedar.chartbills.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;

import com.jcedar.chartbills.R;

import java.util.Currency;
import java.util.Locale;


/**
 * Created by OLUWAPHEMMY on 12/7/2016.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener
        /*TimePickerDialog.OnTimeSetListener*/ {

    public static final String TAG = SettingsFragment.class.getSimpleName();
    SharedPreferences sharedPreferences;
    PreferenceScreen timePref;
    static String timeSet;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        onSharedPreferenceChanged(sharedPreferences, getActivity().getResources().getString(R.string.pref_key_notification));
        onSharedPreferenceChanged(sharedPreferences, getActivity().getResources().getString(R.string.pref_key_currency));
        onSharedPreferenceChanged(sharedPreferences, getActivity().getResources().getString(R.string.pref_key_week_start));
//        onSharedPreferenceChanged(sharedPreferences, getActivity().getResources().getString(R.string.pref_set_reminder_time));

        Currency currency = Currency.getInstance(Locale.getDefault());

        Log.e(TAG, "onCreate: SettingsFragment Currency " + currency.getCurrencyCode());

        final CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference(getString(R.string.pref_key_notification));
        final ListPreference weekStartListPref = (ListPreference) findPreference(getString(R.string.pref_key_week_start));
        final ListPreference currencyListPref = (ListPreference) findPreference(getString(R.string.pref_key_currency));

 /*       timePref = (PreferenceScreen) findPreference(getString(R.string.pref_set_reminder_time));
        timePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                pickNotifyTime();
                return true;
            }
        });

        timePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                timePref.setSummary(PrefUtils.getUpdateTime(getActivity()));
                return true;
            }
        });*/


 /*       checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                if (!checkBoxPreference.isChecked()){
                    freqListPref.setShouldDisableView(true);
                }
                return false;
            }
        });*/
    }

/*    private void pickNotifyTime () {
        TimePickerDialog timePickerDialog = null;
            Calendar c = Calendar.getInstance();
            int hrDay = c.get(Calendar.HOUR_OF_DAY);
            int min = c.get(Calendar.MINUTE);
            timePickerDialog = new TimePickerDialog(getActivity(), this, hrDay, min, false);
        timePickerDialog.show();
    }*/


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

        Preference preference =findPreference(s);
        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list.
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(sharedPreferences.getString(s,""));
            // Set the summary to reflect the new value.
            preference.setSummary(
                    index >= 0
                            ? listPreference.getEntries()[index]
                            : null);

        } else if (preference instanceof CheckBoxPreference) {
            CheckBoxPreference checkBoxPreference = (CheckBoxPreference) preference;
            boolean check = checkBoxPreference.isChecked();
//            preference.d
        }

        else {
                        preference.setSummary(sharedPreferences.getString(s,""));
                    }


    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

/*    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
//        timePref.setSummary(i+":"+i1);
        Calendar cc = Calendar.getInstance();
        cc.set(Calendar.HOUR_OF_DAY, i);
        cc.set(Calendar.MINUTE, i1);
        Format sdf = new SimpleDateFormat("h:mm a", Locale.getDefault());
        timePref.setSummary(sdf.format(cc.getTime()));
        PrefUtils.setUpdateTime(getActivity(), sdf.format(cc.getTime()));
    }*/
}
