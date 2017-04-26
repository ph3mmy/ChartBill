package com.jcedar.tixee.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.jcedar.tixee.R;

/**
 * Created by OLUWAPHEMMY on 2/14/2017.
 */

public class UserHolder extends RecyclerView.ViewHolder {

    public TextView userEmail, userRole;
    public CardView cdEdit, cdDelete;

    public UserHolder(View itemView) {
        super(itemView);

        userEmail = (TextView) itemView.findViewById(R.id.tvUserEmail);
        userRole = (TextView) itemView.findViewById(R.id.tvUserRole);
        cdEdit = (CardView) itemView.findViewById(R.id.cdUserEdit);
        cdDelete = (CardView) itemView.findViewById(R.id.cdUserDelete);

    }

}
