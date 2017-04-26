package com.jcedar.tixee.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jcedar.tixee.R;
import com.jcedar.tixee.adapter.CampaignAdapter;
import com.jcedar.tixee.adapter.CampaignHolder;
import com.jcedar.tixee.helper.AddCampaignDialog;
import com.jcedar.tixee.helper.MyUtils;
import com.jcedar.tixee.model.Campaign;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by OLUWAPHEMMY on 2/4/2017.
 */

public class CampaignFragment extends Fragment implements View.OnClickListener, AddCampaignDialog.EditCampaignFragmentInteractionListener {


    private static final String TAG = CampaignFragment.class.getSimpleName();
    ListView listView;
    FloatingActionButton addCampaign;
    TextView errorMsg;
    DatabaseReference mDb;

    private String userId;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    private FirebaseRecyclerAdapter<Campaign, CampaignHolder> mAdapter;

    public CampaignFragment () {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_campaign, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.campaignRecycler);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setHasFixedSize(true);



        addCampaign = (FloatingActionButton) view.findViewById(R.id.plusFab);
        errorMsg = (TextView) view.findViewById(R.id.errorMsg);

//        allCampaigns = new ArrayList<>();

        FirebaseDatabase mFirebaseInstance = FirebaseDatabase.getInstance();
        DatabaseReference mFirebaseDatabase = mFirebaseInstance.getReference();
        mDb = mFirebaseDatabase.child("Campaign");
        addCampaign.setOnClickListener(this);

        mAdapter = new FirebaseRecyclerAdapter<Campaign, CampaignHolder>(
                Campaign.class,
                R.layout.list_item_row, CampaignHolder.class, mDb
        ) {
            @Override
            protected void populateViewHolder(CampaignHolder viewHolder, final Campaign model, final int position) {


                viewHolder.campaignTitle.setText(capitalizeFirstLetter(model.getName()));
                viewHolder.campaignTicket.setText("" + model.getNumberOfTickets());
                viewHolder.campPry.setText("" + model.getAdminUsed());
                viewHolder.ticketLeft.setText("" + model.getTicketLeft());

                viewHolder.cdDel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        deleteCampaign(position, model);

                    }
                });

                viewHolder.cdEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editCampaignTicket(position, model);
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



        mDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChildren()) {
                    errorMsg.setVisibility(View.VISIBLE);
                } else {
                    errorMsg.setVisibility(View.GONE);
                }
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.plusFab:
                addNewCampaign();
        }
    }

    private String capitalizeFirstLetter (String str) {
        return str.toUpperCase().charAt(0) + str.substring(1);
    }

    private void addNewCampaign() {
        AddCampaignDialog mDialog = new AddCampaignDialog();
        Bundle bb = new Bundle();
        bb.putString("edit", "ADD_DIALOG");
        mDialog.setArguments(bb);
        mDialog.show(getFragmentManager(), "entree_dialog");
    }


    private void deleteCampaign (int position, Campaign model) {
        DatabaseReference mDb = FirebaseDatabase.getInstance().getReference("Campaign");
        Campaign czm = mAdapter.getItem(position);
        String key = mDb.child(czm.getId()).getKey();
        final String mm = model.getId();

        mDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot.child(mm).getRef().removeValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Log.e(TAG, "czm key in delete " + key + " mm del " + mm);
        String data = "TRUE";
        notifyActivity(data, getActivity());
        if (mAdapter != null) {
            mAdapter.notifyItemRemoved(position);
        }
        mAdapter.notifyDataSetChanged();
        Toast.makeText(getActivity(), "Campaign successfully deleted", Toast.LENGTH_SHORT).show();
    }

    static void notifyActivity(String data, Context mContext){
        Intent intent = new Intent("my-event");
        intent.putExtra("message", data);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

    private void editCampaignTicket (int position, Campaign model) {

        AddCampaignDialog mDialog = new AddCampaignDialog();
        Bundle b = new Bundle();
        DatabaseReference mDb = FirebaseDatabase.getInstance().getReference("Campaign");
        Campaign czm = mAdapter.getItem(position);
        String cName = capitalizeFirstLetter(model.getName());
        String cNoTicket = String.valueOf(model.getNumberOfTickets());
        Log.e(TAG, "czm in edit " + cName + "tickNo = " + cNoTicket);
        String key = mDb.child(czm.getId()).getKey();
        b.putString("edit","EDIT_DIALOG");
        b.putString("cName",cName);
        b.putString("cTicket", cNoTicket);
        b.putString("cKey", key);
        mDialog.setTargetFragment(CampaignFragment.this, 0);
        mDialog.setArguments(b);
        mDialog.setCancelable(false);
        mDialog.show(getFragmentManager(), "entree_dialog");

    }

    @Override
    public void onEditCampaignButtonClick(String key, String newTicketVal) {
        DatabaseReference mDb = FirebaseDatabase.getInstance().getReference("Campaign");
        int mm = Integer.parseInt(newTicketVal);
        //TODO MODIFY THIS TRUNK
        mDb.child(key).child("numberOfTickets").setValue(mm);
        mAdapter.notifyDataSetChanged();
    }
}
