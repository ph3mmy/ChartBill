package com.jcedar.tixee.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jcedar.tixee.R;
import com.jcedar.tixee.adapter.CampaignHolder;
import com.jcedar.tixee.adapter.PrimaryHolder;
import com.jcedar.tixee.helper.AddCampaignDialog;
import com.jcedar.tixee.helper.EditTicketDialog;
import com.jcedar.tixee.model.Campaign;
import com.jcedar.tixee.model.Ticket;
import com.jcedar.tixee.model.User;
import com.jcedar.tixee.model.UserCampaign;

/**
 * Created by OLUWAPHEMMY on 2/6/2017.
 */

public class PrimaryFragment extends Fragment implements AddCampaignDialog.AllocateCampaignFragmentInteractionListener {

    private static final String TAG = PrimaryFragment.class.getSimpleName();
    Context context;
    View view;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private TextView errorMsg;
    String mUser;
    private FirebaseAuth firebaseAuth;
    static int pryUserNoTicket, pryUserUsed;

    private FirebaseRecyclerAdapter<User, PrimaryHolder> mAdapter;
    static String pryUserCampaign;

    @Override
    public void onStart() {
        super.onStart();
    
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bb = getArguments();
        pryUserCampaign = bb.getString("pryCamName");
        pryUserNoTicket = bb.getInt("pryCamAva");
        pryUserUsed = bb.getInt("pryCamUsed");
        Log.e(TAG, "czm in alloc pry oncreate" + pryUserCampaign + "tickNoAva = " + pryUserNoTicket + " pryUserUsed " + pryUserUsed);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_primary, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            mUser = firebaseAuth.getCurrentUser().getUid();
        }
        recyclerView = (RecyclerView) view.findViewById(R.id.primaryTicketRecycler);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setHasFixedSize(true);

        errorMsg = (TextView) view.findViewById(R.id.errorMsg);

        FirebaseDatabase mFirebaseInstance = FirebaseDatabase.getInstance();
        DatabaseReference mFirebaseDatabase = mFirebaseInstance.getReference();

        Query query = mFirebaseDatabase.child("User").orderByChild("role").equalTo("2");
                
        mAdapter = new FirebaseRecyclerAdapter<User, PrimaryHolder>(
                User.class,
                R.layout.primary_list_row, PrimaryHolder.class, query
        ) {
            @Override
            protected void populateViewHolder(PrimaryHolder viewHolder, final User model, final int position) {


                viewHolder.campaignTitle.setText((model.getEmail()));
                viewHolder.availTicket.setText(model.getUserCampaign().getCampaignName());
                viewHolder.allocTicket.setText("" + model.getUserCampaign().getUserNumberOfTicket());
                viewHolder.usedTicket.setText("" + model.getUserCampaign().getUserUsedTicket());


                viewHolder.cdEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (pryUserCampaign.equalsIgnoreCase(model.getUserCampaign().getCampaignName())) {
                            editSecondaryTicket(position, model);
                        } else
                            Toast.makeText(getActivity(), "You cannot allocate Ticket in this campaign", Toast.LENGTH_SHORT).show();
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



        query.addListenerForSingleValueEvent(new ValueEventListener() {
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


        return view;
    }

    private String capitalizeFirstLetter (String str) {
        return str.toUpperCase().charAt(0) + str.substring(1);
    }



    private void editSecondaryTicket (int position, User model) {

        AddCampaignDialog mDialog = new AddCampaignDialog();
        Bundle b = new Bundle();

        String cName = model.getEmail();
        String cTicketType = String.valueOf(model.getUserCampaign().getUserNumberOfTicket());
        int pry = pryUserNoTicket - pryUserUsed;
        Log.e(TAG, "czm in alloc pry " + pryUserCampaign + "tickNoAva = " + pryUserNoTicket + " pryUserUsed " + pryUserUsed);
        String key = model.getUserID();
        b.putString("edit","ALLOC_DIALOG");
        b.putString("cName",cName);
        b.putString("cAv", String.valueOf(pry));
        b.putString("cTicket", cTicketType);
        b.putString("cKey", key);
        mDialog.setTargetFragment(PrimaryFragment.this, 0);
        mDialog.setArguments(b);
        mDialog.setCancelable(false);
        mDialog.show(getFragmentManager(), "allocate_ticket_dialog");

    }

    @Override
    public void onAllocateCampaignButtonClick(String key, String newTicketVal) {
        DatabaseReference mDb = FirebaseDatabase.getInstance().getReference("User");
        int mm = Integer.parseInt(newTicketVal);
        mDb.child(key).child("userCampaign").child("userNumberOfTicket").setValue(mm);
        mAdapter.notifyDataSetChanged();
    }
}
