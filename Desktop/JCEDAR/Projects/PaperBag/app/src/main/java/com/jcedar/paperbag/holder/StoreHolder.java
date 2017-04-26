package com.jcedar.paperbag.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jcedar.paperbag.R;


public class StoreHolder extends RecyclerView.ViewHolder {

    public TextView storeName, storeAddress, storePhone, storeUser;
    public ImageView storeDel;

    public StoreHolder(View itemView) {
        super(itemView);

        storeName = (TextView) itemView.findViewById(R.id.tvAdminRowStoreName);
        storeAddress = (TextView) itemView.findViewById(R.id.tvAdminRowStoreAddress);
        storePhone = (TextView) itemView.findViewById(R.id.tvAdminRowStorePhone);
        storeUser = (TextView) itemView.findViewById(R.id.tvAdminRowStoreOwner);
        storeDel = (ImageView) itemView.findViewById(R.id.ivDeleteStore);

    }
}
