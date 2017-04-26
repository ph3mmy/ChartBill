package com.jcedar.tixee.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ScrollingView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.WriterException;
import com.jcedar.tixee.R;
import com.jcedar.tixee.fragment.AdminFragment;
import com.jcedar.tixee.fragment.CampaignFragment;
import com.jcedar.tixee.fragment.OtherUserFragment;
import com.jcedar.tixee.fragment.PrimaryFragment;
import com.jcedar.tixee.fragment.TicketFragment;
import com.jcedar.tixee.helper.MyUtils;
import com.jcedar.tixee.helper.ShowBarcodeDialog;
import com.jcedar.tixee.model.Campaign;
import com.jcedar.tixee.model.Ticket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by OLUWAPHEMMY on 2/2/2017.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDatabase;

    private TextView welcomeTv, campaignCount, ticketCount, ticketNumber, usedNumber;
    private EditText ticketId, contactName, contactNumber, ticketType;
    private Button genId, genTicket;
    private Spinner campaignSpinner;
    private LinearLayout adminLayout, primaryUserLayout;
    private CardView cdCampaign, cdTicket;
    static Map<String, String> nameAndKey;
    List<String> campaigns;
    View progressBar;
    ScrollView scroll;
    static String user, campaignID, camName, imageStr, role, userRole;
    int campTicketAva;
    int campTicketUsed;
    static String bundle;


    List<String> ticketKeys = new ArrayList<String>();
    List<String> campaignKeys = new ArrayList<String>();
    private static String uuid;

    @Override
    protected void onStart() {
        super.onStart();
        Bundle b = getIntent().getExtras();
        if (b != null) {
            role = b.getString("role");
//            uuid = b.getString("uuid");
            Log.e(TAG, "Returned role from login = " + role + "uuid " + uuid);
        }

//        switchUserView(role);

//        new GetMeTicketCount(this).execute();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Tixee - Welcome");
            setSupportActionBar(toolbar);
        }


        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
//        mFirebaseAuth.signOut();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Not logged in, launch the Log In activity
            MyUtils.loadLogInView(this);
        } else {
            user = mFirebaseUser.getEmail();
            uuid = mFirebaseUser.getUid();

            Log.e(TAG, "user id is = " + uuid);

            Bundle args = getIntent().getExtras();
            if (args != null) {
                bundle = args.getString("role");
                String uuidL = args.getString("uuid");
                Log.e(TAG, "new role is " + bundle + "uuid from login " + uuidL);
            } else {
                bundle = MyUtils.getPersonal(this);
                Log.e(TAG, "new role when bundle is empty  " + bundle);
            }
        }

        TextView error = (TextView) findViewById(R.id.tvErrorMain);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (user != null && user.contains("admin")) {

            AdminFragment fragment = new AdminFragment();
            Bundle bb = new Bundle();
            bb.putString("user", user);
            fragment.setArguments(bb);
            transaction.replace(R.id.frame, fragment, "Admin");
            transaction.commit();
        } else if (bundle != null) {
            if ((bundle.equalsIgnoreCase("1")) || (bundle.equalsIgnoreCase("2"))) {

                OtherUserFragment fragment = new OtherUserFragment();
                Bundle bb = new Bundle();
                bb.putString("role", bundle);
                bb.putString("user", user);
                bb.putString("uuid", uuid);
                fragment.setArguments(bb);
                transaction.replace(R.id.frame, fragment, "Other");
                transaction.commit();
            }
        }
        else
            error.setVisibility(View.VISIBLE);
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
        Fragment frg = getSupportFragmentManager().findFragmentByTag("Admin");
        AdminFragment ratee = (AdminFragment) frg;
        @SuppressLint("CommitTransaction")
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.detach(ratee).commitNowAllowingStateLoss();
        @SuppressLint("CommitTransaction")
        FragmentTransaction fts = getSupportFragmentManager().beginTransaction();
        fts.attach(ratee).commitNowAllowingStateLoss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadRate();
        Log.e(TAG, "onresume of mainActivity");
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("my-event"));
    }


    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem menuItem = menu.findItem(R.id.action_scan);
//        MenuItem addUser = menu.findItem(R.id.action_add_user);
        MenuItem signOut = menu.findItem(R.id.action_sign_out);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_scan) {
            startActivity(new Intent(MainActivity.this, ScanTicketActivity.class));
        }

/*        if (id == R.id.action_add_user) {

            startActivity(new Intent(MainActivity.this, AddUserActivity.class));
        }*/
        if (id == R.id.action_sign_out) {
            if (user != null) {
                mFirebaseAuth.signOut();
                MyUtils.removePersonKey(this);
                MyUtils.loadLogInView(this);
            }
        }

        return false;
    }
}



