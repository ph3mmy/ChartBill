package com.jcedar.paperbag.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jcedar.paperbag.R;
import com.jcedar.paperbag.fragment.AdminFragment;
import com.jcedar.paperbag.fragment.BuyerFragment;
import com.jcedar.paperbag.fragment.SellerFragment;
import com.jcedar.paperbag.helper.MyUtils;

import java.util.List;
import java.util.Map;

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
    static String acti;

    private Toolbar toolbar;
    private static String uuid;

    @Override
    protected void onStart() {
        super.onStart();
        Bundle b = getIntent().getExtras();
        if (b != null) {
            role = b.getString("role");
//            uuid = b.getString("uuid");
//            Log.e(TAG, "Returned role from login = " + role + "uuid " + uuid);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
        }

//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);


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

            String args = getIntent().getStringExtra("role");
//            String mm = args.getString("role");
            Log.e(TAG, "onCreate: returned args = " + args + " mm returned = " );
            if (args != null) {
//                Bundle mm = getIntent().
                bundle = getIntent().getStringExtra("role");
                acti = getIntent().getStringExtra("activation");
//                acti = args.getString("activation");
                String uuidL = getIntent().getStringExtra("uuid");
//                String uuidL = args.getString("uuid");
                Log.e(TAG, "new role is " + bundle + "uuid from login " + uuidL + " activation Id = " + acti);
            } else {
                bundle = MyUtils.getPersonal(this);
                acti = MyUtils.getActivationId(this);
                Log.e(TAG, "new role when bundle is empty  " + bundle + " activation id = " + acti);
            }
        }

        TextView error = (TextView) findViewById(R.id.tvErrorMain);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Log.e(TAG, "onCreate: USER = " + user );

        if (user != null && user.contains("admin")) {
            toolbar.setTitle("Admin DashBoard");
            setSupportActionBar(toolbar);
            AdminFragment fragment = new AdminFragment();
            Bundle bb = new Bundle();
            bb.putString("user", user);
            fragment.setArguments(bb);
            transaction.replace(R.id.frame, fragment, "Admin");
            transaction.commit();
        } else if (bundle != null && (!acti.equalsIgnoreCase("0"))) {
            if (bundle.equalsIgnoreCase("1")) {
                toolbar.setTitle("PaperBag - Seller");
                setSupportActionBar(toolbar);
                SellerFragment fragment = new SellerFragment();
                Bundle bb = new Bundle();
                bb.putString("role", bundle);
                bb.putString("user", user);
                bb.putString("uuid", uuid);
                fragment.setArguments(bb);
                transaction.replace(R.id.frame, fragment, "Seller");
                transaction.commit();
            } else if (bundle.equalsIgnoreCase("2")) {
                toolbar.setTitle("PaperBag - Menu");
                setSupportActionBar(toolbar);
                BuyerFragment fragment = new BuyerFragment();
                Bundle bb = new Bundle();
                bb.putString("role", bundle);
                bb.putString("user", user);
                bb.putString("uuid", uuid);
                fragment.setArguments(bb);
                transaction.replace(R.id.frame, fragment, "Buyer");
                transaction.commit();
            }else {
                toolbar.setTitle("Account Not Activated");
                setSupportActionBar(toolbar);
                error.setVisibility(View.VISIBLE);
            }
        }
        else {
            toolbar.setTitle("Account Not Activated");
            setSupportActionBar(toolbar);
            error.setVisibility(View.VISIBLE);
        }

        // Handle possible data accompanying notification message.
        if (getIntent().getExtras() != null) {
            Bundle mm = getIntent().getExtras();
                    String sender = mm.getString("OrderActivitySender");

            for (String key : getIntent().getExtras().keySet()) {
                String value = getIntent().getExtras().getString(key);

                if (key.equals("OrderActivity")) {
                    Intent intent = new Intent(this, MsgActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("value", value);
                    intent.putExtra("sender", sender);
                    startActivity(intent);
//                    finish();
                }

            }
        }

    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("token");
            String ref = intent.getStringExtra("onRefreshed");
            Log.e(TAG, "broadcasted message : " + message + " role = " + role + "User = " + user);
            if (ref.equalsIgnoreCase("TRUE")){
                sendToken(role, uuid, user, message);
            }
        }
    };

    private void sendToken (String role, String uuid, String user, String token) {
        if (!user.contains("admin") && (role.equalsIgnoreCase("1"))){
            DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
            mRef.child("Stores").child(uuid).child("storeSellerToken").setValue(token);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("token-event"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

//        MenuItem menuItem = menu.findItem(R.id.action_scan);
//        MenuItem addUser = menu.findItem(R.id.action_add_user);
        MenuItem signOut = menu.findItem(R.id.action_sign_out);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_user_cart) {
//            startActivity(new Intent(MainActivity.this, ScanTicketActivity.class));
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



