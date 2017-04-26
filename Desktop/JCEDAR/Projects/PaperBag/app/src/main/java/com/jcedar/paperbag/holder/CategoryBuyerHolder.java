package com.jcedar.paperbag.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jcedar.paperbag.R;


public class CategoryBuyerHolder extends RecyclerView.ViewHolder {

    public TextView categTitle;
    public RelativeLayout imageLayout;
//    public ImageView delImg, coverImg;

    public CategoryBuyerHolder(View itemView) {
        super(itemView);

        categTitle = (TextView) itemView.findViewById(R.id.tvBuyerCategoryTitle);
        imageLayout = (RelativeLayout) itemView.findViewById(R.id.buyerCategoryRel);

    }
}
