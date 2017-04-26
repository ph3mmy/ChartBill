package com.jcedar.paperbag.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jcedar.paperbag.R;

import static com.jcedar.paperbag.R.id.adminUserDelete;
import static com.jcedar.paperbag.R.id.ivDisapprove;


public class NewUserHolder extends RecyclerView.ViewHolder {

    public TextView userName, userEmail, userRole;
    public ImageView imgApprove, imgDisapprove, delUser;
    public LinearLayout mLayout, adminDelUserLayout;

    public NewUserHolder(View itemView) {
        super(itemView);

        userName = (TextView) itemView.findViewById(R.id.tvAdminRowNewUser);
        userEmail = (TextView) itemView.findViewById(R.id.tvAdminRowNewUserEmail);
        userRole = (TextView) itemView.findViewById(R.id.tvAdminRowNewUserRole);
        imgApprove = (ImageView) itemView.findViewById(R.id.ivApprove);
        imgDisapprove = (ImageView) itemView.findViewById(ivDisapprove);
        delUser = (ImageView) itemView.findViewById(adminUserDelete);
        adminDelUserLayout = (LinearLayout) itemView.findViewById(R.id.adminUserDeleteLayout);
        mLayout = (LinearLayout) itemView.findViewById(R.id.adminActionLayout);

    }
}
