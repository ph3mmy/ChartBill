package com.jcedar.tixee.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jcedar.tixee.R;
import com.jcedar.tixee.activity.AdminActivity;
import com.jcedar.tixee.helper.MyUtils;
import com.jcedar.tixee.helper.ShowBarcodeDialog;
import com.jcedar.tixee.model.Ticket;
import com.jcedar.tixee.model.UserCampaign;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by OLUWAPHEMMY on 2/17/2017.
 */

public class OtherUserFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = OtherUserFragment.class.getSimpleName();
    View view;
    private TextView welcomeTv, campaignCount, ticketCount, ticketNumber, usedNumber, otherCamp;
    private EditText ticketId, contactName, contactNumber, ticketType;
    private Button genId, genTicket;
    private Spinner campaignSpinner;
    private LinearLayout adminLayout, primaryUserLayout;
    private CardView cdCampaign, cdTicket;
    static Map<String, String> nameAndKey;
    List<String> campaigns;
    View progressBar;
    ScrollView scroll;
    static String user, campaignID, camName, imageStr, role, userId;
    int campTicketAva;
    int campTicketUsed;


    List<String> ticketKeys = new ArrayList<String>();
    List<String> campaignKeys = new ArrayList<String>();
    private DatabaseReference mFirebaseDatabase;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        role = getArguments().getString("role");
        user = getArguments().getString("user");
        userId = getArguments().getString("uuid");
        Log.e(TAG, "role returned from mainActivity = " + role + "uuid = " + userId);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        
        view = inflater.inflate(R.layout.fragment_other, container, false);

        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference("User");

        initializeViews();


        welcomeTv.setText("Welcome " + "(" +user + ")");
        switchUserView(role);

        primaryUserLayout.setOnClickListener(this);
        genTicket.setOnClickListener(this);
        genId.setOnClickListener(this);


        nameAndKey = new HashMap<String, String>();
        campaigns = new ArrayList<String>();

        mFirebaseDatabase.child(userId).child("userCampaign").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG, "campaign name dara " + dataSnapshot);
                final String campTitle = dataSnapshot.child("campaignName").getValue(String.class);
                final String key = dataSnapshot.child("campaignId").getValue(String.class);

                Log.e(TAG, "other frag " + campTitle + " other key " + key);

                otherCamp.setText(campTitle);

                campTicketAva = dataSnapshot.child("userNumberOfTicket").getValue(Integer.class);
                campTicketUsed = dataSnapshot.child("userUsedTicket").getValue(Integer.class);
                ticketNumber.setText(" " + campTicketAva);
                Log.e(TAG, "ret spin used = " + campTicketUsed);
                usedNumber.setText("" + campTicketUsed);

                DatabaseReference aFirebaseDatabase = FirebaseDatabase.getInstance().getReference();
                aFirebaseDatabase.child("Campaign").child(key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Log.e(TAG, "other frag camp key snapshot = " + dataSnapshot);

                        if (dataSnapshot != null) {
                            imageStr = dataSnapshot.child("imageUrl").getValue(String.class);
                            campaignID = key;
                            camName = campTitle;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                /*ArrayAdapter<String> camAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, campaigns);
                camAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                campaignSpinner.setAdapter(camAdapter);

                campaignSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        final String sel = campaignSpinner.getSelectedItem().toString();
                        camName = sel;

                        for (String s : nameAndKey.keySet()) {
                            Log.e(TAG, "items in the map key = " + s + " name " + nameAndKey.get(s));
                        }

                        final String key = nameAndKey.get(sel);
                        Log.e(TAG, "returned key = " + key);

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });*/

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return view;
    }

    private void initializeViews() {

        welcomeTv = (TextView) view.findViewById(R.id.tvWelcome);
        campaignCount = (TextView) view.findViewById(R.id.campaign_count);
        ticketCount = (TextView) view.findViewById(R.id.ticket_count);
        ticketNumber = (TextView) view.findViewById(R.id.tvAvailTick);
        usedNumber = (TextView) view.findViewById(R.id.tvUsedTicket);
        otherCamp = (TextView) view.findViewById(R.id.tvOtherCampaign);

        ticketId = (EditText) view.findViewById(R.id.etTicketId);
        ticketType = (EditText) view.findViewById(R.id.etTicketType);
        contactName = (EditText) view.findViewById(R.id.etTicketContactName);
        contactNumber = (EditText) view.findViewById(R.id.etTicketContactNumber);

        progressBar = (ProgressBar) view.findViewById(R.id.main_progress);
        scroll = (ScrollView) view.findViewById(R.id.scrollMain);

        genId = (Button) view.findViewById(R.id.btGetId);
        genTicket = (Button) view.findViewById(R.id.btGenTicket);

        adminLayout = (LinearLayout) view.findViewById(R.id.admin_layout);
        primaryUserLayout = (LinearLayout) view.findViewById(R.id.primary_user_layout);

        cdCampaign = (CardView) view.findViewById(R.id.cDViewCampaign);
        cdTicket = (CardView) view.findViewById(R.id.cDViewTicket);

    }

    private void switchUserView (String role ) {
        if (role != null) {
                Log.e(TAG, " role inside switch = " + role);
                if (role.equalsIgnoreCase("1")) {
                    primaryUserLayout.setVisibility(View.VISIBLE);
                    adminLayout.setVisibility(View.GONE);
                } else if (role.equalsIgnoreCase("2")) {
                    adminLayout.setVisibility(View.GONE);
                    primaryUserLayout.setVisibility(View.GONE);
                }

        }

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.primary_user_layout:
                Intent adminIntent = new Intent(getActivity(), AdminActivity.class);
                adminIntent.putExtra("adminCamp", "PRIMARY_LAYOUT");
                adminIntent.putExtra("pryUserAva", campTicketAva);
                adminIntent.putExtra("pryUserUsed", campTicketUsed);
                adminIntent.putExtra("pryUserCamName", camName);
                startActivity(adminIntent);
                break;
            case R.id.btGetId:
                String randomId = MyUtils.getRandomString(16);
                Log.e(TAG, "generated rand = " + randomId);
                ticketId.setText(randomId);
                genId.setVisibility(View.GONE);
                ticketId.setVisibility(View.VISIBLE);
                break;
            case R.id.btGenTicket:
                String tickEdit = ticketId.getText().toString();
                String tickType = ticketType.getText().toString();
                String tickContact = contactName.getText().toString();
                String tickPhone = contactNumber.getText().toString();
                int leftTicket = campTicketAva - campTicketUsed;
                if (campTicketAva <= 0) {
                    Toast.makeText(getActivity(), "You have used up your Ticket Quota", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(tickEdit)) {
                    Toast.makeText(getActivity(), "Generate ID first", Toast.LENGTH_SHORT).show();
                    genId.setError("Generate ID first");
                } else if (TextUtils.isEmpty(tickType)) {
                    ticketType.setError("Ticket type cannot be empty");
                } else if (!MyUtils.checkNetworkAvailability(getActivity())) {
                    MyUtils.networkDialog(getActivity()).show();
                } else {

                    String saveTime = MyUtils.getReadableDate();
                    hideSoftKeyboard(getActivity());
                    sendTicketToServer(tickEdit, tickType, tickContact, tickPhone, campaignID, camName, user, saveTime);
                    Log.e(TAG, "other image string = " + imageStr);
                    showGenBarDialog(tickEdit, camName, tickContact, tickPhone, tickType, imageStr);
                    doTicketMath(campaignID, userId);
                    contactNumber.setText("");
                    contactNumber.setText("");
                    ticketType.setText("");
                    ticketId.setText("");
                    ticketId.setVisibility(View.GONE);
                    genId.setError(null);
                    genId.setVisibility(View.VISIBLE);

                }

        }
    }



    private void showGenBarDialog (String tickId, String campaignName, String conName, String conPhone, String tickType, String imageUrl) {
        ShowBarcodeDialog mDialog = new ShowBarcodeDialog();
        if (conName.isEmpty()) {
            conName = "Nil";
        }
        if (conPhone.isEmpty()) {
            conPhone = "Nil";
        }
        Bundle bundle = new Bundle();
        bundle.putString("camName", campaignName);
        bundle.putString("conName", conName);
        bundle.putString("conPhone", conPhone);
        bundle.putString("tickType", tickType);
        bundle.putString("imageUrl", imageUrl);
        bundle.putString("tickId", tickId);
        mDialog.setArguments(bundle);
        mDialog.setCancelable(false);
        mDialog.show(getFragmentManager(), "gen_dialog");

    }

    private void hideSoftKeyboard(Activity activity) {
        View v = activity.getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void sendTicketToServer (String ticketID, String ticketType, String contactName, String contactPhone, String campID, String campName, String genUser, String genDate) {
        DatabaseReference aTicketFirebase = FirebaseDatabase.getInstance().getReference("Ticket");
        String status = "Valid";

        if (contactName.isEmpty()) {
            contactName = "Nil";
        }
        if (contactPhone.isEmpty()) {
            contactPhone = "Nil";
        }

        DatabaseReference db_ref = aTicketFirebase.push();
        String userId = aTicketFirebase.push().getKey();
        Log.e(TAG, "the gotten key " + userId);
        Ticket ticket = new Ticket(userId, campID, ticketID, status, ticketType, contactName, contactPhone, campName, genUser, genDate);
        aTicketFirebase.child(ticketID).setValue(ticket);
    }

    private void doTicketMath (final String camID, final String userId){

        final DatabaseReference aTicketFirebase = FirebaseDatabase.getInstance().getReference("User");
        aTicketFirebase.child(userId).child("userCampaign").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int campTicketAva  = dataSnapshot.child("userNumberOfTicket").getValue(Integer.class);
                int leftAva = campTicketAva - 1;
                aTicketFirebase.child(userId).child("userCampaign").child("userNumberOfTicket").setValue(leftAva);

                int campTicketUsed  = dataSnapshot.child("userUsedTicket").getValue(Integer.class);
                int leftUsed = campTicketUsed + 1;
                aTicketFirebase.child(userId).child("userCampaign").child("userUsedTicket").setValue(leftUsed);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
