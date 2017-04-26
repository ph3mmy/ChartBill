package com.jcedar.paperbag.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jcedar.paperbag.R;

import static com.jcedar.paperbag.R.id.allCategoryPhoto;


public class CategoryHolder extends RecyclerView.ViewHolder {

    public TextView categTitle, dateAdded, catgSize;
    public ImageView delImg, coverImg;

    public CategoryHolder(View itemView) {
        super(itemView);

        categTitle = (TextView) itemView.findViewById(R.id.tvAllCategoryTitle);
        dateAdded = (TextView) itemView.findViewById(R.id.tvAllCategoryDate);
        catgSize = (TextView) itemView.findViewById(R.id.tvAllCategoryProductSize);
        delImg = (ImageView) itemView.findViewById(R.id.ivAllCategorydelete);
        coverImg = (ImageView) itemView.findViewById(allCategoryPhoto);

    }
}
