package com.jcedar.tixee.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jcedar.tixee.R;
import com.jcedar.tixee.adapter.CampaignHolder;
import com.jcedar.tixee.adapter.TicketHolder;
import com.jcedar.tixee.helper.AddCampaignDialog;
import com.jcedar.tixee.helper.EditTicketDialog;
import com.jcedar.tixee.helper.MyUtils;
import com.jcedar.tixee.model.Campaign;
import com.jcedar.tixee.model.Ticket;

/**
 * Created by OLUWAPHEMMY on 2/4/2017.
 */

public class TicketFragment extends Fragment implements EditTicketDialog.AdminEditTicketFragmentInteractionListener {

    private static final String TAG = TicketFragment.class.getSimpleName();
    Context context;
    View view;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private TextView errorMsg;
    DatabaseReference mDb;

    private FirebaseRecyclerAdapter<Ticket, TicketHolder> mAdapter;

    public TicketFragment () {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_ticket, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.ticketRecycler);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setHasFixedSize(true);

        errorMsg = (TextView) view.findViewById(R.id.errorMsg);

        FirebaseDatabase mFirebaseInstance = FirebaseDatabase.getInstance();
        DatabaseReference mFirebaseDatabase = mFirebaseInstance.getReference();


        mDb = FirebaseDatabase.getInstance().getReference("Ticket");

//        addCampaign.setOnClickListener(this);

        mAdapter = new FirebaseRecyclerAdapter<Ticket, TicketHolder>(
                Ticket.class,
                R.layout.admin_ticket_list_row, TicketHolder.class, mDb
        ) {
            @Override
            protected void populateViewHolder(TicketHolder viewHolder, final Ticket model, final int position) {


                viewHolder.campaignTitle.setText(capitalizeFirstLetter(model.getTicketId()));
                viewHolder.tcketId.setText(capitalizeFirstLetter(model.getCampaignName()));
                viewHolder.ticketStatus.setText(model.getStatus());
                viewHolder.ticketType.setText(model.getTicketType());
                viewHolder.ticketGen.setText(model.getGenBy());
                viewHolder.ticketGenDate.setText(model.getGenDate());

                viewHolder.cdDel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        deleteTicket(position);

                    }
                });

                viewHolder.cdEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editTicket(position, model);
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

    private void deleteTicket (int position) {
        DatabaseReference mDb = FirebaseDatabase.getInstance().getReference("Ticket");
        Ticket czm = mAdapter.getItem(position);
        Log.e(TAG, "czm in delete " + czm);
        String key = mDb.child(czm.getTicketId()).getKey();
        mDb.child(key).removeValue();
        String data = "TRUE";
        MyUtils.notifyActivity(data, getActivity());
        if (mAdapter != null) {
            mAdapter.notifyItemRemoved(position);
        }
        mAdapter.notifyDataSetChanged();
        Toast.makeText(getActivity(), "Ticket successfully deleted", Toast.LENGTH_SHORT).show();
    }

    private void editTicket (int position, Ticket model) {

        EditTicketDialog mDialog = new EditTicketDialog();
        Bundle b = new Bundle();
        Ticket czm = mAdapter.getItem(position);
        String cName = model.getTicketId();
        String cTicketType = model.getTicketType();

        Log.e(TAG, "czm in edit " + cName + "tickNo = " + cTicketType);
        String key = mDb.child(czm.getTicketId()).getKey();
        b.putString("edit","EDIT_DIALOG");
        b.putString("cName",cName);
        b.putString("cTicketType", cTicketType);
        b.putString("cKey", key);
        mDialog.setTargetFragment(TicketFragment.this, 0);
        mDialog.setArguments(b);
        mDialog.show(getFragmentManager(), "edit_ticket_dialog");

    }

    @Override
    public void onAdminEditTicketButtonClick(String key, String status, String ticketType, String conName, String conPhone) {
        mDb.child(key).child("status").setValue(status);
        mDb.child(key).child("ticketType").setValue(ticketType);
        mDb.child(key).child("contactName").setValue(conName);
        mDb.child(key).child("contactPhone").setValue(conPhone);
        mAdapter.notifyDataSetChanged();
    }
}
