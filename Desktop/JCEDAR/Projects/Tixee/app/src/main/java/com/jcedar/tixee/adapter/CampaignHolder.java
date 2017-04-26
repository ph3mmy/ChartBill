package com.jcedar.tixee.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jcedar.tixee.R;
import com.jcedar.tixee.model.Campaign;

import java.util.List;

public class CampaignHolder extends RecyclerView.ViewHolder {

    public TextView campaignTitle;
    public TextView campaignTicket, campPry, ticketLeft;
    public ImageView cdEdit, cdDel;

    public CampaignHolder(View itemView) {
        super(itemView);

        campaignTitle = (TextView) itemView.findViewById(R.id.tvCampaignTitle);
        campaignTicket = (TextView) itemView.findViewById(R.id.tvCampaignId);
        campPry = (TextView) itemView.findViewById(R.id.tvCampaignUsedId);
        ticketLeft = (TextView) itemView.findViewById(R.id.tvCampaignLeft);
        cdEdit = (ImageView) itemView.findViewById(R.id.cdEdit);
        cdDel = (ImageView) itemView.findViewById(R.id.cdDelete);

    }
}
