package com.jcedar.paperbag.holder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jcedar.paperbag.R;


public class ProductHolder extends RecyclerView.ViewHolder {

    public RelativeLayout imgPreview;
    public TextView productName, orderedCount, productPrice, forAdminDate, forAdminStore;
    public LinearLayout forAdminLayout;
    public ImageView delImg, imgPrev;
    public CardView sellerListItem;

    public ProductHolder(View itemView) {
        super(itemView);

        productName = (TextView) itemView.findViewById(R.id.tvItemName);
        orderedCount = (TextView) itemView.findViewById(R.id.tvItemCount);
        productPrice = (TextView) itemView.findViewById(R.id.tvItemPrice);
        forAdminDate = (TextView) itemView.findViewById(R.id.tvForAdminDate);
        forAdminStore = (TextView) itemView.findViewById(R.id.tvForAdminStore);
        forAdminLayout = (LinearLayout) itemView.findViewById(R.id.layoutForAdminDateAndStore);
        sellerListItem = (CardView) itemView.findViewById(R.id.cdSellerListItem);
        delImg = (ImageView) itemView.findViewById(R.id.imgItemDel);
        imgPrev = (ImageView) itemView.findViewById(R.id.imgPreview);
        imgPreview = (RelativeLayout) itemView.findViewById(R.id.imgView);

    }
}
