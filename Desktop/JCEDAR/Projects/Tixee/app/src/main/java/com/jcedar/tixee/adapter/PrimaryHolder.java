package com.jcedar.tixee.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jcedar.tixee.R;

/**
 * Created by OLUWAPHEMMY on 2/7/2017.
 */

public class PrimaryHolder extends RecyclerView.ViewHolder {

    public TextView campaignTitle, availTicket, allocTicket, usedTicket;
    public ImageView cdEdit;

    public PrimaryHolder(View itemView) {
        super(itemView);

        campaignTitle = (TextView) itemView.findViewById(R.id.tvPryCampTitle);
        availTicket = (TextView) itemView.findViewById(R.id.tvPryAvailTickId);
        allocTicket = (TextView) itemView.findViewById(R.id.tvPryAllocTickId);
        usedTicket = (TextView) itemView.findViewById(R.id.tvPryUsedTickId);
        cdEdit = (ImageView) itemView.findViewById(R.id.imageEditPry);

    }
}
