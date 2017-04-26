package com.jcedar.tixee.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jcedar.tixee.R;

/**
 * Created by OLUWAPHEMMY on 2/9/2017.
 */

public class TicketHolder extends RecyclerView.ViewHolder {
    public TextView campaignTitle;
    public TextView tcketId, ticketStatus, ticketType, ticketGen, ticketGenDate;
    public ImageView cdEdit, cdDel;

    public TicketHolder(View itemView) {
        super(itemView);

        campaignTitle = (TextView) itemView.findViewById(R.id.tvAdminTicketId);
        tcketId = (TextView) itemView.findViewById(R.id.tvAdminTicketCampTitle);
        ticketStatus = (TextView) itemView.findViewById(R.id.tvAdminTicketStatus);
        ticketType = (TextView) itemView.findViewById(R.id.tvAdminTicketType);
        ticketGen = (TextView) itemView.findViewById(R.id.tvAdminTicketGenBy);
        ticketGenDate = (TextView) itemView.findViewById(R.id.tvAdminDateGen);
        cdEdit = (ImageView) itemView.findViewById(R.id.ivAdminEdit);
        cdDel = (ImageView) itemView.findViewById(R.id.ivAdminDel);

    }
}
