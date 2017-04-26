package com.jcedar.tixee.helper;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.format.DateUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.jcedar.tixee.activity.LoginActivity;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * Created by OLUWAPHEMMY on 2/8/2017.
 */

public class MyUtils {

    public static  final String PERSON_KEY = "personal_key";
    public static final String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnm";


    public static String getReadableDate () {
//        Date date = new Date(timestamp);
        SimpleDateFormat dateFormat = new SimpleDateFormat("E, MMM d, hh:mm:aa");
//        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
      /*  Date parsedDate = null;
        try {
            parsedDate = dateFormat.parse(dateFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();*/
        return dateFormat.format(Calendar.getInstance().getTime());
        }

    public static AlertDialog networkDialog (final Context c) {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setMessage("No Internet Connectivity detected! Check your Internet Connectivity settings")
                .setCancelable(false)
                .setPositiveButton("Check Settings", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        c.startActivity(new Intent(Settings.ACTION_SETTINGS));
                    }
                })
                .setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        return alert;
    }

    // method for bitmap to base64
    public static String encodeTobase64(Bitmap image) {
        Bitmap image1 = image; //create a copy of the image
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image1.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        Log.d("Image Log:", imageEncoded);
        return imageEncoded;
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    public static void loadLogInView(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }


    public static boolean checkNetworkAvailability(Context c) {
        ConnectivityManager cm =
                (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public static void setPersonKey(Context context, String key){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putString(PERSON_KEY, key).apply();
    }

    public static String getPersonal(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(PERSON_KEY, "0");
    }
    public static void removePersonKey(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().remove(PERSON_KEY).apply();
    }


    /**send broadcast to mainactivity after update*/
    public static void notifyActivity(String data, Context mContext){
        Intent intent = new Intent("my-event");
        intent.putExtra("message", data);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }


    public static String getRandomString(final int sizeOfRandomString)
    {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }
}
