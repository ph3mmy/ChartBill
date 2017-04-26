package com.jcedar.paperbag.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.jcedar.paperbag.R;
import com.jcedar.paperbag.dialog.SignupDialog;
import com.jcedar.paperbag.helper.MyUtils;
import com.jcedar.paperbag.model.Stores;
import com.jcedar.paperbag.model.User;

import java.util.Arrays;
import java.util.List;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements SignupDialog.NewUserSellerFragmentInteractionListener,
        SignupDialog.NewUserBuyerFragmentInteractionListener {

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mFirebaseDatabase;

    private static final String TAG = LoginActivity.class.getSimpleName();

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    String[] userType = new String[]{"admin@paperbag.com"};

    // UI references.
    private AutoCompleteTextView mEmailView;
    private TextInputEditText mPasswordView;
    private View mProgressView;
    private LinearLayout progressLayout;
    private View mLoginFormView;
    private TextView signingIn;
    static String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


/*        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Tixee - Sign In");
        }*/

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();


        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (TextInputEditText) findViewById(R.id.password);
        mLoginFormView = findViewById(R.id.login_form);
        progressLayout = (LinearLayout) findViewById(R.id.progressLayout);
        mProgressView = findViewById(R.id.login_progress);
        signingIn = (TextView) findViewById(R.id.tvSigningIn);
        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        Button signupButton = (Button) findViewById(R.id.sign_up_button);

        signupButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyUtils.checkNetworkAvailability(LoginActivity.this)) {
                    showSignUpDialog();
                } else {
                    MyUtils.networkDialog(LoginActivity.this).show();
                }
            }
        });

        mEmailView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                addEmailsToAutoComplete();
            }
        });

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

    }

    private void showSignUpDialog() {

        SignupDialog dialog = new SignupDialog();
        FragmentManager manager = getSupportFragmentManager();
        dialog.setCancelable(false);
        dialog.show(manager, "sign_up");

    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        final String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            hideSoftKeyboard();
/*            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);*/

            loginUser(email, password);
/*
            mFirebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                FirebaseUser mUser = task.getResult().getUser();
                                String uuid = mUser.getUid();
                                Log.e("uuid", " uuid in Login = " + uuid);

                                new UserLoginTask(uuid).execute();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage(task.getException().getMessage())
                                        .setTitle(R.string.login_error_title)
                                        .setPositiveButton(android.R.string.ok, null);
                                AlertDialog dialog = builder.create();
                                dialog.show();
                                returnLoginView();
                            }
                        }
                    });*/

        }
    }

    private void loginUser(String email, String password) {

        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser mUser = task.getResult().getUser();
                            String uuid = mUser.getUid();
                            String uMail = mUser.getEmail();
                            Log.e("uuid", " uuid in Login = " + uuid);

                            new UserLoginTask(uuid, uMail).execute();
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            builder.setMessage(task.getException().getMessage())
                                    .setTitle(R.string.login_error_title)
                                    .setPositiveButton(android.R.string.ok, null);
                            AlertDialog dialog = builder.create();
                            dialog.show();
                            returnLoginView();
                        }
                    }
                });
    }

    private void returnLoginView() {
        showProgress(false);
        mLoginFormView.setVisibility(View.VISIBLE);
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 6;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressLayout.setVisibility(show ? View.VISIBLE : View.GONE);
//            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            signingIn.setVisibility(show ? View.VISIBLE : View.GONE);
            progressLayout.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressLayout.setVisibility(show ? View.VISIBLE : View.GONE);
                    signingIn.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });/*            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                    signingIn.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });*/
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressLayout.setVisibility(show ? View.VISIBLE : View.GONE);
            signingIn.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            /*            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            signingIn.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);*/
        }
    }

    private void hideSoftKeyboard() {
        View v = this.getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }




    private void addEmailsToAutoComplete() {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        List<String> email = Arrays.asList(userType);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, email);

        mEmailView.setAdapter(adapter);
    }

    /**
     * call back for seller info
     */
    @Override
    public void onUserSellerButtonClick(final String email, final String password, final String name, final String role,
                                        final String activationId, final String shopName, final String shopAddr, final String shopPhone) {

        final ProgressDialog mmD = myDialog(LoginActivity.this);
        mmD.show();
        mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.e(TAG, "inside create fireUser ");
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = task.getResult().getUser();
                            String uuid = firebaseUser.getUid();
                            Log.e(TAG, "firebase user " + firebaseUser + "uuid = " + uuid);
                            DatabaseReference userRef = mFirebaseDatabase.child("User");

                            DatabaseReference db_ref = userRef.push();
                            String userId = userRef.push().getKey();
                            Log.e(TAG, "the gotten key " + userId);
                            User user = new User(uuid, name, email, role, activationId);
                            userRef.child(uuid).setValue(user);

                            DatabaseReference shopRef = mFirebaseDatabase.child("Stores");
                            Stores newStore = new Stores(uuid, shopName, shopAddr, shopPhone, uuid, name,email, "token");
                            shopRef.child(uuid).setValue(newStore);

                            Toast.makeText(LoginActivity.this, "Account successfully Created", Toast.LENGTH_SHORT).show();
//                            myDialog(LoginActivity.this).dismiss();
                            mmD.hide();
                            mmD.dismiss();
                            mFirebaseAuth.signOut();
                            showProgress(true);
                            loginUser(email, password);
                        }
                    }
                });
    }

    @Override
    public void onUserBuyerButtonClick(final String email, final String password, final String name, final String role, final String activationId) {

        final ProgressDialog mdial = myDialog(LoginActivity.this);
        mdial.show();
        mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.e(TAG, "inside buyer create fireUser ");
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = task.getResult().getUser();
                            String uuid = firebaseUser.getUid();
                            Log.e(TAG, "firebase buyer user " + firebaseUser + "uuid = " + uuid);
                            DatabaseReference userRef = mFirebaseDatabase.child("User");

                            DatabaseReference db_ref = userRef.push();
                            String userId = userRef.push().getKey();
                            Log.e(TAG, "the gotten key " + userId);
                            User user = new User(uuid, name, email, role, activationId);
                            userRef.child(uuid).setValue(user);
                            Toast.makeText(LoginActivity.this, "Account successfully Created", Toast.LENGTH_SHORT).show();
                            mdial.hide();
                            mdial.dismiss();

                            mFirebaseAuth.signOut();
                            showProgress(true);
                            loginUser(email, password);

                        }
                    }
                });
    }

    private ProgressDialog myDialog (Context context) {
        ProgressDialog cLog = new ProgressDialog(context);
        cLog.setTitle("Please wait..");
        cLog.setMessage("Signing Up...");
        cLog.setCancelable(false);

        return cLog;
    }



    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<String, Void, String> {

        private String uuid;
        String roles;
        String activationId, userString;
        FirebaseUser user;
//        private final String mPassword;

        UserLoginTask(String uuid, String mail) {
            this.uuid = uuid;
//            mPassword = password;
            this.userString = mail;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("User");
            mRef.child(uuid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.e("TAG", "user role  inside LoGIN async " + dataSnapshot);
                    if (dataSnapshot != null) {
                        roles = dataSnapshot.child("role").getValue(String.class);
                        activationId = dataSnapshot.child("activationId").getValue(String.class);
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        @Override
        protected String doInBackground(String... strings) {

            String token = FirebaseInstanceId.getInstance().getToken();
            int i = 0;
            do {

                Log.e("LOGIN", "waste some time " + i);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ++i;
            }
            while (i < 6);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "run: im wasting time " );
                }
            });

            Log.e(TAG, "doInBackground: returned token = "+ token );
            if (!userString.contains("admin") && (roles.equalsIgnoreCase("1"))){
                DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
                mRef.child("Stores").child(uuid).child("storeSellerToken").setValue(token);
            }

            return roles;
        }

        @Override
        protected void onPostExecute(final String success) {/*
            mAuthTask = null;
            showProgress(false);*/

            showProgress(false);
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            Log.e("TAG", "user role 1 " + roles + " success " + success);
            intent.putExtra("role", roles);
            intent.putExtra("uuid", uuid);
            intent.putExtra("activation", activationId);
            MyUtils.setPersonKey(LoginActivity.this, roles);
            MyUtils.setActivationId(LoginActivity.this, activationId);
            startActivity(intent);

        }

    }
}

