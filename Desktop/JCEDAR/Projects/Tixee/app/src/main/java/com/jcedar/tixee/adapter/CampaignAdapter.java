package com.jcedar.tixee.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.jcedar.tixee.R;
import com.jcedar.tixee.model.Campaign;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by OLUWAPHEMMY on 2/4/2017.
 */

public class CampaignAdapter extends RecyclerView.Adapter<CampaignHolder> {

    private static final String TAG = CampaignAdapter.class.getSimpleName();
    private List<Campaign> campaigns;
    protected Context context;

    public CampaignAdapter (Context context, List<Campaign> campaigns) {
        this.context = context;
        this.campaigns = campaigns;
    }


    @Override
    public CampaignHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CampaignHolder viewHolder = null;
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_row, parent, false);
        viewHolder = new CampaignHolder(layoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CampaignHolder holder, int position) {

        holder.campaignTitle.setText(campaigns.get(position).getName());
        holder.campaignTicket.setText(campaigns.get(position).getNumberOfTickets());

    }

    @Override
    public int getItemCount() {
        return this.campaigns.size();
    }
}

