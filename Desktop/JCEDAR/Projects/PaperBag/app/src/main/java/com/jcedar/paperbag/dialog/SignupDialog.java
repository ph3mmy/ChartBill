package com.jcedar.paperbag.dialog;

import android.app.Dialog;
import android.os.Bundle;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jcedar.paperbag.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by OLUWAPHEMMY on 2/14/2017.
 */

public class SignupDialog extends DialogFragment implements View.OnClickListener {
    private static final String TAG = SignupDialog.class.getSimpleName();
    private EditText etUserName, etPassword, etUserMail;
    private EditText etShopName, etShopAddress, etShopPhone;
    private Button addUser;
    private ImageView closeDialog;
    private TextView ticKetAvaTV;
    private Spinner agentTypeSpinner;

    private FirebaseAuth mFirebaseAuth;
    private LinearLayout shopLayout, imageChooserLayput;

    DatabaseReference mFirebaseDatabase;
    static Map<String, String> nameAndKey;

    String[] agentType = {"Buyer", "Seller"};

    String editDialog, cName, cTicket, userId, cAv, imageString;

    String user, campaignID, userType, mm;
    int campTicketAva, adminUsed, campAvaTicket;
    TextInputLayout tilPass;

    public SignupDialog() {

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
/*        editDialog = getArguments().getString("user");
        Log.e(TAG, "ret Arg = " + editDialog);
        userId = getArguments().getString("uid");
        cName = getArguments().getString("cName");
        cTicket = getArguments().getString("cTicket");*/


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.dialog_sign_up, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

//        dialogTitle = (TextView) rootView.findViewById(R.id.tvEditUser);
        addUser = (Button) rootView.findViewById(R.id.btAddNewUser);
        etUserName = (EditText) rootView.findViewById(R.id.etDialogUserName);
        etUserMail = (EditText) rootView.findViewById(R.id.etDialogUserEmail);
        etPassword = (EditText) rootView.findViewById(R.id.etDialogUserPassword);
        agentTypeSpinner = (Spinner) rootView.findViewById(R.id.spinnerUserType);
        closeDialog = (ImageView) rootView.findViewById(R.id.ivCloseDialog);

        shopLayout = (LinearLayout) rootView.findViewById(R.id.layoutShop);
        etShopName = (EditText) rootView.findViewById(R.id.etDialogShopName);
        etShopAddress = (EditText) rootView.findViewById(R.id.etDialogShopAddress);
        etShopPhone = (EditText) rootView.findViewById(R.id.etDialogShopPhone);

        nameAndKey = new HashMap<>();


/*        if (editDialog != null) {
            if (editDialog.equalsIgnoreCase("EDIT_DIALOG")) {
                tilPass.setVisibility(View.GONE);
                etPassword.setVisibility(View.GONE);
                etTicketQty.setText(cTicket);
                etUserName.setText(cName);
                etUserName.setEnabled(false);
                dialogTitle.setText("EDIT USER");
                addUser.setText("Edit User");
            }
        }*/

        ArrayAdapter<String> userAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, agentType);
        userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        agentTypeSpinner.setAdapter(userAdapter);

        agentTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                final String sel = agentTypeSpinner.getSelectedItem().toString();
                userType = sel;

                if (sel.equalsIgnoreCase("Seller")) {
                    shopLayout.setVisibility(View.VISIBLE);
                }else if (sel.equalsIgnoreCase("Buyer")) {
                    shopLayout.setVisibility(View.GONE);
                }

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

                // Store values at the time of the login attempt.
                String userName = etUserName.getText().toString();
                String email = etUserMail.getText().toString();
                String password = etPassword.getText().toString();

                String shopName = etShopName.getText().toString();
                String shopAddress = etShopAddress.getText().toString();
                String shopPhone = etShopPhone.getText().toString();

                int mTickAlloc = 0;
                boolean cancel = false;
                View focusView = null;


                if (TextUtils.isEmpty(userName)) {
                    etUserName.setError("Username is required");
                    focusView = etUserName;
                    cancel = true;
                }
                // Check for a valid password, if the user entered one.
                if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
                    etPassword.setError(getString(R.string.error_invalid_password));
                    focusView = etPassword;
                    cancel = true;
                }

                // Check for a valid email address.
                if (TextUtils.isEmpty(email)) {
                    etUserMail.setError(getString(R.string.error_field_required));
                    focusView = etUserMail;
                    cancel = true;
                }
                if (!isEmailValid(email)) {
                    etUserMail.setError(getString(R.string.error_invalid_email));
                    focusView = etUserMail;
                    cancel = true;
                }
                if (userType.equalsIgnoreCase("seller")) {
                    if (TextUtils.isEmpty(shopName)) {
                        etShopName.setError("Shop Name is required");
                        focusView = etShopName;
                        cancel = true;
                    }
                    if (TextUtils.isEmpty(shopAddress)) {
                        etShopAddress.setError("Shop Address is required");
                        focusView = etShopAddress;
                        cancel = true;
                    }
                    if (TextUtils.isEmpty(shopPhone)) {
                        etShopPhone.setError("Shop Phone number is required");
                        focusView = etShopPhone;
                        cancel = true;
                    }
                }
                if (cancel) {
                    // There was an error; don't attempt login and focus the first
                    // form field with an error.
                    focusView.requestFocus();
                } else {
                    String role = userRole(userType);
                    String activationId  = "0";
                    if (userType.equalsIgnoreCase("Buyer")) {
                        NewUserBuyerFragmentInteractionListener listener = (NewUserBuyerFragmentInteractionListener) getActivity();
                        listener.onUserBuyerButtonClick(email, password, userName, role, activationId);
                        dismiss();
                    } else if (userType.equalsIgnoreCase("Seller")) {
                        NewUserSellerFragmentInteractionListener listener = (NewUserSellerFragmentInteractionListener) getActivity();
                        listener.onUserSellerButtonClick(email, password, userName, role, activationId, shopName, shopAddress, shopPhone);
                        dismiss();
                    }
                }

        }
    }


    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    private String userRole(String role) {
        String rolex = null;
        if (role.equalsIgnoreCase("Buyer")) {
            rolex = "2";
        } else if (role.equalsIgnoreCase("Seller")) {
            rolex = "1";
        }
        return rolex;
    }

    private String extractUserName(String email) {
        String[] name = email.split("@");
        Log.e(TAG, "Splitted UserName = " + name[0]);
        return name[0];
    }

    public interface NewUserSellerFragmentInteractionListener {
        public void onUserSellerButtonClick(String email, String password, String name, String role, String activationId, String shopName,
                                          String shopAddr, String shopPhone);
    }

    public interface NewUserBuyerFragmentInteractionListener {
        public void onUserBuyerButtonClick(String email, String password, String name, String role, String activationId);
    }

}