package com.jcedar.tixee.helper;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jcedar.tixee.R;
import com.jcedar.tixee.model.Ticket;
import com.jcedar.tixee.model.User;
import com.jcedar.tixee.model.UserCampaign;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by OLUWAPHEMMY on 2/14/2017.
 */

public class AddUserDialog extends DialogFragment implements View.OnClickListener{
    private static final String TAG = AddUserDialog.class.getSimpleName();
    private EditText etUserName, etPassword, etTicketQty;
    private Button addUser;
    private ImageView closeDialog;
    private TextView ticKetAvaTV, dialogTitle;
    private Spinner campaignSpinner, agentTypeSpinner;

    private FirebaseAuth mFirebaseAuth;
    private LinearLayout dialogAv, imageChooserLayput;

    DatabaseReference mFirebaseDatabase;
    static Map<String, String> nameAndKey;

    String[] agentType = {"Primary", "Secondary"};

    String editDialog, cName, cTicket, userId, cAv, imageString;

    String user, campaignID, camName, mm;
    int campTicketAva, adminUsed, campAvaTicket;
    TextInputLayout tilPass;

    public AddUserDialog() {

    }


    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;

            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();

//        dialogType = getArguments().getInt("position");
        editDialog = getArguments().getString("user");
        Log.e(TAG, "ret Arg = " + editDialog);
        userId = getArguments().getString("uid");
        cName = getArguments().getString("cName");
        cTicket = getArguments().getString("cTicket");


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.dialog_add_user, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        dialogTitle = (TextView) rootView.findViewById(R.id.tvEditUser);
        tilPass = (TextInputLayout) rootView.findViewById(R.id.tilUserPass);
        addUser = (Button) rootView.findViewById(R.id.btAddNewUser);
        etUserName = (EditText) rootView.findViewById(R.id.etDialogUserEmail);
        etPassword = (EditText) rootView.findViewById(R.id.etDialogUserPassword);
        etTicketQty = (EditText) rootView.findViewById(R.id.etDialogUserTicketQty);
        campaignSpinner = (Spinner) rootView.findViewById(R.id.spinnerUserCampaign);
        agentTypeSpinner = (Spinner) rootView.findViewById(R.id.spinnerAgentType);
        closeDialog = (ImageView) rootView.findViewById(R.id.ivCloseDialog);
        ticKetAvaTV = (TextView) rootView.findViewById(R.id.tvUserAvaTicket);

        nameAndKey = new HashMap<>();


        if (editDialog != null) {
            if (editDialog.equalsIgnoreCase("EDIT_DIALOG")) {
                tilPass.setVisibility(View.GONE);
                etPassword.setVisibility(View.GONE);
                etTicketQty.setText(cTicket);
                etUserName.setText(cName);
                etUserName.setEnabled(false);
                dialogTitle.setText("EDIT USER");
                addUser.setText("Edit User");
            }
        }

        mFirebaseDatabase.child("Campaign").addValueEventListener(new ValueEventListener() {
            final List<String> campaign = new ArrayList<String>();
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String campTitle  = dataSnapshot1.child("name").getValue(String.class);
                    String key  = dataSnapshot1.getKey();
                    if (!campaign.contains(campTitle)){
                        campaign.add(campTitle);
                        nameAndKey.put(campTitle, key);
                    }
                }

                if (getActivity() != null) {
                    ArrayAdapter<String> camAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, campaign);
                    camAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    campaignSpinner.setAdapter(camAdapter);
                }
        campaignSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                final String sel = campaignSpinner.getSelectedItem().toString();
                camName = sel;

                final String  key = nameAndKey.get(sel);
                mFirebaseDatabase.child("Campaign").child(key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot != null) {
                            campTicketAva = dataSnapshot.child("ticketLeft").getValue(Integer.class);
                            adminUsed = dataSnapshot.child("adminUsed").getValue(Integer.class);
                            campaignID = key;
                            ticKetAvaTV.setText(" " + campTicketAva);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
});


        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_spinner_item, agentType
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        agentTypeSpinner.setAdapter(spinnerAdapter);

        agentTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mm = agentTypeSpinner.getSelectedItem().toString();
                Log.e(TAG, "status spinner selected = " + mm);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        closeDialog.setOnClickListener(this);
        addUser.setOnClickListener(this);

        return rootView;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivCloseDialog:
                dismiss();
                break;
            case R.id.btAddNewUser:
                // Reset errors.
               /* etUserName.setError(null);
                etPassword.setError(null);
                etTicketQty.setError(null);*/

                // Store values at the time of the login attempt.
                String email = etUserName.getText().toString();
                String password = etPassword.getText().toString();
                String ticketAlloc = etTicketQty.getText().toString();
                int mTickAlloc = 0;
                boolean cancel = false;
                View focusView = null;

                if (!editDialog.equalsIgnoreCase("EDIT_DIALOG")) {

                if (campaignSpinner.getChildCount() == 0) {
                    Toast.makeText(getActivity(), "Campaign Spinner not yet populated", Toast.LENGTH_SHORT).show();
                }
                    // Check for a valid password, if the user entered one.
                    if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
                        etPassword.setError(getString(R.string.error_invalid_password));
                        focusView = etPassword;
                        cancel = true;
                    }

                    // Check for a valid email address.
                    if (TextUtils.isEmpty(email)) {
                        etUserName.setError(getString(R.string.error_field_required));
                        focusView = etUserName;
                        cancel = true;
                    } if (!isEmailValid(email)) {
                        etUserName.setError(getString(R.string.error_invalid_email));
                        focusView = etUserName;
                        cancel = true;
                    }
                if (TextUtils.isEmpty(ticketAlloc)) {
                    etTicketQty.setError("Allocated Ticket cannot be empty");
                    focusView = etTicketQty;
                    cancel = true;
                } else if (!TextUtils.isEmpty(ticketAlloc)) {

                    mTickAlloc = Integer.parseInt(ticketAlloc);
                    if (mTickAlloc <= 0) {
                        etTicketQty.setError("Allocated Ticket must be greater than 0");
                        focusView = etTicketQty;
                        cancel = true;
                    } if (mTickAlloc > campTicketAva ) {
                        etTicketQty.setError("You cannot allocate more than available quantity");
                        focusView = etTicketQty;
                        cancel = true;
                    }
                }
                if (cancel) {
                        // There was an error; don't attempt login and focus the first
                        // form field with an error.
                        focusView.requestFocus();
                    } else {

                        String name = extractUserName(email);
                        String role = userRole(mm);
                    int camUsed = adminUsed + mTickAlloc;
                    int camLeft= campTicketAva - mTickAlloc;
                    AddNewUserFragmentInteractionListener listener = (AddNewUserFragmentInteractionListener) getActivity();
                    listener.onAddUserButtonClick(email, password, name, role, camName, mTickAlloc, campaignID, camUsed, camLeft);
                        dismiss();
                    }
                } else {
                    int tickAlloc = Integer.parseInt(ticketAlloc);
                    if (tickAlloc <= 0) {
                        etTicketQty.setError("Allocated Ticket must be greater than 0");
                        focusView = etTicketQty;
                        cancel = true;
                    } if (tickAlloc > campTicketAva ) {
                        etTicketQty.setError("You cannot allocate more than available quantity");
                        focusView = etTicketQty;
                        cancel = true;
                    }
                    else {

//                    mTickAlloc = checkAllocated(ticketAlloc, focusView, cancel);
                        String role = userRole(mm);
                        int camUsed = adminUsed + tickAlloc;
                        int camLeft= campTicketAva - tickAlloc;
                        EditUserFragmentInteractionListener listener = (EditUserFragmentInteractionListener) getActivity();
                        listener.onEditUserButtonClick(userId, role, camName, campaignID,tickAlloc, camUsed, camLeft);
                        dismiss();
                    }
                }
        }
    }

    private int checkAllocated (String ticketAlloc, View focusView, boolean cancel) {

        int tickAlloc = Integer.parseInt(ticketAlloc);
        if (tickAlloc <= 0) {
            etTicketQty.setError("Allocated Ticket must be greater than 0");
            focusView = etTicketQty;
            cancel = true;
        } if (tickAlloc > campTicketAva ) {
            etTicketQty.setError("You cannot allocate more than available quantity");
            focusView = etTicketQty;
            cancel = true;
        }
        return tickAlloc;
    }


    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    private String userRole (String role) {
        String rolex = null;
        if (role.equalsIgnoreCase("Primary")) {
            rolex = "1";
        } else if (role.equalsIgnoreCase("Secondary")) {
            rolex = "2";
        }
        return rolex;
    }

    private String extractUserName (String email) {
        String[] name = email.split("@");
        Log.e(TAG, "Splitted UserName = " + name[0]);
        return name [0];
    }
    public interface EditUserFragmentInteractionListener {
        public void onEditUserButtonClick(String key, String agentType, String NewCampaign, String campId, int noOfTicket, int camUsed, int camLeft);
    }

    public interface AddNewUserFragmentInteractionListener {
        public void onAddUserButtonClick (String email, String password, String name, String role, String campaignName,
                                          int allocTicket, String camId, int camUsed, int camLeft);
    }

}