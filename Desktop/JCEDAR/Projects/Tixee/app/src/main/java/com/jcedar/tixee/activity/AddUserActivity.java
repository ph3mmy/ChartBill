package com.jcedar.tixee.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
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
import com.jcedar.tixee.adapter.UserHolder;
import com.jcedar.tixee.fragment.PrimaryFragment;
import com.jcedar.tixee.helper.AddCampaignDialog;
import com.jcedar.tixee.helper.AddUserDialog;
import com.jcedar.tixee.helper.MyUtils;
import com.jcedar.tixee.model.Campaign;
import com.jcedar.tixee.model.User;
import com.jcedar.tixee.model.UserCampaign;

/**
 * Created by OLUWAPHEMMY on 2/9/2017.
 */
public class AddUserActivity extends AppCompatActivity implements AddUserDialog.EditUserFragmentInteractionListener,
        AddUserDialog.AddNewUserFragmentInteractionListener{

    private static final String TAG = AddUserActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private TextView errorMsg;
    int noOfAllocTicket, adminUsed, campAvaTicket;
    private DatabaseReference mFirebaseDatabase, uFirebaseDatabase;
    private FirebaseAuth mFirebaseAuth;
    FirebaseUser mUser;
    String uName;

    private FirebaseRecyclerAdapter<User, UserHolder> mAdapter;
    private FloatingActionButton addUserFab;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();

        if (toolbar != null) {
            toolbar.setTitle("Registered Users");
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }


        recyclerView = (RecyclerView) findViewById(R.id.userRecycler);

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);

        addUserFab = (FloatingActionButton)findViewById(R.id.addUserFab);
        errorMsg = (TextView)findViewById(R.id.errorMsg);

        FirebaseDatabase mFirebaseInstance = FirebaseDatabase.getInstance();
        uFirebaseDatabase = mFirebaseInstance.getReference("User");
        mFirebaseDatabase.keepSynced(true);

        mAdapter = new FirebaseRecyclerAdapter<User, UserHolder>(
                User.class, R.layout.users_list_row, UserHolder.class, uFirebaseDatabase
        ) {
            @Override
            protected void populateViewHolder(UserHolder viewHolder, final User model, final int position) {

                viewHolder.userEmail.setText(model.getEmail());
                viewHolder.userRole.setText(capitalizeFirstLetter(setUserRole(model.getRole())));
                viewHolder.cdEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editUser(position, model);
                        uName = model.getName();
                    }
                });

                viewHolder.cdDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteUser(position, model);
                    }
                });

            }
        };


        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int jokeCount = mAdapter.getItemCount();
                int lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the user is at the bottom of the list, scroll
                // to the bottom of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (jokeCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                    recyclerView.scrollToPosition(positionStart);
                }
            }
        });

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(mAdapter);



        uFirebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChildren()) {
                    errorMsg.setVisibility(View.VISIBLE);
                } else
                    errorMsg.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        RecyclerView.AdapterDataObserver mObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                if (itemCount == 0) {
                    errorMsg.setVisibility(View.VISIBLE);
                } else
                    errorMsg.setVisibility(View.GONE);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                if (itemCount == 0) {
                    errorMsg.setVisibility(View.VISIBLE);
                } else
                    errorMsg.setVisibility(View.GONE);
            }

        };

        mAdapter.registerAdapterDataObserver(mObserver);



        addUserFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AddUserDialog mDialog = new AddUserDialog();
                Bundle bb = new Bundle();
                bb.putString("user", "ADD_DIALOG");
                mDialog.setArguments(bb);
                mDialog.show(getSupportFragmentManager(), "user_dialog");

            }
        });

    }


    private String capitalizeFirstLetter (String str) {
        return str.toUpperCase().charAt(0) + str.substring(1);
    }

    private void editUser (int position, User model) {

        AddUserDialog mDialog = new AddUserDialog();
        Bundle b = new Bundle();
        DatabaseReference mDb = FirebaseDatabase.getInstance().getReference("User");
        User czm = mAdapter.getItem(position);
        String uId = czm.getUserID();
        String cName = model.getEmail();

//        DatabaseReference mDbCamp = FirebaseDatabase.getInstance().getReference("UserCampaign");
        mDb.child(uId).child("userCampaign").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                noOfAllocTicket = dataSnapshot.child("userNumberOfTicket").getValue(Integer.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Log.e(TAG, "czm in alloc pry " + cName + "tickNo  Aloocataed User = " + noOfAllocTicket);
        b.putString("user","EDIT_DIALOG");
        b.putString("uid", uId);
        b.putString("cName",cName);
        b.putString("cTicket", String.valueOf(noOfAllocTicket));
        mDialog.setArguments(b);
        mDialog.show(getSupportFragmentManager(), "allocate_user_dialog");

    }

    private String setUserRole (String userRole) {
        String rolez = "";
        if (userRole.equalsIgnoreCase("1")) {
            rolez = "Primary Agent";
        } else if (userRole.equalsIgnoreCase("2")) {
            rolez = "Secondary Agent";
        }
        return rolez;
    }

    private void deleteUser (int position, User model) {
//        DatabaseReference mDb = FirebaseDatabase.getInstance().getReference("User");

        DatabaseReference mDb = FirebaseDatabase.getInstance().getReference("User");
        User czm = mAdapter.getItem(position);
        String uId = czm.getUserID();
        String cName = model.getEmail();

//        User czm = mAdapter.getItem(position);
        Log.e(TAG, "czm in delete " + czm);
//        String key = mDb.child(czm.getUserID()).;
//        String uId = czm.getUserID();
        String mm = model.getUserID();
//        String key = mDb.child(model.getUserID()).toString();
        Log.e(TAG, "model key in delete " + uId +  "mm " + mm);
        mDb.child(uId).removeValue();
//        mAdapter.notifyItemRemoved(position);
        mAdapter.notifyDataSetChanged();
    }



    @Override
    public void onEditUserButtonClick(String key, String agentType, String NewCampaign, String campId, int noOfTicket, final int camUsed, final int camLeft) {
        DatabaseReference editUserRef = FirebaseDatabase.getInstance().getReference("User");
        editUserRef.child(key).child("role").setValue(agentType);
        editUserRef.child(key).child("userCampaign").child("userNumberOfTicket").setValue(noOfTicket);
        editUserRef.child(key).child("userCampaign").child("userUsedTicket").setValue(0);
        editUserRef.child(key).child("userCampaign").child("campaignName").setValue(NewCampaign);
        editUserRef.child(key).child("userCampaign").child("campaignId").setValue(campId);

        DatabaseReference camRef = mFirebaseDatabase.child("Campaign").child(campId);
        camRef.child("adminUsed").setValue(camUsed);
        camRef.child("ticketLeft").setValue(camLeft);
        Toast.makeText(this, " User  "  +  uName  + " successfully edited" , Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onAddUserButtonClick(final String email, String password, final String name, final String role, final String campaignName,
                                     final int allocTicket, final String camId, final int camUsed, final int camLeft) {
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
                            UserCampaign userCamp = new UserCampaign(campaignName, camId,allocTicket, 0);
                            User user = new User(uuid, name, email, role, userCamp);
                            userRef.child(uuid).setValue(user);

                            DatabaseReference camRef = mFirebaseDatabase.child("Campaign").child(camId);
                            camRef.child("adminUsed").setValue(camUsed);
                            camRef.child("ticketLeft").setValue(camLeft);

                            Toast.makeText(AddUserActivity.this, "New user " + name + " added successfully", Toast.LENGTH_SHORT).show();

                            mFirebaseAuth.signOut();
                            MyUtils.removePersonKey(AddUserActivity.this);
                            MyUtils.loadLogInView(AddUserActivity.this);


/*                    DatabaseReference userCampRef = userRef.child("UserCampaign");

                    DatabaseReference db_user_ref = userCampRef.push();
                    Log.e(TAG, "the gotten key " + userId);
                    userCampRef.child(uuid).setValue(userCamp);*/

                        }
                    }
                });
    }
}
